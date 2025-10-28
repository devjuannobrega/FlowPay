package com.flowpay.banking.dto.loan;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para pagamento de parcela de empréstimo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayLoanInstallmentRequest {

    @NotNull(message = "ID da parcela é obrigatório")
    private Long installmentId;

    @NotNull(message = "Conta é obrigatória")
    private Long accountId;

    private String idempotencyKey;
}
