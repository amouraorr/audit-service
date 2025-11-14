package com.fiap.auditservice.infrastructure.config.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes para OpenApiConfig")
@ExtendWith(MockitoExtension.class)
class OpenApiConfigTest {

    @Test
    @DisplayName("Deve criar um OpenAPI com title, version e description configurados corretamente")
    void shouldCreateCustomOpenAPI() {
        // Arrange
        OpenApiConfig config = new OpenApiConfig();

        // Act
        OpenAPI openAPI = config.customOpenAPI();

        // Assert
        assertNotNull(openAPI, "OpenAPI não deve ser nulo");
        Info info = openAPI.getInfo();
        assertNotNull(info, "Info não deve ser nulo");

        assertEquals("PÓS GRADUAÇÃO - FIAP 2025 - SERVIÇO DE AUDITORIA", info.getTitle(), "Title deve estar conforme configurado");
        assertEquals("1.0.0", info.getVersion(), "Version deve estar conforme configurado");
        assertEquals("Microsserviço de auditoria: registra, persiste e expõe eventos de auditoria do sistema.", info.getDescription(), "Description deve estar conforme configurado");
    }
}