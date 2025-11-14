package com.fiap.auditservice.infrastructure.persistence.mapper;

import com.fiap.auditservice.domain.AuditRecord;
import com.fiap.auditservice.infrastructure.persistence.jpa.AuditJpaEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AuditEntityMapperTest {

    @InjectMocks
    private AuditEntityMapper mapper;

    @Test
    @DisplayName("toEntity deve retornar null quando a entrada for null")
    void toEntity_shouldReturnNullWhenInputIsNull() {
        // Arrange

        // Act
        AuditJpaEntity result = mapper.toEntity(null);

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("toEntity deve gerar id quando id do domínio for nulo e copiar campos corretamente")
    void toEntity_shouldGenerateIdWhenIdIsNull_andCopyFields_andConvertCreatedAt() {
        // Arrange
        OffsetDateTime createdAt = OffsetDateTime.now(ZoneOffset.UTC);
        Map<String, Object> payload = Collections.singletonMap("key", "value");
        AuditRecord record = new AuditRecord(null, "EVENT_TYPE", "SOURCE", payload, createdAt);

        // Act
        AuditJpaEntity entity = mapper.toEntity(record);

        // Assert & Assert
        assertNotNull(entity);
        assertNotNull(entity.getId(), "Id gerado não deve ser nulo");
        assertEquals("EVENT_TYPE", entity.getEventType());
        assertEquals("SOURCE", entity.getSource());
        assertEquals(payload, entity.getPayload());
        assertEquals(createdAt.toInstant(), entity.getCreatedAt());
    }

    @Test
    @DisplayName("toEntity deve manter id quando id do domínio for fornecido e createdAt nulo deve resultar em null no entity")
    void toEntity_shouldKeepProvidedId_andHandleNullCreatedAt() {
        // Arrange
        UUID providedId = UUID.randomUUID();
        AuditRecord record = new AuditRecord(providedId, "EV", "SRC", null, null);

        // Act
        AuditJpaEntity entity = mapper.toEntity(record);

        // Assert
        assertNotNull(entity);
        assertEquals(providedId, entity.getId());
        assertEquals("EV", entity.getEventType());
        assertEquals("SRC", entity.getSource());
        assertNull(entity.getCreatedAt());
    }

    @Test
    @DisplayName("toDomain deve retornar null quando a entity for null")
    void toDomain_shouldReturnNullWhenInputIsNull() {
        // Arrange

        // Act
        AuditRecord result = mapper.toDomain(null);

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("toDomain deve converter Instant para OffsetDateTime em UTC e substituir payload nulo por mapa vazio")
    void toDomain_shouldConvertInstantToOffsetDateTime_andEmptyPayloadWhenNull() {
        // Arrange
        UUID id = UUID.randomUUID();
        AuditJpaEntity entity = new AuditJpaEntity();
        entity.setId(id);
        entity.setEventType("TYPE");
        entity.setSource("SRC");
        entity.setPayload(null);
        Instant instant = Instant.parse("2025-01-01T10:00:00Z");
        entity.setCreatedAt(instant);

        // Act
        AuditRecord record = mapper.toDomain(entity);

        // Assert
        assertNotNull(record);
        assertEquals(id, record.getId());
        assertEquals("TYPE", record.getEventType());
        assertEquals("SRC", record.getSource());
        assertNotNull(record.getPayload());
        assertTrue(record.getPayload().isEmpty());
        assertEquals(OffsetDateTime.ofInstant(instant, ZoneOffset.UTC), record.getCreatedAt());
    }

    @Test
    @DisplayName("toDomain deve manter createdAt null quando entity.createdAt for null e manter payload quando presente")
    void toDomain_shouldKeepCreatedAtNullAndPreservePayload() {
        // Arrange
        UUID id = UUID.randomUUID();
        Map<String, Object> payload = Collections.singletonMap("a", 1);
        AuditJpaEntity entity = new AuditJpaEntity();
        entity.setId(id);
        entity.setEventType("T");
        entity.setSource("S");
        entity.setPayload(payload);
        entity.setCreatedAt(null);

        // Act
        AuditRecord record = mapper.toDomain(entity);

        // Assert
        assertNotNull(record);
        assertEquals(id, record.getId());
        assertEquals("T", record.getEventType());
        assertEquals("S", record.getSource());
        assertEquals(payload, record.getPayload());
        assertNull(record.getCreatedAt());
    }
}