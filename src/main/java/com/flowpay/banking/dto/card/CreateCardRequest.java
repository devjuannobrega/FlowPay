package com.flowpay.banking.dto.card;

import com.flowpay.banking.enums.CardType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para criação de cartão
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCardRequest {

    @NotNull(message = "Conta é obrigatória")
    private Long accountId;

    @NotNull(message = "Tipo de cartão é obrigatório")
    private CardType cardType;

    @DecimalMin(value = "0.00", message = "Limite deve ser positivo")
    private BigDecimal creditLimit;

    private boolean isVirtual;
}
