package com.fiap.auditservice.infrastructure.persistence.mapper;

import com.fiap.auditservice.domain.AuditRecord;
import com.fiap.auditservice.infrastructure.persistence.jpa.AuditJpaEntity;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

@Component
public class AuditEntityMapper {

    public AuditJpaEntity toEntity(AuditRecord r) {
        if (r == null) return null;
        AuditJpaEntity e = new AuditJpaEntity();
        e.setId(r.getId() == null ? UUID.randomUUID() : r.getId());
        e.setEventType(r.getEventType());
        e.setSource(r.getSource());
        e.setPayload(r.getPayload());
        e.setCreatedAt(r.getCreatedAt() == null ? null : r.getCreatedAt().toInstant());
        return e;
    }

    public AuditRecord toDomain(AuditJpaEntity e) {
        if (e == null) return null;
        Map<String, Object> payload = e.getPayload() == null ? Collections.emptyMap() : e.getPayload();

        OffsetDateTime createdAt = e.getCreatedAt() == null
                ? null
                : OffsetDateTime.ofInstant(e.getCreatedAt(), ZoneOffset.UTC);

        return new AuditRecord(e.getId(), e.getEventType(), e.getSource(), payload, createdAt);
    }
}