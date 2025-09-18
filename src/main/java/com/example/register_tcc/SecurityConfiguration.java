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
                .requestMatchers("https://tccv1-git-master-nexterzins-projects.vercel.app",
        "https://tccadaptativeia.vercel.app").permitAll()
                .anyRequest().authenticated()
        );

        return http.build();
    }
    
   // Configura a política de CORS
    // Dentro de SecurityConfiguration.java

// Dentro de SecurityConfiguration.java

@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    
    // Lista de origens estáticas (sua URL de produção final)
    configuration.setAllowedOrigins(Arrays.asList("https://tccadaptativeia.vercel.app")); 

    // ✅ A MÁGICA ESTÁ AQUI!
    // Libera qualquer subdomínio de preview da Vercel para seus projetos
    configuration.addAllowedOriginPattern("https://*-nexterzins-projects.vercel.app");

    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(Arrays.asList("*"));
    configuration.setAllowCredentials(true);
    
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
}
}






