package com.example.register_tcc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Configura CORS
            .authorizeHttpRequests(authorizeRequests ->
            authorizeRequests
                .requestMatchers("/api/usuarios/resetar-senha", "/api/usuarios/registrar", "/api/usuarios/login", "/api/usuarios/recuperar-senha", "tccadaptativeia.vercel.app").permitAll()
                .anyRequest().authenticated()
        );

        return http.build();
    }
    
    // Configura a política de CORS
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Permite a origem do seu frontend Next.js.
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        // Permite os métodos HTTP que você usa (POST para registro).
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // Permite os headers que o frontend envia.
        configuration.setAllowedHeaders(Arrays.asList("Content-Type", "Authorization"));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

