package com.example.register_tcc;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final EmailService emailService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordResetTokenService passwordResetTokenService;

    public UsuarioService(
        UsuarioRepository usuarioRepository,
        EmailService emailService,
        PasswordResetTokenRepository passwordResetTokenRepository,
        PasswordResetTokenService passwordResetTokenService
    ) {
        this.usuarioRepository = usuarioRepository;
        this.emailService = emailService;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.passwordResetTokenService = passwordResetTokenService;
    }

    public Usuario registrarUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public Optional<Usuario> login(String email, String senha) {
        return usuarioRepository.findByEmail(email)
            .filter(u -> u.getSenha().equals(senha));
    }

    @Transactional
    public boolean iniciarRecuperacaoSenha(String email) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
        if (usuarioOpt.isEmpty()) {
            return false; // email não encontrado
        }

        Usuario usuario = usuarioOpt.get();

        try {
            String token = UUID.randomUUID().toString();
            LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(30);

            // 👉 agora cria ou atualiza em vez de sempre tentar inserir
            passwordResetTokenService.criarOuAtualizarToken(usuario, token, expiryDate);

            String resetLink = "https://tccadaptative-my1u77npm-nexterzins-projects.vercel.app/reset-password?token=" + token;
            emailService.enviarEmail(
                usuario.getEmail(),
                "Recuperação de Senha",
                "Clique no link para redefinir sua senha: " + resetLink
            );

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false; // evita 500
        }
    }

    @Transactional
    public boolean resetarSenha(String token, String novaSenha) {
        Optional<PasswordResetToken> tokenOpt = passwordResetTokenRepository.findByToken(token);
        if (tokenOpt.isEmpty()) {
            return false;
        }

        PasswordResetToken resetToken = tokenOpt.get();

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return false;
        }

        Usuario usuario = resetToken.getUsuario();
        usuario.setSenha(novaSenha);
        usuarioRepository.save(usuario);

        passwordResetTokenRepository.delete(resetToken);

        return true;
    }
}