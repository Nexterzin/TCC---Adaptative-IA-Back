package com.example.register_tcc;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

	private final UsuarioRepository usuarioRepository;
	private final EmailService emailService;
	private final PasswordResetTokenRepository passwordResetTokenRepository;
	private final PasswordResetTokenService passwordResetTokenService;
	private final PasswordEncoder passwordEncoder;

	public UsuarioService(UsuarioRepository usuarioRepository, EmailService emailService,
			PasswordResetTokenRepository passwordResetTokenRepository,
			PasswordResetTokenService passwordResetTokenService,
			PasswordEncoder passwordEncoder) {
		this.usuarioRepository = usuarioRepository;
		this.emailService = emailService;
		this.passwordResetTokenRepository = passwordResetTokenRepository;
		this.passwordResetTokenService = passwordResetTokenService;
		this.passwordEncoder = passwordEncoder;
	}

	public Usuario registrarUsuario(Usuario usuario) {
		if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
			throw new RuntimeException("E-mail já cadastrado");
		}

		usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));

		return usuarioRepository.save(usuario);
	}

	public Optional<Usuario> login(String email, String senha) {
		return usuarioRepository.findByEmail(email).filter(u -> passwordEncoder.matches(senha, u.getSenha()));
	}

	@Transactional
	public boolean iniciarRecuperacaoSenha(String email) {
		Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
		if (usuarioOpt.isEmpty()) {
			return false; 
		}

		Usuario usuario = usuarioOpt.get();

		try {
			String token = UUID.randomUUID().toString();
			LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(30);

			passwordResetTokenService.criarOuAtualizarToken(usuario, token, expiryDate);

			String resetLink = "https://tccadaptativeia.vercel.app/reset-password?token=" + token;
			emailService.enviarEmail(usuario.getEmail(), "Recuperação de Senha",
					"Clique no link para redefinir sua senha: " + resetLink);

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false; 
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

		usuario.setSenha(passwordEncoder.encode(novaSenha));

		usuarioRepository.save(usuario);
		passwordResetTokenRepository.delete(resetToken);

		return true;

	}
}
