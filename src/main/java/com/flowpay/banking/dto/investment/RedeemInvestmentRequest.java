package com.flowpay.banking.dto.investment;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para resgate de investimento
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RedeemInvestmentRequest {

    @NotNull(message = "ID do investimento é obrigatório")
    private Long investmentId;

    private boolean isPartial;
    private java.math.BigDecimal valorResgate; // Para resgate parcial
}
