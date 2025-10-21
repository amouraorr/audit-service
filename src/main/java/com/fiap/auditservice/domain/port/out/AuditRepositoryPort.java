package com.fiap.auditservice.domain.port.out;

import com.fiap.auditservice.domain.AuditRecord;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AuditRepositoryPort {

    AuditRecord save(AuditRecord auditRecord);
    Optional<AuditRecord> findById(UUID id);
    List<AuditRecord> findAll(int limit);
}