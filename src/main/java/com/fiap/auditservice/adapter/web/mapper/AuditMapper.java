package com.fiap.auditservice.adapter.web.mapper;

import com.fiap.auditservice.adapter.web.dto.AuditRecordDto;
import com.fiap.auditservice.domain.AuditRecord;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuditMapper {

    AuditRecordDto toDto(AuditRecord record);
    AuditRecord toDomain(AuditRecordDto dto);
}