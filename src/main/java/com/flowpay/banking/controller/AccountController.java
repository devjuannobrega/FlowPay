package com.flowpay.banking.controller;

import com.flowpay.banking.dto.account.AccountResponse;
import com.flowpay.banking.dto.account.CreateAccountRequest;
import com.flowpay.banking.dto.account.UpdateLimitRequest;
import com.flowpay.banking.service.AccountService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller para gestão de contas
 */
@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@Tag(name = "Contas", description = "Gestão de contas bancárias - criação, consulta, bloqueio e fechamento")
@SecurityRequirement(name = "bearerAuth")
public class AccountController {

    private final AccountService accountService;

    /**
     * Cria uma nova conta
     * POST /api/accounts
     */
    @Operation(
        summary = "Criar nova conta bancária",
        description = "Cria uma nova conta bancária para o usuário autenticado com tipo de conta especificado (CORRENTE, POUPANCA, etc)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Conta criada com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AccountResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "id": 1,
                      "userId": 1,
                      "agencia": "0001",
                      "numero": "12345678",
                      "digito": "9",
                      "tipo": "CORRENTE",
                      "saldo": 0.00,
                      "limiteDisponivel": 1000.00,
                      "limiteDiario": 1000.00,
                      "status": "ATIVA",
                      "createdAt": "2025-10-27T10:30:00"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Dados inválidos ou usuário já possui conta deste tipo",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                    {
                      "timestamp": "2025-10-27T10:30:00",
                      "status": 400,
                      "error": "Bad Request",
                      "message": "Usuário já possui uma conta corrente ativa",
                      "path": "/api/accounts"
                    }
                    """
                )
            )
        )
    })
    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody CreateAccountRequest request) {
        AccountResponse response = accountService.createAccount(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Retorna todas as contas do usuário autenticado
     * GET /api/accounts/my
     */
    @Operation(
        summary = "Listar minhas contas",
        description = "Retorna todas as contas bancárias do usuário autenticado"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista de contas retornada com sucesso",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                    [
                      {
                        "id": 1,
                        "userId": 1,
                        "agencia": "0001",
                        "numero": "12345678",
                        "digito": "9",
                        "tipo": "CORRENTE",
                        "saldo": 1500.00,
                        "limiteDisponivel": 800.00,
                        "limiteDiario": 1000.00,
                        "status": "ATIVA",
                        "createdAt": "2025-10-27T10:30:00"
                      },
                      {
                        "id": 2,
                        "userId": 1,
                        "agencia": "0001",
                        "numero": "87654321",
                        "digito": "0",
                        "tipo": "POUPANCA",
                        "saldo": 5000.00,
                        "limiteDisponivel": 0.00,
                        "limiteDiario": 0.00,
                        "status": "ATIVA",
                        "createdAt": "2025-10-27T11:00:00"
                      }
                    ]
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Não autenticado",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                    {
                      "timestamp": "2025-10-27T10:30:00",
                      "status": 401,
                      "error": "Unauthorized",
                      "message": "Token de autenticação inválido ou expirado"
                    }
                    """
                )
            )
        )
    })
    @GetMapping("/my")
    public ResponseEntity<List<AccountResponse>> getMyAccounts() {
        List<AccountResponse> response = accountService.getMyAccounts();
        return ResponseEntity.ok(response);
    }

    /**
     * Retorna informações de uma conta específica
     * GET /api/accounts/{accountId}
     */
    @Operation(
        summary = "Buscar conta por ID",
        description = "Retorna informações detalhadas de uma conta específica pelo ID"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Conta encontrada com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AccountResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "id": 1,
                      "userId": 1,
                      "agencia": "0001",
                      "numero": "12345678",
                      "digito": "9",
                      "tipo": "CORRENTE",
                      "saldo": 1500.00,
                      "limiteDisponivel": 800.00,
                      "limiteDiario": 1000.00,
                      "status": "ATIVA",
                      "createdAt": "2025-10-27T10:30:00"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Conta não encontrada ou usuário sem permissão",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                    {
                      "timestamp": "2025-10-27T10:30:00",
                      "status": 400,
                      "error": "Bad Request",
                      "message": "Conta não encontrada",
                      "path": "/api/accounts/1"
                    }
                    """
                )
            )
        )
    })
    @GetMapping("/{accountId}")
    public ResponseEntity<AccountResponse> getAccountById(
        @Parameter(description = "ID da conta bancária", required = true, example = "1")
        @PathVariable Long accountId
    ) {
        AccountResponse response = accountService.getAccountById(accountId);
        return ResponseEntity.ok(response);
    }

    /**
     * Retorna informações de uma conta por número
     * GET /api/accounts/find?agencia=0001&numero=12345678&digito=9
     */
    @Operation(
        summary = "Buscar conta por número",
        description = "Retorna informações de uma conta usando agência, número e dígito"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Conta encontrada",
            content = @Content(schema = @Schema(implementation = AccountResponse.class))),
        @ApiResponse(responseCode = "400", description = "Conta não encontrada",
            content = @Content(examples = @ExampleObject(value = "{\"message\": \"Conta não encontrada\"}")))
    })
    @GetMapping("/find")
    public ResponseEntity<AccountResponse> getAccountByNumber(
            @Parameter(description = "Código da agência", required = true, example = "0001") @RequestParam String agencia,
            @Parameter(description = "Número da conta", required = true, example = "12345678") @RequestParam String numero,
            @Parameter(description = "Dígito verificador", required = true, example = "9") @RequestParam String digito) {
        AccountResponse response = accountService.getAccountByNumber(agencia, numero, digito);
        return ResponseEntity.ok(response);
    }

    /**
     * Atualiza o limite diário
     * PATCH /api/accounts/{accountId}/limit
     */
    @Operation(
        summary = "Atualizar limite diário",
        description = "Atualiza o limite diário de transações da conta"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Limite atualizado com sucesso",
            content = @Content(schema = @Schema(implementation = AccountResponse.class))),
        @ApiResponse(responseCode = "400", description = "Limite inválido",
            content = @Content(examples = @ExampleObject(value = "{\"message\": \"Limite deve ser positivo\"}")))
    })
    @PatchMapping("/{accountId}/limit")
    public ResponseEntity<AccountResponse> updateLimit(
            @Parameter(description = "ID da conta", required = true, example = "1") @PathVariable Long accountId,
            @Valid @RequestBody UpdateLimitRequest request) {
        AccountResponse response = accountService.updateLimit(accountId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Bloqueia uma conta
     * POST /api/accounts/{accountId}/block
     */
    @Operation(
        summary = "Bloquear conta",
        description = "Bloqueia uma conta temporariamente impedindo transações"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Conta bloqueada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Erro ao bloquear conta",
            content = @Content(examples = @ExampleObject(value = "{\"message\": \"Conta já está bloqueada\"}")))
    })
    @PostMapping("/{accountId}/block")
    public ResponseEntity<Void> blockAccount(
        @Parameter(description = "ID da conta", required = true, example = "1") @PathVariable Long accountId
    ) {
        accountService.blockAccount(accountId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Desbloqueia uma conta
     * POST /api/accounts/{accountId}/unblock
     */
    @Operation(
        summary = "Desbloquear conta",
        description = "Desbloqueia uma conta previamente bloqueada"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Conta desbloqueada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Erro ao desbloquear conta",
            content = @Content(examples = @ExampleObject(value = "{\"message\": \"Conta não está bloqueada\"}")))
    })
    @PostMapping("/{accountId}/unblock")
    public ResponseEntity<Void> unblockAccount(
        @Parameter(description = "ID da conta", required = true, example = "1") @PathVariable Long accountId
    ) {
        accountService.unblockAccount(accountId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Fecha uma conta
     * DELETE /api/accounts/{accountId}
     */
    @Operation(
        summary = "Fechar conta",
        description = "Fecha definitivamente uma conta bancária (saldo deve ser zero)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Conta fechada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Não é possível fechar a conta",
            content = @Content(examples = @ExampleObject(
                value = "{\"message\": \"Não é possível fechar conta com saldo ou pendências\"}")))
    })
    @DeleteMapping("/{accountId}")
    public ResponseEntity<Void> closeAccount(
        @Parameter(description = "ID da conta", required = true, example = "1") @PathVariable Long accountId
    ) {
        accountService.closeAccount(accountId);
        return ResponseEntity.noContent().build();
    }
}
