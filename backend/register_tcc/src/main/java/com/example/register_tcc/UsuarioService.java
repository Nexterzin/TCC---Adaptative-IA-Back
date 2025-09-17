package com.example.register_tcc;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final EmailService emailService;
    private final PasswordResetTokenRepository passwordResetTokenRepository; // 👈 precisa injetar isso

    public UsuarioService(
        UsuarioRepository usuarioRepository, 
        EmailService emailService,
        PasswordResetTokenRepository passwordResetTokenRepository
    ) {
        this.usuarioRepository = usuarioRepository;
        this.emailService = emailService;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
    }

    public Usuario registrarUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }
    
    public Optional<Usuario> login(String email, String senha) {
        return usuarioRepository.findByEmail(email)
                .filter(u -> u.getSenha().equals(senha));
    }

    // 👇 Esse método tem que estar aqui dentro
    public boolean iniciarRecuperacaoSenha(String email) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

        if (usuarioOpt.isEmpty()) {
            return false;
        }

        Usuario usuario = usuarioOpt.get();

        // gera token
        String token = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(30);

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUsuario(usuario);
        resetToken.setExpiryDate(expiryDate);

        passwordResetTokenRepository.save(resetToken);

        // envia link por e-mail
        String resetLink = "http://localhost:3000/reset-password?token=" + token;
        emailService.enviarEmail(
            usuario.getEmail(),
            "Recuperação de Senha",
            "Clique no link para redefinir sua senha: " + resetLink
        );

        return true;
    }
    public boolean resetarSenha(String token, String novaSenha) {
        Optional<PasswordResetToken> tokenOpt = passwordResetTokenRepository.findByToken(token);

        if (tokenOpt.isEmpty()) {
            return false; // token inválido
        }

        PasswordResetToken resetToken = tokenOpt.get();

        // verifica validade do token
        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return false; // token expirado
        }

        Usuario usuario = resetToken.getUsuario();
        usuario.setSenha(novaSenha); // altera senha
        usuarioRepository.save(usuario);

        // opcional: deletar o token após uso
        passwordResetTokenRepository.delete(resetToken);

        return true;
    }

}
