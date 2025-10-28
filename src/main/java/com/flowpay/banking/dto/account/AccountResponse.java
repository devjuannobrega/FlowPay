package com.flowpay.banking.dto.account;

import com.flowpay.banking.enums.AccountStatus;
import com.flowpay.banking.enums.AccountType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO para resposta com informações da conta
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Informações detalhadas da conta bancária")
public class AccountResponse {

    @Schema(description = "ID único da conta", example = "1")
    private Long id;

    @Schema(description = "Número da agência bancária", example = "0001")
    private String agencia;

    @Schema(description = "Número da conta", example = "123456")
    private String numero;

    @Schema(description = "Dígito verificador da conta", example = "7")
    private String digitoVerificador;

    @Schema(description = "Tipo da conta bancária", example = "CORRENTE")
    private AccountType tipo;

    @Schema(description = "Saldo total da conta", example = "1500.50")
    private BigDecimal saldo;

    @Schema(description = "Saldo disponível para uso", example = "1450.50")
    private BigDecimal saldoDisponivel;

    @Schema(description = "Saldo bloqueado temporariamente", example = "50.00")
    private BigDecimal saldoBloqueado;

    @Schema(description = "Limite diário para transações", example = "5000.00")
    private BigDecimal limiteDiario;

    @Schema(description = "Status atual da conta", example = "ACTIVE")
    private AccountStatus status;

    @Schema(description = "Data de aniversário da poupança (apenas para contas poupança)", example = "2024-01-15")
    private LocalDate dataAniversarioPoupanca;

    @Schema(description = "Data e hora de criação da conta", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Data e hora da última atualização", example = "2024-10-27T14:20:00")
    private LocalDateTime updatedAt;
}
