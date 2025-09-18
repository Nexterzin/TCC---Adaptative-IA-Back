package com.example.register_tcc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod; // ❗️ Importante adicionar este
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
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            // ✅ Parte 2: Regras de Acesso (VERSÃO CORRETA)
            .authorizeHttpRequests(authorizeRequests ->
                authorizeRequests
                    // Libera as requisições de "permissão" do navegador
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    // Libera todos os seus endpoints de usuário como públicos
                    .requestMatchers("/api/usuarios/**").permitAll()
                    // Exige autenticação para qualquer outra requisição no futuro
                    .anyRequest().authenticated()
            );

        return http.build();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        // ✅ Parte 1: Configuração de CORS (A sua já estava PERFEITA!)
        CorsConfiguration configuration = new CorsConfiguration();
        
        configuration.setAllowedOrigins(Arrays.asList(
            "https://tccadaptativeia.vercel.app",
            "https://tccv1-git-master-nexterzins-projects.vercel.app"
        )); 
        
        configuration.addAllowedOriginPattern("https://*-nexterzins-projects.vercel.app");

        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
