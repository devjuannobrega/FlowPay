package com.flowpay.banking.entity;

import com.flowpay.banking.enums.CardStatus;
import com.flowpay.banking.enums.CardType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidade que representa um cartão bancário
 */
@Entity
@Table(name = "cards",
    indexes = {
        @Index(name = "idx_card_number", columnList = "cardNumber"),
        @Index(name = "idx_card_account", columnList = "account_id")
    },
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"cardNumber"})
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    @NotNull(message = "Conta é obrigatória")
    private Account account;

    @Column(nullable = false, unique = true, length = 16)
    @NotBlank(message = "Número do cartão é obrigatório")
    @Pattern(regexp = "\\d{16}", message = "Número do cartão deve ter 16 dígitos")
    private String cardNumber;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "Nome no cartão é obrigatório")
    private String cardHolderName;

    @Column(nullable = false, length = 3)
    @NotBlank(message = "CVV é obrigatório")
    private String cvv; // Deve ser criptografado

    @Column(nullable = false)
    @NotNull(message = "Data de expiração é obrigatória")
    private LocalDate expiryDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @NotNull(message = "Tipo de cartão é obrigatório")
    private CardType cardType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private CardStatus status = CardStatus.PENDING_ACTIVATION;

    /**
     * Limite para cartão de crédito
     */
    @Column(precision = 15, scale = 2)
    private BigDecimal creditLimit;

    /**
     * Limite utilizado no ciclo atual
     */
    @Column(precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal usedLimit = BigDecimal.ZERO;

    /**
     * Limite para compras online
     */
    @Column(precision = 15, scale = 2)
    private BigDecimal onlineLimit;

    /**
     * Limite para compras internacionais
     */
    @Column(precision = 15, scale = 2)
    private BigDecimal internationalLimit;

    /**
     * Limite para saque
     */
    @Column(precision = 15, scale = 2)
    private BigDecimal withdrawalLimit;

    @Column(nullable = false)
    @Builder.Default
    private Boolean contactless = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean internationalPurchases = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean onlinePurchases = true;

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
    public String getMaskedCardNumber() {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "****";
        }
        return "**** **** **** " + cardNumber.substring(12);
    }

    @Transient
    public BigDecimal getAvailableLimit() {
        if (creditLimit == null) return BigDecimal.ZERO;
        return creditLimit.subtract(usedLimit);
    }

    @Transient
    public boolean isExpired() {
        return expiryDate != null && expiryDate.isBefore(LocalDate.now());
    }

    @Transient
    public boolean isActive() {
        return status == CardStatus.ACTIVE && !isExpired();
    }
}
