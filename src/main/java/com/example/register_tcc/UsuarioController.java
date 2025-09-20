package com.example.register_tcc;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/registrar")
    public Usuario registrar(@RequestBody Usuario usuario) {
        return usuarioService.registrarUsuario(usuario);
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Usuario usuario) {
        return usuarioService.login(usuario.getEmail(), usuario.getSenha())
                .map(u -> ResponseEntity.ok("Login realizado com sucesso"))
                .orElse(ResponseEntity.status(401).body("Email ou senha inválidos"));
    }

    @PostMapping("/recuperar-senha")
    public ResponseEntity<String> recuperarSenha(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body("E-mail é obrigatório.");
        }

        boolean sucesso = usuarioService.iniciarRecuperacaoSenha(email);

        if (sucesso) {
            return ResponseEntity.ok("E-mail de recuperação enviado.");
        } else {
            return ResponseEntity.status(404).body("E-mail não encontrado.");
        }
    }
 // DTO para receber token + nova senha
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

}