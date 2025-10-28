package com.flowpay.banking.controller;

import com.flowpay.banking.dto.transaction.*;
import com.flowpay.banking.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller para transações bancárias
 */
@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Tag(name = "Transações", description = "Operações bancárias: depósitos, saques, transferências e histórico")
@SecurityRequirement(name = "bearerAuth")
public class TransactionController {

    private final TransactionService transactionService;

    /**
     * Realiza um depósito
     * POST /api/transactions/deposit
     */
    @Operation(summary = "Realizar depósito", description = "Deposita dinheiro em uma conta bancária")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Depósito realizado com sucesso",
            content = @Content(schema = @Schema(implementation = TransactionResponse.class),
                examples = @ExampleObject(value = """
                    {"id": 1, "tipo": "DEPOSIT", "valor": 500.00, "status": "COMPLETED",
                     "sourceAccountId": 1, "balanceAfter": 500.00, "createdAt": "2025-10-27T10:30:00"}
                    """))),
        @ApiResponse(responseCode = "400", description = "Dados inválidos",
            content = @Content(examples = @ExampleObject(
                value = "{\"message\": \"Valor do depósito deve ser positivo\"}")))
    })
    @PostMapping("/deposit")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<TransactionResponse> deposit(@Valid @RequestBody DepositRequest request) {
        TransactionResponse response = transactionService.deposit(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Realiza um saque
     * POST /api/transactions/withdrawal
     */
    @Operation(summary = "Realizar saque", description = "Saca dinheiro de uma conta bancária")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Saque realizado com sucesso",
            content = @Content(schema = @Schema(implementation = TransactionResponse.class),
                examples = @ExampleObject(value = """
                    {"id": 2, "tipo": "WITHDRAWAL", "valor": 200.00, "taxa": 2.50, "status": "COMPLETED",
                     "sourceAccountId": 1, "balanceBefore": 500.00, "balanceAfter": 297.50}
                    """))),
        @ApiResponse(responseCode = "400", description = "Saldo insuficiente ou limite excedido",
            content = @Content(examples = @ExampleObject(
                value = "{\"message\": \"Saldo insuficiente para realizar o saque\"}")))
    })
    @PostMapping("/withdrawal")
    public ResponseEntity<TransactionResponse> withdrawal(@Valid @RequestBody WithdrawalRequest request) {
        TransactionResponse response = transactionService.withdrawal(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Realiza uma transferência
     * POST /api/transactions/transfer
     */
    @Operation(summary = "Realizar transferência", description = "Transfere dinheiro entre contas bancárias")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Transferência realizada com sucesso",
            content = @Content(schema = @Schema(implementation = TransactionResponse.class),
                examples = @ExampleObject(value = """
                    {"id": 3, "tipo": "TRANSFER", "valor": 100.00, "taxa": 0.00, "status": "COMPLETED",
                     "sourceAccountId": 1, "destinationAccountId": 2, "balanceAfter": 197.50,
                     "transactionHash": "abc123def456"}
                    """))),
        @ApiResponse(responseCode = "400", description = "Saldo insuficiente ou conta de destino inválida",
            content = @Content(examples = @ExampleObject(
                value = "{\"message\": \"Conta de destino não encontrada\"}")))
    })
    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponse> transfer(@Valid @RequestBody TransferRequest request) {
        TransactionResponse response = transactionService.transfer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Retorna histórico de transações de uma conta
     * GET /api/transactions/account/{accountId}
     */
    @Operation(summary = "Histórico de transações",
        description = "Retorna todas as transações de uma conta, com filtros opcionais por período")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de transações retornada com sucesso",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = """
                    [{"id": 1, "tipo": "DEPOSIT", "valor": 500.00, "status": "COMPLETED"},
                     {"id": 2, "tipo": "WITHDRAWAL", "valor": 200.00, "status": "COMPLETED"}]
                    """))),
        @ApiResponse(responseCode = "400", description = "Conta não encontrada ou sem permissão")
    })
    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<TransactionResponse>> getAccountTransactions(
            @Parameter(description = "ID da conta", example = "1") @PathVariable Long accountId,
            @Parameter(description = "Data inicial (ISO 8601)", example = "2025-10-01T00:00:00")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "Data final (ISO 8601)", example = "2025-10-31T23:59:59")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<TransactionResponse> response = transactionService.getAccountTransactions(accountId, startDate, endDate);
        return ResponseEntity.ok(response);
    }

    /**
     * Retorna detalhes de uma transação específica
     * GET /api/transactions/{transactionId}
     */
    @Operation(summary = "Buscar transação por ID", description = "Retorna detalhes completos de uma transação específica")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transação encontrada",
            content = @Content(schema = @Schema(implementation = TransactionResponse.class))),
        @ApiResponse(responseCode = "400", description = "Transação não encontrada",
            content = @Content(examples = @ExampleObject(value = "{\"message\": \"Transação não encontrada\"}")))
    })
    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionResponse> getTransactionById(
        @Parameter(description = "ID da transação", example = "1") @PathVariable Long transactionId
    ) {
        TransactionResponse response = transactionService.getTransactionById(transactionId);
        return ResponseEntity.ok(response);
    }

    /**
     * Estorna uma transação
     * POST /api/transactions/{transactionId}/reverse
     */
    @Operation(summary = "Estornar transação",
        description = "Reverte uma transação (apenas ADMIN). Cria transação de estorno e restaura saldos")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transação estornada com sucesso",
            content = @Content(schema = @Schema(implementation = TransactionResponse.class),
                examples = @ExampleObject(value = """
                    {"id": 4, "tipo": "REVERSAL", "valor": 100.00, "status": "COMPLETED",
                     "reversedTransactionId": 3, "isReversed": false}
                    """))),
        @ApiResponse(responseCode = "400", description = "Não é possível estornar esta transação",
            content = @Content(examples = @ExampleObject(
                value = "{\"message\": \"Transação já foi estornada anteriormente\"}")))
    })
    @PostMapping("/{transactionId}/reverse")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TransactionResponse> reverseTransaction(
        @Parameter(description = "ID da transação a estornar", example = "3") @PathVariable Long transactionId
    ) {
        TransactionResponse response = transactionService.reverseTransaction(transactionId);
        return ResponseEntity.ok(response);
    }
}
