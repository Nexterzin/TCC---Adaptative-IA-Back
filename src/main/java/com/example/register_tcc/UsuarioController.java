package com.example.register_tcc;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    private final PdfProcessingService pdfProcessingService; 

    public UsuarioController(UsuarioService usuarioService, PdfProcessingService pdfProcessingService) {
        this.usuarioService = usuarioService;
        this.pdfProcessingService = pdfProcessingService;
    }


    @PostMapping("/registrar")
    public ResponseEntity<?> registrar(@RequestBody Usuario usuario) {
        try {
            Usuario novoUsuario = usuarioService.registrarUsuario(usuario);
            return ResponseEntity.ok(novoUsuario);
        } catch (RuntimeException e) {
            return ResponseEntity
                .status(400)
                .body(Map.of("message", e.getMessage()));
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Usuario usuario) {
        return usuarioService.login(usuario.getEmail(), usuario.getSenha())
                .map(u -> ResponseEntity.ok("Login realizado com sucesso"))
                .orElse(ResponseEntity.status(401).body("Email ou senha inválidos"));
    }

    @PostMapping("/recuperar-senha")
    public ResponseEntity<Map<String,String>> recuperarSenha(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "E-mail é obrigatório."));
        }

        boolean sucesso = usuarioService.iniciarRecuperacaoSenha(email);

        if (sucesso) {
            return ResponseEntity.ok(Map.of("message", "E-mail de recuperação enviado."));
        } else {
            return ResponseEntity.status(404).body(Map.of("message", "E-mail não encontrado."));
        }
    }

    public static class ResetSenhaRequest {
        private String token;
        private String novaSenha;
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
        public String getNovaSenha() { return novaSenha; }
        public void setNovaSenha(String novaSenha) { this.novaSenha = novaSenha; }
    }

    @PostMapping("/resetar-senha")
    public ResponseEntity<String> resetarSenha(@RequestBody ResetSenhaRequest request) {
        boolean sucesso = usuarioService.resetarSenha(request.getToken(), request.getNovaSenha());
        if (sucesso) {
            return ResponseEntity.ok("Senha redefinida com sucesso.");
        } else {
            return ResponseEntity.status(400).body("Token inválido ou expirado.");
        }
    }
    
    @PostMapping("/analisar-laudo")
    public ResponseEntity<?> analisarLaudo(@RequestParam("arquivo") MultipartFile arquivo) {
        if (arquivo.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "O arquivo não pode ser vazio."));
        }

        try {
            String resultadoIA = pdfProcessingService.processarPdfEChamarIA(arquivo);
            
            return ResponseEntity.ok(resultadoIA);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Erro ao ler o arquivo PDF."));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Ocorreu um erro interno no servidor: " + e.getMessage()));
        }
    }

}
