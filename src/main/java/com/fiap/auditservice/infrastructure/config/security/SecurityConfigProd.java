package com.fiap.auditservice.infrastructure.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuração de segurança para produção (perfil != docker).
 * Libera endpoints de documentação e health/info; aplica OAuth2 JWT para o restante.
 */
@Configuration
@Profile("!docker")
public class SecurityConfigProd {

    @Bean("securityFilterChainProd")
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // Endpoints públicos necessários para swagger/ui e health/info
        String[] publicMatchers = new String[] {
                "/v3/api-docs/**",
                "/swagger-ui/**",
                "/swagger-ui.html",
                "/api-doc/**",
                "/api-doc",
                "/actuator/health",
                "/actuator/info"
        };

        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(publicMatchers).permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

        return http.build();
    }
}