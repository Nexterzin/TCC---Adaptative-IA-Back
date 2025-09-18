package com.example.register_tcc;

import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class PasswordResetTokenService {

    private final PasswordResetTokenRepository repository;

    public PasswordResetTokenService(PasswordResetTokenRepository repository) {
        this.repository = repository;
    }

    public void criarOuAtualizarToken(Usuario usuario, String novoToken, LocalDateTime novaData) {
        PasswordResetToken token = repository.findByUsuarioId(usuario.getId()).orElse(null);

        if (token != null) {
            // atualiza o token existente
            token.setToken(novoToken);
            token.setExpiryDate(novaData);
            repository.save(token);
        } else {
            // cria um novo token
            repository.save(new PasswordResetToken(novoToken, usuario, novaData));
        }
    }
}
