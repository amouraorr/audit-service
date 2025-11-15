package com.fiap.auditservice.infrastructure.persistence.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.fiap.auditservice.domain.AuditRecord;
import com.fiap.auditservice.infrastructure.persistence.jpa.AuditJpaEntity;
import com.fiap.auditservice.infrastructure.persistence.jpa.AuditJpaRepository;
import com.fiap.auditservice.infrastructure.persistence.mapper.AuditEntityMapper;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para AuditRepositoryAdapter")
class AuditRepositoryAdapterTest {

    @Mock
    private AuditJpaRepository repository;

    @Mock
    private AuditEntityMapper mapper;

    @InjectMocks
    private AuditRepositoryAdapter adapter;

    @Test
    @DisplayName("save - deve salvar registro e retornar domínio mapeado")
    void save_shouldSaveAndReturnDomain() {
        // Arrange
        AuditRecord inputDomain = mock(AuditRecord.class);
        AuditJpaEntity entity = mock(AuditJpaEntity.class);
        AuditJpaEntity savedEntity = mock(AuditJpaEntity.class);
        AuditRecord savedDomain = mock(AuditRecord.class);

        when(mapper.toEntity(inputDomain)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(savedEntity);
        when(mapper.toDomain(savedEntity)).thenReturn(savedDomain);

        // Act
        AuditRecord result = adapter.save(inputDomain);

        // Assert
        assertSame(savedDomain, result);

        // Verify
        verify(mapper).toEntity(inputDomain);
        verify(repository).save(entity);
        verify(mapper).toDomain(savedEntity);
    }

    @Test
    @DisplayName("findById - quando existir deve retornar Optional com domínio")
    void findById_whenFound_shouldReturnDomain() {
        // Arrange
        UUID id = UUID.randomUUID();
        AuditJpaEntity entity = mock(AuditJpaEntity.class);
        AuditRecord domain = mock(AuditRecord.class);

        when(repository.findById(id)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        // Act
        Optional<AuditRecord> result = adapter.findById(id);

        // Assert
        assertTrue(result.isPresent());
        assertSame(domain, result.get());

        // Verify
        verify(repository).findById(id);
        verify(mapper).toDomain(entity);
    }

    @Test
    @DisplayName("findById - quando não existir deve retornar Optional vazio")
    void findById_whenNotFound_shouldReturnEmpty() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        // Act
        Optional<AuditRecord> result = adapter.findById(id);

        // Assert
        assertFalse(result.isPresent());

        // Verify
        verify(repository).findById(id);
        verify(mapper, never()).toDomain(any());
    }

    @Test
    @DisplayName("findAll - com limit positivo deve retornar lista ordenada e mapeada")
    void findAll_withPositiveLimit_shouldReturnMappedListAndUsePageRequest() {
        // Arrange
        int limit = 5;
        AuditJpaEntity e1 = mock(AuditJpaEntity.class);
        AuditJpaEntity e2 = mock(AuditJpaEntity.class);
        AuditRecord d1 = mock(AuditRecord.class);
        AuditRecord d2 = mock(AuditRecord.class);

        when(repository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(e1, e2)));
        when(mapper.toDomain(e1)).thenReturn(d1);
        when(mapper.toDomain(e2)).thenReturn(d2);

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        // Act
        List<AuditRecord> results = adapter.findAll(limit);

        // Assert
        assertNotNull(results);
        assertEquals(2, results.size());
        assertSame(d1, results.get(0));
        assertSame(d2, results.get(1));

        // Verify
        verify(repository).findAll(pageableCaptor.capture());
        Pageable captured = pageableCaptor.getValue();
        assertEquals(limit, captured.getPageSize());
        assertEquals(0, captured.getPageNumber());
        Sort sort = captured.getSort();
        assertTrue(sort.getOrderFor("createdAt").isDescending());
        verify(mapper).toDomain(e1);
        verify(mapper).toDomain(e2);
    }

    @Test
    @DisplayName("findAll - com limit menor ou igual a zero deve usar tamanho mínimo 1")
    void findAll_withNonPositiveLimit_shouldUseMinimumSizeOne() {
        // Arrange
        int limit = 0;
        AuditJpaEntity e1 = mock(AuditJpaEntity.class);
        AuditRecord d1 = mock(AuditRecord.class);

        when(repository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(e1)));
        when(mapper.toDomain(e1)).thenReturn(d1);

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        // Act
        List<AuditRecord> results = adapter.findAll(limit);

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        assertSame(d1, results.get(0));

        // Verify
        verify(repository).findAll(pageableCaptor.capture());
        Pageable captured = pageableCaptor.getValue();
        assertEquals(1, captured.getPageSize());
        assertEquals(0, captured.getPageNumber());
        Sort sort = captured.getSort();
        assertTrue(sort.getOrderFor("createdAt").isDescending());
        verify(mapper).toDomain(e1);
    }
}