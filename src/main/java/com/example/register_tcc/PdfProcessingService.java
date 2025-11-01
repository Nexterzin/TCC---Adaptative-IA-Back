package com.example.register_tcc;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.client.RestTemplate; 
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;

import java.util.Locale;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PdfProcessingService {

    private final RestTemplate restTemplate; 

    public PdfProcessingService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String processarPdfEChamarIA(MultipartFile arquivo) throws IOException {
        String textoCompleto;
        try (PDDocument document = PDDocument.load(arquivo.getInputStream())) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            textoCompleto = pdfStripper.getText(document).toLowerCase();
        }

        int gravidez = extrairInteiro(textoCompleto, "gravidez");
        double glicose = extrairDecimal(textoCompleto, "glicose");
        double imc = extrairDecimal(textoCompleto, "imc");
        int idade = extrairInteiro(textoCompleto, "idade");

        return chamarApiDeIA(gravidez, glicose, imc, idade);
    }
    
    private String chamarApiDeIA(int gravidez, double glicose, double imc, int idade) {
        
        String urlApi = "https://iapythontcc-production.up.railway.app/predict";

        IaInputData dadosIA = new IaInputData(gravidez, glicose, imc, idade);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<IaInputData> entity = new HttpEntity<>(dadosIA, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                urlApi, 
                entity, 
                String.class
            );
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else {
                throw new RuntimeException("A API de IA retornou o status: " + response.getStatusCode() + ". Corpo: " + response.getBody());
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao se comunicar com a API de IA: " + e.getMessage(), e);
        }
    }
    
    private int extrairInteiro(String texto, String rotulo) {
        Pattern pattern = Pattern.compile(rotulo + "\\s*[:\\-]?\\s*(\\d+)");
        Matcher matcher = pattern.matcher(texto);
        
        if (matcher.find()) {
            try {
                return Integer.parseInt(matcher.group(1));
            } catch (NumberFormatException e) {
                return 0; 
            }
        }
        return 0;
    }
    
    private double extrairDecimal(String texto, String rotulo) {
        Pattern pattern = Pattern.compile(rotulo + "\\s*[:\\-]?\\s*(\\d+([.,]\\d+)?)");
        Matcher matcher = pattern.matcher(texto);
        
        if (matcher.find()) {
            try {
                return Double.parseDouble(matcher.group(1).replace(',', '.'));
            } catch (NumberFormatException e) {
                return 0.0;
            }
        }
        return 0.0;
    }
}