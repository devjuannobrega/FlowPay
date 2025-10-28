package com.flowpay.banking.dto.transaction;

import com.flowpay.banking.enums.TransactionStatus;
import com.flowpay.banking.enums.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para resposta de transação
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Informações detalhadas de uma transação bancária")
public class TransactionResponse {

    @Schema(description = "ID único da transação", example = "1")
    private Long id;

    @Schema(description = "Hash único da transação para rastreamento", example = "a3f5b2c8d1e9f4a7b6c5d8e2f1a4b7c9")
    private String transactionHash;

    @Schema(description = "Tipo da transação", example = "TRANSFER")
    private TransactionType tipo;

    @Schema(description = "Valor da transação", example = "250.00")
    private BigDecimal valor;

    @Schema(description = "Taxa cobrada na transação", example = "1.50")
    private BigDecimal taxa;

    @Schema(description = "Descrição ou observação da transação", example = "Pagamento de aluguel")
    private String descricao;

    @Schema(description = "Status atual da transação", example = "COMPLETED")
    private TransactionStatus status;

    // Contas envolvidas
    @Schema(description = "ID da conta de origem", example = "1")
    private Long sourceAccountId;

    @Schema(description = "Número da conta de origem", example = "123456-7")
    private String sourceAccountNumber;

    @Schema(description = "ID da conta de destino", example = "2")
    private Long destinationAccountId;

    @Schema(description = "Número da conta de destino", example = "654321-9")
    private String destinationAccountNumber;

    // Saldos
    @Schema(description = "Saldo da conta antes da transação", example = "1500.00")
    private BigDecimal balanceBefore;

    @Schema(description = "Saldo da conta após a transação", example = "1248.50")
    private BigDecimal balanceAfter;

    // Metadados
    @Schema(description = "Pontuação de risco da transação (0-100)", example = "15")
    private Integer riskScore;

    @Schema(description = "Data e hora de criação da transação", example = "2024-10-27T14:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Data e hora do processamento da transação", example = "2024-10-27T14:30:05")
    private LocalDateTime processedAt;

    // Estorno
    @Schema(description = "Indica se a transação foi estornada", example = "false")
    private boolean isReversed;

    @Schema(description = "ID da transação de estorno (se aplicável)", example = "null")
    private Long reversedTransactionId;
}
