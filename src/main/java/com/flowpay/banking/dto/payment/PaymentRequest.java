package com.flowpay.banking.dto.payment;

import com.flowpay.banking.enums.PaymentType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para requisição de pagamento
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {

    @NotNull(message = "Conta é obrigatória")
    private Long accountId;

    @NotNull(message = "Tipo de pagamento é obrigatório")
    private PaymentType paymentType;

    @NotBlank(message = "Código de barras é obrigatório")
    private String barcode;

    @NotNull(message = "Valor é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
    private BigDecimal valor;

    private LocalDate dueDate;

    @Future(message = "Data de agendamento deve ser no futuro")
    private LocalDate scheduledDate;

    private String descricao;
    private String idempotencyKey;
}
