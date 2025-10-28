package com.flowpay.banking.entity;

import com.flowpay.banking.enums.ReferralStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidade que representa o programa de indicação/referral
 */
@Entity
@Table(name = "referrals",
    indexes = {
        @Index(name = "idx_referral_referrer", columnList = "referrer_user_id"),
        @Index(name = "idx_referral_code", columnList = "referralCode"),
        @Index(name = "idx_referral_status", columnList = "status")
    },
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"referralCode"})
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Referral {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Usuário que indicou (quem vai receber o bônus)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "referrer_user_id", nullable = false)
    @NotNull(message = "Usuário indicador é obrigatório")
    private User referrer;

    /**
     * Usuário indicado (novo usuário)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "referred_user_id")
    private User referredUser;

    /**
     * Código único de indicação
     */
    @Column(nullable = false, unique = true, length = 20)
    @NotBlank(message = "Código de indicação é obrigatório")
    private String referralCode;

    /**
     * Email do indicado (antes do cadastro)
     */
    @Column(length = 100)
    @Email
    private String referredEmail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private ReferralStatus status = ReferralStatus.PENDING;

    /**
     * Valor do bônus para o indicador
     */
    @Column(precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal bonusAmount = new BigDecimal("50.00");

    /**
     * Transação do pagamento do bônus
     */
    @OneToOne
    @JoinColumn(name = "bonus_transaction_id")
    private Transaction bonusTransaction;

    /**
     * Data em que o indicado completou o cadastro
     */
    @Column
    private LocalDateTime completedAt;

    /**
     * Data em que o bônus foi pago
     */
    @Column
    private LocalDateTime paidAt;

    @Column(nullable = false)
    @Builder.Default
    private Boolean deleted = false;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Transient
    public boolean isCompleted() {
        return status == ReferralStatus.COMPLETED;
    }

    @Transient
    public boolean isPaid() {
        return status == ReferralStatus.PAID;
    }
}
