package com.fiap.auditservice.adapter.web;

import com.fiap.auditservice.adapter.web.dto.AuditRecordDto;
import com.fiap.auditservice.adapter.web.mapper.AuditMapper;
import com.fiap.auditservice.application.usecase.SaveAuditRecordUseCase;
import com.fiap.auditservice.domain.AuditRecord;
import com.fiap.auditservice.domain.port.out.AuditRepositoryPort;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditControllerTest {

    @Mock
    private SaveAuditRecordUseCase saveAuditRecordUseCase;

    @Mock
    private AuditRepositoryPort auditRepositoryPort;

    @Mock
    private AuditMapper auditMapper;

    @InjectMocks
    private AuditController auditController;

    private ServletRequestAttributes previousRequestAttributes;

    @BeforeEach
    void setUp() {
        previousRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.setRequestAttributes(previousRequestAttributes);
    }

    @Test
    @DisplayName("Listar auditorias retorna lista mapeada e status 200")
    void listAudits_returnsListOk() {
        // Arrange
        int limit = 2;
        AuditRecord record1 = mock(AuditRecord.class);
        AuditRecord record2 = mock(AuditRecord.class);

        AuditRecordDto dto1 = mock(AuditRecordDto.class);
        AuditRecordDto dto2 = mock(AuditRecordDto.class);

        when(auditRepositoryPort.findAll(limit)).thenReturn(List.of(record1, record2));
        when(auditMapper.toDto(record1)).thenReturn(dto1);
        when(auditMapper.toDto(record2)).thenReturn(dto2);

        // Act
        var response = auditController.listAudits(limit);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertSame(dto1, response.getBody().get(0));
        assertSame(dto2, response.getBody().get(1));

        // Verify
        verify(auditRepositoryPort, times(1)).findAll(limit);
        verify(auditMapper, times(1)).toDto(record1);
        verify(auditMapper, times(1)).toDto(record2);
    }

    @Test
    @DisplayName("Buscar auditoria por id quando encontrado retorna 200 com body")
    void getAuditById_found() {
        // Arrange
        UUID id = UUID.randomUUID();
        AuditRecord record = mock(AuditRecord.class);
        AuditRecordDto dto = mock(AuditRecordDto.class);

        when(auditRepositoryPort.findById(id)).thenReturn(Optional.of(record));
        when(auditMapper.toDto(record)).thenReturn(dto);

        // Act
        var response = auditController.getAuditById(id);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertSame(dto, response.getBody());

        // Verify
        verify(auditRepositoryPort, times(1)).findById(id);
        verify(auditMapper, times(1)).toDto(record);
    }

    @Test
    @DisplayName("Buscar auditoria por id quando não encontrado retorna 404")
    void getAuditById_notFound() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(auditRepositoryPort.findById(id)).thenReturn(Optional.empty());

        // Act
        var response = auditController.getAuditById(id);

        // Assert
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());

        // Verify
        verify(auditRepositoryPort, times(1)).findById(id);
        verifyNoInteractions(auditMapper);
    }

    @Test
    @DisplayName("Criar auditoria retorna 201, body mapeado e header Location")
    void createAudit_success() {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/audits");
        ServletRequestAttributes attrs = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(attrs);

        AuditRecordDto inputDto = mock(AuditRecordDto.class);
        AuditRecord toSave = mock(AuditRecord.class);
        AuditRecord saved = mock(AuditRecord.class);
        AuditRecordDto responseDto = mock(AuditRecordDto.class);

        UUID savedId = UUID.randomUUID();

        when(auditMapper.toDomain(inputDto)).thenReturn(toSave);
        when(saveAuditRecordUseCase.save(toSave)).thenReturn(saved);
        when(saved.getId()).thenReturn(savedId);
        when(saved.getEventType()).thenReturn("EVENT");
        when(auditMapper.toDto(saved)).thenReturn(responseDto);

        // Act
        var response = auditController.createAudit(inputDto);

        // Assert
        assertNotNull(response);
        assertEquals(201, response.getStatusCodeValue());
        assertSame(responseDto, response.getBody());
        assertNotNull(response.getHeaders().getLocation());
        URI location = response.getHeaders().getLocation();
        assertTrue(location.toString().endsWith("/" + savedId.toString()) || location.toString().contains(savedId.toString()));

        // Verify
        verify(auditMapper, times(1)).toDomain(inputDto);
        verify(saveAuditRecordUseCase, times(1)).save(toSave);
        verify(auditMapper, times(1)).toDto(saved);
    }
}