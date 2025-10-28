package com.flowpay.banking.entity;

import com.flowpay.banking.enums.TransactionStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidade que representa uma parcela de empréstimo
 */
@Entity
@Table(name = "loan_installments",
    indexes = {
        @Index(name = "idx_installment_loan", columnList = "loan_id"),
        @Index(name = "idx_installment_due_date", columnList = "dueDate")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanInstallment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_id", nullable = false)
    @NotNull(message = "Empréstimo é obrigatório")
    private Loan loan;

    @OneToOne
    @JoinColumn(name = "payment_transaction_id")
    private Transaction paymentTransaction;

    @Column(nullable = false)
    @Min(value = 1, message = "Número da parcela deve ser maior que zero")
    private Integer installmentNumber;

    @Column(nullable = false, precision = 15, scale = 2)
    @NotNull(message = "Valor da parcela é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
    private BigDecimal amount;

    /**
     * Parte do valor que é principal (amortização)
     */
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal principalAmount;

    /**
     * Parte do valor que é juros
     */
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal interestAmount;

    /**
     * Multa por atraso
     */
    @Column(precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal latePaymentFee = BigDecimal.ZERO;

    /**
     * Juros de mora (por dia de atraso)
     */
    @Column(precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal lateInterest = BigDecimal.ZERO;

    @Column(nullable = false)
    @NotNull(message = "Data de vencimento é obrigatória")
    private LocalDate dueDate;

    @Column
    private LocalDate paymentDate;

    @Column(precision = 15, scale = 2)
    private BigDecimal paidAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private TransactionStatus status = TransactionStatus.PENDING;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Transient
    public boolean isOverdue() {
        return dueDate.isBefore(LocalDate.now()) && status != TransactionStatus.COMPLETED;
    }

    @Transient
    public long getDaysOverdue() {
        if (!isOverdue()) return 0;
        return java.time.temporal.ChronoUnit.DAYS.between(dueDate, LocalDate.now());
    }

    @Transient
    public BigDecimal getTotalAmountDue() {
        return amount.add(latePaymentFee).add(lateInterest);
    }
}
