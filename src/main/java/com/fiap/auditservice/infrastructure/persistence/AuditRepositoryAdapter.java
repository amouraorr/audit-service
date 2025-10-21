package com.fiap.auditservice.infrastructure.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.auditservice.domain.AuditRecord;
import com.fiap.auditservice.domain.port.out.AuditRepositoryPort;
import com.fiap.auditservice.infrastructure.persistence.jpa.AuditJpaEntity;
import com.fiap.auditservice.infrastructure.persistence.jpa.AuditJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class AuditRepositoryAdapter implements AuditRepositoryPort {

    private final AuditJpaRepository jpaRepository;
    private final ObjectMapper objectMapper;

    public AuditRepositoryAdapter(AuditJpaRepository jpaRepository, ObjectMapper objectMapper) {
        this.jpaRepository = jpaRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public AuditRecord save(AuditRecord auditRecord) {
        AuditJpaEntity entity = toEntity(auditRecord);
        AuditJpaEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<AuditRecord> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<AuditRecord> findAll(int limit) {
        return jpaRepository.findAll()
                .stream()
                .limit(limit)
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    private AuditJpaEntity toEntity(AuditRecord r) {
        AuditJpaEntity e = new AuditJpaEntity();
        e.setId(r.getId());
        e.setEventType(r.getEventType());
        e.setSource(r.getSource());
        try {
            e.setPayload(objectMapper.writeValueAsString(r.getPayload()));
        } catch (JsonProcessingException ex) {
            e.setPayload("{}");
        }
        e.setCreatedAt(r.getCreatedAt());
        return e;
    }

    private AuditRecord toDomain(AuditJpaEntity e) {
        try {
            @SuppressWarnings("unchecked")
            var payload = objectMapper.readValue(e.getPayload() == null ? "{}" : e.getPayload(), java.util.Map.class);
            return new AuditRecord(e.getId(), e.getEventType(), e.getSource(), payload, e.getCreatedAt());
        } catch (JsonProcessingException ex) {
            return new AuditRecord(e.getId(), e.getEventType(), e.getSource(), java.util.Collections.emptyMap(), e.getCreatedAt());
        }
    }
}