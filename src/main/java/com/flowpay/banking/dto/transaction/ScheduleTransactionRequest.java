package com.flowpay.banking.dto.transaction;

import com.flowpay.banking.enums.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para agendamento de transação
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleTransactionRequest {

    @NotNull(message = "Conta de origem é obrigatória")
    private Long sourceAccountId;

    @NotNull(message = "Conta de destino é obrigatória")
    private Long destinationAccountId;

    @NotNull(message = "Valor é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
    private BigDecimal valor;

    @NotNull(message = "Tipo de transação é obrigatório")
    private TransactionType tipo;

    @NotNull(message = "Data de agendamento é obrigatória")
    @Future(message = "Data de agendamento deve ser no futuro")
    private LocalDateTime scheduledDate;

    private String descricao;
    private boolean isRecurring;
    private String recurrencePattern; // "DAILY", "WEEKLY", "MONTHLY"
}
