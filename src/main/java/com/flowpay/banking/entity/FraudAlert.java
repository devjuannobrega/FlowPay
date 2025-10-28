package com.flowpay.banking.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entidade que representa um alerta de fraude
 */
@Entity
@Table(name = "fraud_alerts",
    indexes = {
        @Index(name = "idx_fraud_user", columnList = "user_id"),
        @Index(name = "idx_fraud_account", columnList = "account_id"),
        @Index(name = "idx_fraud_transaction", columnList = "transaction_id"),
        @Index(name = "idx_fraud_status", columnList = "status"),
        @Index(name = "idx_fraud_score", columnList = "riskScore")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FraudAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id")
    private Transaction transaction;

    /**
     * Score de risco (0-100, quanto maior, mais suspeito)
     */
    @Column(nullable = false)
    @Min(value = 0)
    @Max(value = 100)
    private Integer riskScore;

    /**
     * Tipo de alerta (UNUSUAL_AMOUNT, UNUSUAL_LOCATION, MULTIPLE_ATTEMPTS, etc.)
     */
    @Column(nullable = false, length = 50)
    @NotBlank(message = "Tipo de alerta é obrigatório")
    private String alertType;

    /**
     * Descrição do que foi detectado
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    @NotBlank(message = "Descrição é obrigatória")
    private String description;

    /**
     * Regras que dispararam o alerta (JSON)
     */
    @Column(columnDefinition = "TEXT")
    private String triggeredRules;

    /**
     * Status do alerta (OPEN, UNDER_REVIEW, FALSE_POSITIVE, CONFIRMED, RESOLVED)
     */
    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = "OPEN";

    /**
     * ID do analista que revisou
     */
    @Column
    private Long reviewedBy;

    /**
     * Comentários da análise
     */
    @Column(columnDefinition = "TEXT")
    private String reviewComments;

    /**
     * Data/hora da revisão
     */
    @Column
    private LocalDateTime reviewedAt;

    /**
     * Ação tomada (BLOCKED, ALLOWED, TWO_FACTOR_REQUIRED, etc.)
     */
    @Column(length = 50)
    private String actionTaken;

    @Column(length = 45)
    private String ipAddress;

    @Column(length = 255)
    private String userAgent;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Transient
    public boolean isHighRisk() {
        return riskScore >= 70;
    }

    @Transient
    public boolean isMediumRisk() {
        return riskScore >= 40 && riskScore < 70;
    }

    @Transient
    public boolean isLowRisk() {
        return riskScore < 40;
    }
}
