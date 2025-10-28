package com.flowpay.banking.entity;

import com.flowpay.banking.enums.TransactionStatus;
import com.flowpay.banking.enums.TransactionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidade que representa transações agendadas
 */
@Entity
@Table(name = "scheduled_transactions",
    indexes = {
        @Index(name = "idx_scheduled_account", columnList = "source_account_id"),
        @Index(name = "idx_scheduled_date", columnList = "scheduledDate"),
        @Index(name = "idx_scheduled_status", columnList = "status")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduledTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_account_id", nullable = false)
    @NotNull(message = "Conta de origem é obrigatória")
    private Account sourceAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_account_id")
    private Account destinationAccount;

    @OneToOne
    @JoinColumn(name = "executed_transaction_id")
    private Transaction executedTransaction;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @NotNull(message = "Tipo de transação é obrigatório")
    private TransactionType transactionType;

    @Column(nullable = false, precision = 15, scale = 2)
    @NotNull(message = "Valor é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
    private BigDecimal amount;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    @NotNull(message = "Data agendada é obrigatória")
    private LocalDate scheduledDate;

    /**
     * Para transações recorrentes (DAILY, WEEKLY, MONTHLY, YEARLY)
     */
    @Column(length = 20)
    private String recurrenceType;

    /**
     * Data final para transações recorrentes
     */
    @Column
    private LocalDate recurrenceEndDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private TransactionStatus status = TransactionStatus.PENDING;

    /**
     * Dados adicionais específicos do tipo de transação (JSON)
     */
    @Column(columnDefinition = "TEXT")
    private String additionalData;

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
    public boolean shouldExecute() {
        return status == TransactionStatus.PENDING &&
               !LocalDate.now().isBefore(scheduledDate);
    }

    @Transient
    public boolean isRecurring() {
        return recurrenceType != null;
    }
}
