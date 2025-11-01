package com.example.register_tcc;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/api/documentos") 
public class DocumentoController {
    
    @GetMapping("/orientacao-alto-risco")
    public RedirectView downloadAltoRisco() {
        return new RedirectView("/documentos/Diabetes_Alta.pdf");
    }

    @GetMapping("/orientacao-medio-risco")
    public RedirectView downloadMedioRisco() {
        return new RedirectView("/documentos/Diabetes_Moderada_Baixa.pdf");
    }
}