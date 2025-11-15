package com.fiap.auditservice.application.usecase;

import com.fiap.auditservice.domain.AuditRecord;

public interface SaveAuditRecordUseCase {

    AuditRecord save(AuditRecord record);
}