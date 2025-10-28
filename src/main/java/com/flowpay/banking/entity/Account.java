package com.flowpay.banking.entity;

import com.flowpay.banking.enums.AccountStatus;
import com.flowpay.banking.enums.AccountType;
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
 * Entidade que representa uma conta bancária no FlowPay
 */
@Entity
@Table(name = "accounts",
    indexes = {
        @Index(name = "idx_account_number", columnList = "accountNumber, agencia"),
        @Index(name = "idx_account_user", columnList = "user_id")
    },
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"accountNumber", "agencia"})
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "Usuário é obrigatório")
    private User user;

    @Column(nullable = false, length = 10)
    @NotBlank(message = "Número da conta é obrigatório")
    private String accountNumber;

    @Column(nullable = false, length = 4)
    @NotBlank(message = "Agência é obrigatória")
    @Builder.Default
    private String agencia = "0001";

    @Column(nullable = false, length = 1)
    @NotBlank(message = "Dígito verificador é obrigatório")
    private String digitoVerificador;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @NotNull(message = "Tipo de conta é obrigatório")
    private AccountType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private AccountStatus status = AccountStatus.ACTIVE;

    @Column(nullable = false, precision = 15, scale = 2)
    @NotNull(message = "Saldo é obrigatório")
    @DecimalMin(value = "0.00", message = "Saldo não pode ser negativo")
    @Builder.Default
    private BigDecimal saldo = BigDecimal.ZERO;

    @Column(nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal saldoBloqueado = BigDecimal.ZERO;

    @Column(nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal limiteDiario = new BigDecimal("5000.00");

    @Column(nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal limiteUtilizadoHoje = BigDecimal.ZERO;

    @Column
    private LocalDate dataUltimaAtualizacaoLimite;

    /**
     * Data de aniversário da conta poupança (para cálculo de rendimento)
     */
    @Column
    private Integer diaAniversario;

    @Column(nullable = false)
    @Builder.Default
    private Boolean deleted = false;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "sourceAccount", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Transaction> transactionsFrom;

    @OneToMany(mappedBy = "destinationAccount", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Transaction> transactionsTo;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<PixKey> pixKeys;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Card> cards;

    /**
     * Retorna o saldo disponível (saldo - saldo bloqueado)
     */
    @Transient
    public BigDecimal getSaldoDisponivel() {
        return saldo.subtract(saldoBloqueado);
    }

    /**
     * Retorna o limite disponível hoje
     */
    @Transient
    public BigDecimal getLimiteDisponivel() {
        // Resetar limite se for um novo dia
        if (dataUltimaAtualizacaoLimite == null || !dataUltimaAtualizacaoLimite.equals(LocalDate.now())) {
            return limiteDiario;
        }
        return limiteDiario.subtract(limiteUtilizadoHoje);
    }

    /**
     * Retorna a conta formatada (ex: 0001/12345-6)
     */
    @Transient
    public String getContaFormatada() {
        return String.format("%s/%s-%s", agencia, accountNumber, digitoVerificador);
    }

    /**
     * Verifica se a conta está ativa
     */
    @Transient
    public boolean isAtiva() {
        return status == AccountStatus.ACTIVE;
    }
}
