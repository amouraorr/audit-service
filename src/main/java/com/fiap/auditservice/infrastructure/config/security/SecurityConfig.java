package com.fiap.auditservice.infrastructure.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuração de segurança para ambiente padrão/dev.
 * Permite acesso público aos endpoints do Swagger/OpenAPI e alguns endpoints de actuator,
 * mantendo autenticação (HTTP Basic) para o restante da API.
 */
@Configuration
public class SecurityConfig {

    @Bean
    @Order(Ordered.LOWEST_PRECEDENCE)
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

        // Autorização: libera os endpoints públicos e exige autenticação para o restante
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers(publicMatchers).permitAll()
                .anyRequest().authenticated()
        );

        // Configura HTTP Basic usando a API recomendada (Customizer)
        http.httpBasic(Customizer.withDefaults());

        // Desabilita CSRF (como antes)
        http.csrf(csrf -> csrf.disable());

        return http.build();
    }
}