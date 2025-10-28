package com.flowpay.banking.entity;

import com.flowpay.banking.enums.PixKeyType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entidade que representa uma chave PIX
 */
@Entity
@Table(name = "pix_keys",
    indexes = {
        @Index(name = "idx_pix_key_value", columnList = "keyValue"),
        @Index(name = "idx_pix_account", columnList = "account_id")
    },
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"keyValue"})
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PixKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    @NotNull(message = "Conta é obrigatória")
    private Account account;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @NotNull(message = "Tipo de chave é obrigatório")
    private PixKeyType keyType;

    @Column(nullable = false, unique = true, length = 100)
    @NotBlank(message = "Valor da chave é obrigatório")
    private String keyValue;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

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
    public String getKeyFormatted() {
        switch (keyType) {
            case CPF:
                return keyValue.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
            case PHONE:
                return keyValue.replaceAll("(\\d{2})(\\d{5})(\\d{4})", "($1) $2-$3");
            default:
                return keyValue;
        }
    }
}
