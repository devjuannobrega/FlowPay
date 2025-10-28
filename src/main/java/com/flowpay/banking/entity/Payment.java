package com.flowpay.banking.entity;

import com.flowpay.banking.enums.PaymentType;
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
 * Entidade que representa um pagamento (boleto, conta, etc.)
 */
@Entity
@Table(name = "payments",
    indexes = {
        @Index(name = "idx_payment_account", columnList = "account_id"),
        @Index(name = "idx_payment_barcode", columnList = "barcode"),
        @Index(name = "idx_payment_due_date", columnList = "dueDate")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    @NotNull(message = "Conta é obrigatória")
    private Account account;

    @OneToOne
    @JoinColumn(name = "transaction_id")
    private Transaction transaction;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @NotNull(message = "Tipo de pagamento é obrigatório")
    private PaymentType paymentType;

    @Column(length = 48)
    private String barcode; // Código de barras do boleto

    @Column(nullable = false, precision = 15, scale = 2)
    @NotNull(message = "Valor é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
    private BigDecimal amount;

    @Column(precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal discount = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal fine = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal interest = BigDecimal.ZERO;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal totalAmount;

    @Column
    private LocalDate dueDate;

    @Column
    private LocalDate paymentDate;

    @Column(length = 200)
    private String beneficiario;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private TransactionStatus status = TransactionStatus.PENDING;

    /**
     * Para pagamentos agendados
     */
    @Column
    private LocalDate scheduledDate;

    /**
     * Para pagamentos recorrentes
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean recurring = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean deleted = false;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Calcula multa e juros se pagamento em atraso
     */
    @PrePersist
    @PreUpdate
    public void calculateTotal() {
        BigDecimal total = amount.add(fine).add(interest).subtract(discount);
        this.totalAmount = total;
    }

    @Transient
    public boolean isOverdue() {
        return dueDate != null && dueDate.isBefore(LocalDate.now()) &&
               status != TransactionStatus.COMPLETED;
    }

    @Transient
    public long getDaysOverdue() {
        if (!isOverdue()) return 0;
        return java.time.temporal.ChronoUnit.DAYS.between(dueDate, LocalDate.now());
    }
}
