package com.flowpay.banking.entity;

import com.flowpay.banking.enums.LoanStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * Entidade que representa um empréstimo
 */
@Entity
@Table(name = "loans",
    indexes = {
        @Index(name = "idx_loan_account", columnList = "account_id"),
        @Index(name = "idx_loan_status", columnList = "status")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    @NotNull(message = "Conta é obrigatória")
    private Account account;

    @OneToOne
    @JoinColumn(name = "disbursement_transaction_id")
    private Transaction disbursementTransaction;

    @Column(nullable = false, precision = 15, scale = 2)
    @NotNull(message = "Valor solicitado é obrigatório")
    @DecimalMin(value = "100.00", message = "Valor mínimo é R$ 100,00")
    private BigDecimal requestedAmount;

    @Column(precision = 15, scale = 2)
    private BigDecimal approvedAmount;

    /**
     * Taxa de juros mensal (%)
     */
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal monthlyInterestRate;

    /**
     * Número de parcelas
     */
    @Column(nullable = false)
    @Min(value = 1, message = "Número mínimo de parcelas é 1")
    @Max(value = 60, message = "Número máximo de parcelas é 60")
    private Integer installments;

    /**
     * Valor de cada parcela
     */
    @Column(precision = 15, scale = 2)
    private BigDecimal installmentAmount;

    /**
     * Valor total a ser pago (principal + juros)
     */
    @Column(precision = 15, scale = 2)
    private BigDecimal totalAmount;

    /**
     * Valor já pago
     */
    @Column(precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal paidAmount = BigDecimal.ZERO;

    /**
     * Score de crédito do solicitante (300-1000)
     */
    @Column
    @Min(value = 300)
    @Max(value = 1000)
    private Integer creditScore;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private LoanStatus status = LoanStatus.PENDING;

    @Column
    private LocalDate applicationDate;

    @Column
    private LocalDate approvalDate;

    @Column
    private LocalDate disbursementDate;

    @Column
    private LocalDate firstDueDate;

    @Column(length = 500)
    private String rejectionReason;

    @Column(nullable = false)
    @Builder.Default
    private Boolean deleted = false;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<LoanInstallment> loanInstallments;

    @Transient
    public BigDecimal getRemainingAmount() {
        return totalAmount.subtract(paidAmount);
    }

    @Transient
    public BigDecimal getTotalInterest() {
        return totalAmount.subtract(approvedAmount != null ? approvedAmount : BigDecimal.ZERO);
    }

    @Transient
    public int getPaidInstallments() {
        if (loanInstallments == null) return 0;
        return (int) loanInstallments.stream()
                .filter(installment -> installment.getPaymentDate() != null)
                .count();
    }

    @Transient
    public int getRemainingInstallments() {
        return installments - getPaidInstallments();
    }
}
