package com.flowpay.banking.entity;

import com.flowpay.banking.enums.InvestmentStatus;
import com.flowpay.banking.enums.InvestmentType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidade que representa um investimento
 */
@Entity
@Table(name = "investments",
    indexes = {
        @Index(name = "idx_investment_account", columnList = "account_id"),
        @Index(name = "idx_investment_status", columnList = "status")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Investment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    @NotNull(message = "Conta é obrigatória")
    private Account account;

    @OneToOne
    @JoinColumn(name = "application_transaction_id")
    private Transaction applicationTransaction;

    @OneToOne
    @JoinColumn(name = "redemption_transaction_id")
    private Transaction redemptionTransaction;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @NotNull(message = "Tipo de investimento é obrigatório")
    private InvestmentType investmentType;

    @Column(nullable = false, precision = 15, scale = 2)
    @NotNull(message = "Valor investido é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
    private BigDecimal initialAmount;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal currentAmount;

    /**
     * Taxa de rendimento anual (%)
     */
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal annualRate;

    /**
     * Dia do mês para cálculo de rendimento (poupança)
     */
    @Column
    private Integer anniversaryDay;

    @Column
    private LocalDate applicationDate;

    @Column
    private LocalDate maturityDate;

    @Column
    private LocalDate redemptionDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private InvestmentStatus status = InvestmentStatus.ACTIVE;

    /**
     * Rendimento total acumulado
     */
    @Column(precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal totalYield = BigDecimal.ZERO;

    /**
     * Último cálculo de rendimento
     */
    @Column
    private LocalDate lastYieldCalculation;

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
    public BigDecimal getProfitability() {
        if (initialAmount.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return currentAmount.subtract(initialAmount);
    }

    @Transient
    public BigDecimal getProfitabilityPercentage() {
        if (initialAmount.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return getProfitability()
                .divide(initialAmount, 4, java.math.RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));
    }

    @Transient
    public boolean canRedeem() {
        return status == InvestmentStatus.ACTIVE &&
               (maturityDate == null || !LocalDate.now().isBefore(maturityDate));
    }
}
