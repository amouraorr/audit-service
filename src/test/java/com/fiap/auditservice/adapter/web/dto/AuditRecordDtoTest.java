package com.fiap.auditservice.adapter.web.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AuditRecordDtoTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("Deve setar e obter todos os campos via getters e setters")
    void testGettersAndSetters() {
        // Arrange
        AuditRecordDto dto = new AuditRecordDto();
        UUID id = UUID.randomUUID();
        String eventType = "USER_CREATED";
        String source = "user-service";
        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", 123);
        OffsetDateTime createdAt = OffsetDateTime.now();

        // Act
        dto.setId(id);
        dto.setEventType(eventType);
        dto.setSource(source);
        dto.setPayload(payload);
        dto.setCreatedAt(createdAt);

        // Assert
        assertEquals(id, dto.getId());
        assertEquals(eventType, dto.getEventType());
        assertEquals(source, dto.getSource());
        assertEquals(payload, dto.getPayload());
        assertEquals(createdAt, dto.getCreatedAt());
    }

    @Test
    @DisplayName("Serialização JSON deve omitir campos nulos")
    void testJsonSerializationExcludesNulls() throws Exception {
        // Arrange
        AuditRecordDto dto = new AuditRecordDto();
        dto.setEventType("EVENT_X");
        dto.setSource("source-x");

        // Act
        String json = objectMapper.writeValueAsString(dto);
        JsonNode node = objectMapper.readTree(json);

        // Assert
        assertTrue(node.has("eventType"));
        assertTrue(node.has("source"));
        assertFalse(node.has("id"));
        assertFalse(node.has("payload"));
        assertFalse(node.has("createdAt"));
    }

    @Test
    @DisplayName("Validação deve falhar quando eventType estiver em branco")
    void testValidationFailsWhenEventTypeBlank() {
        // Arrange
        AuditRecordDto dto = new AuditRecordDto();
        dto.setEventType("  ");
        dto.setSource("valid-source");

        // Act
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();
            Set<ConstraintViolation<AuditRecordDto>> violations = validator.validate(dto);

            // Assert
            assertFalse(violations.isEmpty());
            boolean hasEventTypeViolation = violations.stream()
                    .anyMatch(v -> "eventType é obrigatório".equals(v.getMessage()));
            assertTrue(hasEventTypeViolation, "Deve conter violação para eventType");
        }
    }

    @Test
    @DisplayName("Validação deve falhar quando source estiver em branco")
    void testValidationFailsWhenSourceBlank() {
        // Arrange
        AuditRecordDto dto = new AuditRecordDto();
        dto.setEventType("EVENT_A");
        dto.setSource("");

        // Act
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();
            Set<ConstraintViolation<AuditRecordDto>> violations = validator.validate(dto);

            // Assert
            assertFalse(violations.isEmpty());
            boolean hasSourceViolation = violations.stream()
                    .anyMatch(v -> "source é obrigatório".equals(v.getMessage()));
            assertTrue(hasSourceViolation, "Deve conter violação para source");
        }
    }

    @Test
    @DisplayName("Validação deve passar quando DTO estiver válido")
    void testValidationPassesWhenValid() {
        // Arrange
        AuditRecordDto dto = new AuditRecordDto();
        dto.setEventType("EVENT_OK");
        dto.setSource("ok-source");
        dto.setId(UUID.randomUUID());
        Map<String, Object> payload = new HashMap<>();
        payload.put("k", "v");
        dto.setPayload(payload);
        dto.setCreatedAt(OffsetDateTime.now());

        // Act
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();
            Set<ConstraintViolation<AuditRecordDto>> violations = validator.validate(dto);

            // Assert
            assertTrue(violations.isEmpty(), "Não deve haver violações para DTO válido");
        }
    }
}