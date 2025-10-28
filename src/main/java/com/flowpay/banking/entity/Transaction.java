package com.flowpay.banking.entity;

import com.flowpay.banking.enums.TransactionStatus;
import com.flowpay.banking.enums.TransactionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidade que representa uma transação financeira
 * Implementa lógica ACID completa para garantir consistência
 */
@Entity
@Table(name = "transactions",
    indexes = {
        @Index(name = "idx_transaction_source", columnList = "source_account_id"),
        @Index(name = "idx_transaction_dest", columnList = "destination_account_id"),
        @Index(name = "idx_transaction_date", columnList = "createdAt"),
        @Index(name = "idx_transaction_hash", columnList = "transactionHash"),
        @Index(name = "idx_transaction_idempotency", columnList = "idempotencyKey")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    @Builder.Default
    private String transactionHash = UUID.randomUUID().toString();

    /**
     * Chave de idempotência para evitar transações duplicadas
     */
    @Column(unique = true, length = 64)
    private String idempotencyKey;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_account_id")
    private Account sourceAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_account_id")
    private Account destinationAccount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @NotNull(message = "Tipo de transação é obrigatório")
    private TransactionType type;

    @Column(nullable = false, precision = 15, scale = 2)
    @NotNull(message = "Valor é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private TransactionStatus status = TransactionStatus.PENDING;

    @Column(length = 500)
    private String description;

    /**
     * Saldo da conta origem ANTES da transação
     */
    @Column(precision = 15, scale = 2)
    private BigDecimal balanceBefore;

    /**
     * Saldo da conta origem DEPOIS da transação
     */
    @Column(precision = 15, scale = 2)
    private BigDecimal balanceAfter;

    /**
     * Taxa/tarifa cobrada na transação
     */
    @Column(precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal fee = BigDecimal.ZERO;

    /**
     * Referência para transação de estorno (se aplicável)
     */
    @OneToOne
    @JoinColumn(name = "reversed_transaction_id")
    private Transaction reversedTransaction;

    /**
     * IP do usuário que iniciou a transação
     */
    @Column(length = 45)
    private String ipAddress;

    /**
     * Device/User-Agent
     */
    @Column(length = 255)
    private String userAgent;

    /**
     * Motivo de falha (se status = FAILED)
     */
    @Column(length = 500)
    private String failureReason;

    /**
     * Score de risco da transação (anti-fraude)
     */
    @Column
    private Integer riskScore;

    /**
     * Data/hora agendada para processamento
     */
    @Column
    private LocalDateTime scheduledAt;

    /**
     * Data/hora em que foi processada
     */
    @Column
    private LocalDateTime processedAt;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Verifica se é uma transação de crédito (entrada de dinheiro)
     */
    @Transient
    public boolean isCredit() {
        return type == TransactionType.DEPOSIT ||
               type == TransactionType.LOAN_DISBURSEMENT ||
               type == TransactionType.WITHDRAWAL_INVESTMENT ||
               type == TransactionType.REVERSAL;
    }

    /**
     * Verifica se é uma transação de débito (saída de dinheiro)
     */
    @Transient
    public boolean isDebit() {
        return type == TransactionType.WITHDRAWAL ||
               type == TransactionType.TRANSFER ||
               type == TransactionType.PIX ||
               type == TransactionType.PAYMENT ||
               type == TransactionType.DEBIT_CARD_PURCHASE ||
               type == TransactionType.CREDIT_CARD_PURCHASE ||
               type == TransactionType.INVESTMENT ||
               type == TransactionType.LOAN_PAYMENT ||
               type == TransactionType.FEE;
    }

    /**
     * Verifica se a transação foi bem-sucedida
     */
    @Transient
    public boolean isCompleted() {
        return status == TransactionStatus.COMPLETED;
    }

    /**
     * Verifica se a transação pode ser estornada
     */
    @Transient
    public boolean canBeReversed() {
        return status == TransactionStatus.COMPLETED &&
               reversedTransaction == null &&
               (type == TransactionType.TRANSFER || type == TransactionType.PIX);
    }
}
