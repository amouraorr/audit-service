package com.fiap.auditservice.adapter.web;

import com.fiap.auditservice.adapter.web.dto.AuditRecordDto;
import com.fiap.auditservice.adapter.web.mapper.AuditMapper;
import com.fiap.auditservice.application.usecase.SaveAuditRecordUseCase;
import com.fiap.auditservice.domain.AuditRecord;
import com.fiap.auditservice.domain.port.out.AuditRepositoryPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping("/api/audits")
@Tag(name = "Audit", description = "Endpoints para gerenciamento de registros de auditoria")
public class AuditController {

    private static final Logger log = LoggerFactory.getLogger(AuditController.class);

    private final SaveAuditRecordUseCase saveAuditRecordUseCase;
    private final AuditRepositoryPort auditRepositoryPort;
    private final AuditMapper auditMapper;

    public AuditController(SaveAuditRecordUseCase saveAuditRecordUseCase,
                           AuditRepositoryPort auditRepositoryPort,
                           AuditMapper auditMapper) {
        this.saveAuditRecordUseCase = saveAuditRecordUseCase;
        this.auditRepositoryPort = auditRepositoryPort;
        this.auditMapper = auditMapper;
    }

    @Operation(summary = "Listar registros de auditoria", description = "Retorna até `limit` registros mais recentes")
    @GetMapping
    public ResponseEntity<List<AuditRecordDto>> listAudits(
            @RequestParam(name = "limit", defaultValue = "100") int limit) {
        log.info("Solicitação para listar auditorias com limite={}", limit);
        List<AuditRecordDto> dtos = auditRepositoryPort.findAll(limit)
                .stream()
                .map(auditMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Buscar registro de auditoria por id")
    @GetMapping("/{id}")
    public ResponseEntity<AuditRecordDto> getAuditById(@PathVariable("id") UUID id) {
        log.info("Solicitação para obter o ID de auditoria = {}", id);
        return auditRepositoryPort.findById(id)
                .map(auditMapper::toDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    log.warn("Auditoria não encontrada id={}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @Operation(summary = "Criar registro de auditoria")
    @PostMapping
    public ResponseEntity<AuditRecordDto> createAudit(@Valid @RequestBody AuditRecordDto auditRecordDto) {
        log.info("Solicitação para criar auditoria eventType={} source={}", auditRecordDto.getEventType(), auditRecordDto.getSource());

        AuditRecord toSave = auditMapper.toDomain(auditRecordDto);

        AuditRecord saved = saveAuditRecordUseCase.save(toSave);

        AuditRecordDto responseDto = auditMapper.toDto(saved);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.getId())
                .toUri();

        log.info("Cria registro de auditoria id={} eventType={}", saved.getId(), saved.getEventType());
        return ResponseEntity.created(location).body(responseDto);
    }
}