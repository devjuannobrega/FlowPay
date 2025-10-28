package com.flowpay.banking.dto.card;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para atualização de limite do cartão
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCardLimitRequest {

    @NotNull(message = "Novo limite é obrigatório")
    @DecimalMin(value = "0.00", message = "Limite deve ser positivo")
    private BigDecimal novoLimite;
}
