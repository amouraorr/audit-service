package com.fiap.auditservice.infrastructure.config.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.auditservice.application.usecase.SaveAuditRecordUseCase;
import com.fiap.auditservice.domain.AuditRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditKafkaConsumerTest {

    @Mock
    private SaveAuditRecordUseCase saveAuditRecordUseCase;

    private ObjectMapper objectMapper;

    private AuditKafkaConsumer consumer;

    @Captor
    private ArgumentCaptor<AuditRecord> auditRecordCaptor;

    @BeforeEach
    void setUp() {
        // Arrange
        this.objectMapper = new ObjectMapper();
        this.consumer = new AuditKafkaConsumer(saveAuditRecordUseCase, objectMapper);
    }

    @Test
    @DisplayName("Deve consumir mensagem JSON válida e salvar registro de auditoria")
    void testConsumeAndSaveAuditRecordWhenMessageIsValid() throws Exception {
        // Arrange
        String topic = "parcels.received";
        String key = "key-123";
        String json = "{\"orderId\":\"abc-123\",\"status\":\"RECEIVED\"}";
        ConsumerRecord<String, String> record = new ConsumerRecord<>(topic, 0, 1L, key, json);

        // Act
        consumer.consume(record);

        // Assert & Verify
        verify(saveAuditRecordUseCase, times(1)).save(auditRecordCaptor.capture());
        AuditRecord saved = auditRecordCaptor.getValue();

        assertNotNull(saved.getId(), "id não deve ser nulo");
        assertEquals(topic, saved.getEventType(), "eventType deve corresponder ao tópico");
        assertEquals("kafka", saved.getSource(), "source deve ser 'kafka'");

        @SuppressWarnings("unchecked")
        Map<String, Object> payload = (Map<String, Object>) saved.getPayload();
        assertNotNull(payload, "payload não deve ser nulo");
        assertEquals("abc-123", payload.get("orderId"));
        assertEquals("RECEIVED", payload.get("status"));

        assertNotNull(saved.getCreatedAt(), "createdAt não deve ser nulo");
    }

    @Test
    @DisplayName("Deve consumir mensagem com valor nulo e salvar registro com payload vazio")
    void testConsumeHandlesNullValue() throws Exception {
        // Arrange
        String topic = "notifications.sent";
        ConsumerRecord<String, String> record = new ConsumerRecord<>(topic, 1, 10L, "k", null);

        // Act
        consumer.consume(record);

        // Assert & Verify
        verify(saveAuditRecordUseCase, times(1)).save(auditRecordCaptor.capture());
        AuditRecord saved = auditRecordCaptor.getValue();

        assertEquals(topic, saved.getEventType());
        assertEquals("kafka", saved.getSource());

        @SuppressWarnings("unchecked")
        Map<String, Object> payload = (Map<String, Object>) saved.getPayload();
        assertNotNull(payload);
        assertTrue(payload.isEmpty(), "payload deve ser um mapa vazio quando a mensagem for nula");

        assertNotNull(saved.getCreatedAt());
    }

    @Test
    @DisplayName("Deve capturar exceção lançada pelo save e não propagar")
    void testConsumeDoesNotThrowWhenSaveThrowsException() throws Exception {
        // Arrange
        String topic = "parcels.received";
        String json = "{\"orderId\":\"x\",\"status\":\"X\"}";
        ConsumerRecord<String, String> record = new ConsumerRecord<>(topic, 0, 2L, "k", json);

        doThrow(new RuntimeException("DB down")).when(saveAuditRecordUseCase).save(any(AuditRecord.class));

        // Act & Assert
        assertDoesNotThrow(() -> consumer.consume(record), "O método consume não deve propagar exceções");

        // Verify
        verify(saveAuditRecordUseCase, times(1)).save(any(AuditRecord.class));
    }
}