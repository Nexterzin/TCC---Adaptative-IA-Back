package com.example.register_tcc;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);

    // método novo
    Optional<PasswordResetToken> findByUsuarioId(Long usuarioId);
}
