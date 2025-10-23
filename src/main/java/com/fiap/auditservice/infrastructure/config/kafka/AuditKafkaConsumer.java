package com.fiap.auditservice.infrastructure.config.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.auditservice.application.usecase.SaveAuditRecordUseCase;
import com.fiap.auditservice.domain.AuditRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Component
public class AuditKafkaConsumer {

    private static final Logger log = LoggerFactory.getLogger(AuditKafkaConsumer.class);

    private final SaveAuditRecordUseCase saveAuditRecordUseCase;
    private final ObjectMapper objectMapper;

    public AuditKafkaConsumer(SaveAuditRecordUseCase saveAuditRecordUseCase, ObjectMapper objectMapper) {
        this.saveAuditRecordUseCase = saveAuditRecordUseCase;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = {"parcels.received", "notifications.sent"}, groupId = "${kafka.consumer.group-id:audit-service-group}")
    public void consume(ConsumerRecord<String, String> record) {
        try {
            String topic = record.topic();
            String value = record.value();

            log.info("Received kafka message from topic {} with key {}", topic, record.key());

            @SuppressWarnings("unchecked")
            Map<String, Object> payload = objectMapper.readValue(value == null ? "{}" : value, Map.class);

            AuditRecord r = new AuditRecord();
            r.setId(UUID.randomUUID());
            r.setEventType(topic);
            r.setSource("kafka");
            r.setPayload(payload);
            r.setCreatedAt(OffsetDateTime.now());

            saveAuditRecordUseCase.save(r);
            log.info("Saved audit record id={} eventType={}", r.getId(), r.getEventType());

        } catch (Exception ex) {
            log.error("Failed to process kafka message", ex);
        }
    }
}