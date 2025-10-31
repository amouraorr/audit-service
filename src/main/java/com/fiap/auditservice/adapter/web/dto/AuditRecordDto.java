package com.fiap.auditservice.adapter.web.dto;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuditRecordDto {

    private UUID id;

    @NotBlank(message = "eventType é obrigatório")
    private String eventType;

    @NotBlank(message = "source é obrigatório")
    private String source;

    private Map<String, Object> payload;
    private OffsetDateTime createdAt;

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