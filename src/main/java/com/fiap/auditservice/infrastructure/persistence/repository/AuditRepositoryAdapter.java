package com.fiap.auditservice.infrastructure.persistence.repository;

import com.fiap.auditservice.domain.AuditRecord;
import com.fiap.auditservice.domain.port.out.AuditRepositoryPort;
import com.fiap.auditservice.infrastructure.persistence.jpa.AuditJpaEntity;
import com.fiap.auditservice.infrastructure.persistence.jpa.AuditJpaRepository;
import com.fiap.auditservice.infrastructure.persistence.mapper.AuditEntityMapper;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
public class AuditRepositoryAdapter implements AuditRepositoryPort {

    private final AuditJpaRepository repository;
    private final AuditEntityMapper mapper;

    public AuditRepositoryAdapter(AuditJpaRepository repository, AuditEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public AuditRecord save(AuditRecord record) {
        AuditJpaEntity entity = mapper.toEntity(record);
        AuditJpaEntity saved = repository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<AuditRecord> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<AuditRecord> findAll(int limit) {
        int size = Math.max(1, limit);
        var page = repository.findAll(PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "createdAt")));
        return page.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}