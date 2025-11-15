package com.fiap.auditservice.domain;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

public class AuditRecord {

    private UUID id;
    private String eventType;
    private String source;
    private Map<String, Object> payload;
    private OffsetDateTime createdAt;

    public AuditRecord() {}

    public AuditRecord(UUID id, String eventType, String source, Map<String, Object> payload, OffsetDateTime createdAt) {
        this.id = id;
        this.eventType = eventType;
        this.source = source;
        this.payload = payload;
        this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public Map<String, Object> getPayload() { return payload; }
    public void setPayload(Map<String, Object> payload) { this.payload = payload; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}