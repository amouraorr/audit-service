/*
package com.fiap.auditservice.infrastructure.persistence.repository;

import com.fiap.auditservice.domain.AuditRecord;
import com.fiap.auditservice.domain.port.out.AuditRepositoryPort;
import com.fiap.auditservice.infrastructure.persistence.jpa.AuditJpaEntity;
import com.fiap.auditservice.infrastructure.persistence.jpa.AuditJpaRepository;
import com.fiap.auditservice.infrastructure.persistence.mapper.AuditEntityMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class AuditRepositoryAdapter implements AuditRepositoryPort {

    private final AuditJpaRepository jpaRepository;
    private final AuditEntityMapper mapper;

    public AuditRepositoryAdapter(AuditJpaRepository jpaRepository, AuditEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public AuditRecord save(AuditRecord auditRecord) {
        AuditJpaEntity entity = mapper.toEntity(auditRecord);
        AuditJpaEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<AuditRecord> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<AuditRecord> findAll(int limit) {
        int size = Math.max(1, limit);
        var page = jpaRepository.findAll(PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "createdAt")));
        return page.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}*/
