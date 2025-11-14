package com.fiap.auditservice.application.service;

import com.fiap.auditservice.domain.AuditRecord;
import com.fiap.auditservice.domain.port.out.AuditRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SaveAuditRecordServiceTest {

    @Mock
    private AuditRepositoryPort repository;

    @InjectMocks
    private SaveAuditRecordService service;

    @Test
    @DisplayName("Deve salvar registro quando id e createdAt já estiverem presentes")
    void save_withIdAndCreatedAt_shouldCallRepositoryAndReturnSavedRecord() {
        // Arrange
        UUID id = UUID.randomUUID();
        OffsetDateTime createdAt = OffsetDateTime.now().minusDays(1);
        AuditRecord record = new AuditRecord();
        record.setId(id);
        record.setCreatedAt(createdAt);

        when(repository.save(any(AuditRecord.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        AuditRecord result = service.save(record);

        // Assert
        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(createdAt, result.getCreatedAt());

        // Verify
        verify(repository, times(1)).save(record);
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("Deve gerar id quando id for nulo antes de salvar")
    void save_withoutId_shouldGenerateIdBeforeSave() {
        // Arrange
        AuditRecord record = new AuditRecord();
        record.setId(null);
        record.setCreatedAt(OffsetDateTime.now());

        when(repository.save(any(AuditRecord.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ArgumentCaptor<AuditRecord> captor = ArgumentCaptor.forClass(AuditRecord.class);

        // Act
        AuditRecord result = service.save(record);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getId(), "O id deve ser gerado quando nulo");

        // Verify
        verify(repository, times(1)).save(captor.capture());
        AuditRecord saved = captor.getValue();
        assertNotNull(saved.getId(), "O id do objeto enviado ao repositório deve ser gerado");
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("Deve gerar createdAt quando createdAt for nulo antes de salvar")
    void save_withoutCreatedAt_shouldSetCreatedAtBeforeSave() {
        // Arrange
        AuditRecord record = new AuditRecord();
        record.setId(UUID.randomUUID());
        record.setCreatedAt(null);

        when(repository.save(any(AuditRecord.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ArgumentCaptor<AuditRecord> captor = ArgumentCaptor.forClass(AuditRecord.class);

        // Act
        AuditRecord result = service.save(record);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getCreatedAt(), "createdAt deve ser definido quando nulo");

        // Verify
        verify(repository, times(1)).save(captor.capture());
        AuditRecord saved = captor.getValue();
        assertNotNull(saved.getCreatedAt(), "createdAt do objeto enviado ao repositório deve ser definido");
        verifyNoMoreInteractions(repository);
    }
}