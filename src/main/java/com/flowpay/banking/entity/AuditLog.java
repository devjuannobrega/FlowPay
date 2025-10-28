package com.flowpay.banking.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entidade para auditoria de todas operações no sistema
 * Log imutável (append-only) para rastreabilidade completa
 */
@Entity
@Table(name = "audit_logs",
    indexes = {
        @Index(name = "idx_audit_user", columnList = "userId"),
        @Index(name = "idx_audit_entity", columnList = "entityType, entityId"),
        @Index(name = "idx_audit_action", columnList = "action"),
        @Index(name = "idx_audit_created", columnList = "createdAt")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * ID do usuário que realizou a ação
     */
    @Column
    private Long userId;

    /**
     * Nome/email do usuário
     */
    @Column(length = 200)
    private String username;

    /**
     * Tipo de entidade afetada (User, Account, Transaction, etc.)
     */
    @Column(nullable = false, length = 50)
    @NotBlank(message = "Tipo de entidade é obrigatório")
    private String entityType;

    /**
     * ID da entidade afetada
     */
    @Column
    private Long entityId;

    /**
     * Ação realizada (CREATE, UPDATE, DELETE, LOGIN, LOGOUT, etc.)
     */
    @Column(nullable = false, length = 50)
    @NotBlank(message = "Ação é obrigatória")
    private String action;

    /**
     * Descrição detalhada da ação
     */
    @Column(length = 500)
    private String description;

    /**
     * Valor anterior (JSON)
     */
    @Column(columnDefinition = "TEXT")
    private String oldValue;

    /**
     * Novo valor (JSON)
     */
    @Column(columnDefinition = "TEXT")
    private String newValue;

    /**
     * IP do usuário
     */
    @Column(length = 45)
    private String ipAddress;

    /**
     * User-Agent
     */
    @Column(length = 255)
    private String userAgent;

    /**
     * Informações adicionais (JSON)
     */
    @Column(columnDefinition = "TEXT")
    private String metadata;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
