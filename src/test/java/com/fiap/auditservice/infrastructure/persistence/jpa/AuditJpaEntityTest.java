package com.fiap.auditservice.infrastructure.persistence.jpa;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class AuditJpaEntityTest {

    @Test
    @DisplayName("Deve definir createdAt no prePersist quando estiver nulo")
    void testPrePersistSetsCreatedAtWhenNull() {
        // Arrange
        Map<String, Object> payload = mock(Map.class);
        UUID id = UUID.randomUUID();
        AuditJpaEntity entity = new AuditJpaEntity(id, "EVENT_TYPE", "SOURCE", payload, null);
        Instant before = Instant.now();

        // Act
        entity.prePersist();

        // Assert
        assertNotNull(entity.getCreatedAt(), "createdAt não deve ser nulo após prePersist");
        Instant after = entity.getCreatedAt();
        assertFalse(after.isBefore(before), "createdAt deve ser maior ou igual ao tempo antes da chamada");
    }

    @Test
    @DisplayName("Não deve sobrescrever createdAt no prePersist quando já estiver definido")
    void testPrePersistDoesNotOverwriteCreatedAtWhenAlreadySet() {
        // Arrange
        Map<String, Object> payload = mock(Map.class);
        UUID id = UUID.randomUUID();
        Instant original = Instant.parse("2020-01-01T00:00:00Z");
        AuditJpaEntity entity = new AuditJpaEntity(id, "EVENT_TYPE", "SOURCE", payload, original);

        // Act
        entity.prePersist();

        // Assert
        assertEquals(original, entity.getCreatedAt(), "createdAt não deve ser sobrescrito se já estiver definido");
    }

    @Test
    @DisplayName("Construtor all-args e getters devem setar e retornar os valores corretamente")
    void testAllArgsConstructorAndGettersSetValues() {
        // Arrange
        Map<String, Object> payload = mock(Map.class);
        UUID id = UUID.randomUUID();
        String eventType = "MY_EVENT";
        String source = "MY_SOURCE";
        Instant createdAt = Instant.parse("2021-06-01T12:00:00Z");

        // Act
        AuditJpaEntity entity = new AuditJpaEntity(id, eventType, source, payload, createdAt);

        // Assert
        assertEquals(id, entity.getId());
        assertEquals(eventType, entity.getEventType());
        assertEquals(source, entity.getSource());
        assertEquals(payload, entity.getPayload());
        assertEquals(createdAt, entity.getCreatedAt());
    }
}