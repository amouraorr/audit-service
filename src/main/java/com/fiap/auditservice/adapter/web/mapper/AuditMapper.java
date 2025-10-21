package com.fiap.auditservice.adapter.web.mapper;

import com.fiap.auditservice.adapter.web.dto.AuditRecordDto;
import com.fiap.auditservice.domain.AuditRecord;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AuditMapper {

    AuditMapper INSTANCE = Mappers.getMapper(AuditMapper.class);

    AuditRecordDto toDto(AuditRecord record);
    AuditRecord toDomain(AuditRecordDto dto);
}