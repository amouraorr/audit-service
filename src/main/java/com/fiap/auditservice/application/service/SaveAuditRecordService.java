package com.fiap.auditservice.application.service;

import com.fiap.auditservice.application.usecase.SaveAuditRecordUseCase;
import com.fiap.auditservice.domain.AuditRecord;
import com.fiap.auditservice.domain.port.out.AuditRepositoryPort;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class SaveAuditRecordService implements SaveAuditRecordUseCase {

    private final AuditRepositoryPort repository;

    public SaveAuditRecordService(AuditRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public AuditRecord save(AuditRecord record) {

        if (record.getId() == null) {
            record.setId(UUID.randomUUID());
        }
        if (record.getCreatedAt() == null) {
            record.setCreatedAt(OffsetDateTime.now());
        }
        return repository.save(record);
    }
}