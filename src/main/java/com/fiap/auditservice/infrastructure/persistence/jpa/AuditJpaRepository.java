package com.fiap.auditservice.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AuditJpaRepository extends JpaRepository<AuditJpaEntity, UUID> {
}