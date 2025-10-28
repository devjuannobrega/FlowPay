package com.flowpay.banking.controller;

import com.flowpay.banking.dto.loan.*;
import com.flowpay.banking.service.LoanService;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller para gestão de empréstimos
 */
@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
@Tag(name = "Empréstimos", description = "Endpoints para solicitação e gestão de empréstimos e parcelas")
@SecurityRequirement(name = "bearerAuth")
public class LoanController {

    private final LoanService loanService;

    /**
     * Solicita um novo empréstimo
     * POST /api/loans/apply
     */
    @Operation(
        summary = "Solicitar empréstimo",
        description = "Cria uma nova solicitação de empréstimo que será analisada pelo sistema"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Empréstimo solicitado com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = LoanResponse.class),
                examples = @ExampleObject(
                    name = "Empréstimo criado",
                    value = """
                    {
                      "id": 1,
                      "userId": 1,
                      "accountId": 1,
                      "amount": 10000.00,
                      "interestRate": 2.5,
                      "installments": 12,
                      "installmentValue": 895.42,
                      "totalAmount": 10745.04,
                      "status": "PENDING",
                      "applicationDate": "2025-10-27T10:30:00",
                      "approvalDate": null,
                      "disbursementDate": null
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Dados inválidos ou usuário não elegível",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Erro de validação",
                    value = """
                    {
                      "timestamp": "2025-10-27T10:30:00",
                      "status": 400,
                      "error": "Bad Request",
                      "message": "Usuário possui empréstimo pendente ou inadimplente",
                      "path": "/api/loans/apply"
                    }
                    """
                )
            )
        )
    })
    @PostMapping("/apply")
    public ResponseEntity<LoanResponse> applyForLoan(@Valid @RequestBody LoanApplicationRequest request) {
        LoanResponse response = loanService.applyForLoan(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Libera um empréstimo (admin)
     * POST /api/loans/{loanId}/disburse
     */
    @Operation(
        summary = "Liberar empréstimo (Admin)",
        description = "Aprova e libera o valor do empréstimo na conta do usuário (requer permissão de administrador)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Empréstimo liberado com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = LoanResponse.class),
                examples = @ExampleObject(
                    name = "Empréstimo liberado",
                    value = """
                    {
                      "id": 1,
                      "userId": 1,
                      "accountId": 1,
                      "amount": 10000.00,
                      "interestRate": 2.5,
                      "installments": 12,
                      "installmentValue": 895.42,
                      "totalAmount": 10745.04,
                      "status": "ACTIVE",
                      "applicationDate": "2025-10-27T10:30:00",
                      "approvalDate": "2025-10-27T11:00:00",
                      "disbursementDate": "2025-10-27T11:00:00"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Empréstimo não pode ser liberado",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Erro ao liberar",
                    value = """
                    {
                      "timestamp": "2025-10-27T10:30:00",
                      "status": 400,
                      "error": "Bad Request",
                      "message": "Empréstimo não está em status pendente",
                      "path": "/api/loans/1/disburse"
                    }
                    """
                )
            )
        )
    })
    @PostMapping("/{loanId}/disburse")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LoanResponse> disburseLoan(
            @Parameter(description = "ID do empréstimo", required = true)
            @PathVariable Long loanId) {
        LoanResponse response = loanService.disburseLoan(loanId);
        return ResponseEntity.ok(response);
    }

    /**
     * Paga uma parcela do empréstimo
     * POST /api/loans/installments/pay
     */
    @Operation(
        summary = "Pagar parcela de empréstimo",
        description = "Realiza o pagamento de uma parcela específica do empréstimo"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "Parcela paga com sucesso"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Parcela não pode ser paga ou saldo insuficiente",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "Saldo insuficiente",
                        value = """
                        {
                          "timestamp": "2025-10-27T10:30:00",
                          "status": 400,
                          "error": "Bad Request",
                          "message": "Saldo insuficiente para pagamento da parcela",
                          "path": "/api/loans/installments/pay"
                        }
                        """
                    ),
                    @ExampleObject(
                        name = "Parcela já paga",
                        value = """
                        {
                          "timestamp": "2025-10-27T10:30:00",
                          "status": 400,
                          "error": "Bad Request",
                          "message": "Parcela já foi paga anteriormente",
                          "path": "/api/loans/installments/pay"
                        }
                        """
                    )
                }
            )
        )
    })
    @PostMapping("/installments/pay")
    public ResponseEntity<Void> payInstallment(@Valid @RequestBody PayLoanInstallmentRequest request) {
        loanService.payInstallment(request);
        return ResponseEntity.noContent().build();
    }

    /**
     * Lista todos os empréstimos do usuário
     * GET /api/loans/my
     */
    @Operation(
        summary = "Listar meus empréstimos",
        description = "Retorna todos os empréstimos do usuário autenticado"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista de empréstimos retornada com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = LoanResponse.class),
                examples = @ExampleObject(
                    name = "Lista de empréstimos",
                    value = """
                    [
                      {
                        "id": 1,
                        "userId": 1,
                        "accountId": 1,
                        "amount": 10000.00,
                        "interestRate": 2.5,
                        "installments": 12,
                        "installmentValue": 895.42,
                        "totalAmount": 10745.04,
                        "status": "ACTIVE",
                        "applicationDate": "2025-10-27T10:30:00",
                        "approvalDate": "2025-10-27T11:00:00",
                        "disbursementDate": "2025-10-27T11:00:00"
                      },
                      {
                        "id": 2,
                        "userId": 1,
                        "accountId": 1,
                        "amount": 5000.00,
                        "interestRate": 2.0,
                        "installments": 6,
                        "installmentValue": 860.66,
                        "totalAmount": 5163.96,
                        "status": "PAID",
                        "applicationDate": "2025-05-15T14:20:00",
                        "approvalDate": "2025-05-15T15:00:00",
                        "disbursementDate": "2025-05-15T15:00:00"
                      }
                    ]
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Erro ao buscar empréstimos",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Erro ao buscar",
                    value = """
                    {
                      "timestamp": "2025-10-27T10:30:00",
                      "status": 400,
                      "error": "Bad Request",
                      "message": "Erro ao buscar empréstimos do usuário",
                      "path": "/api/loans/my"
                    }
                    """
                )
            )
        )
    })
    @GetMapping("/my")
    public ResponseEntity<List<LoanResponse>> getMyLoans() {
        List<LoanResponse> response = loanService.getMyLoans();
        return ResponseEntity.ok(response);
    }

    /**
     * Lista as parcelas de um empréstimo
     * GET /api/loans/{loanId}/installments
     */
    @Operation(
        summary = "Listar parcelas do empréstimo",
        description = "Retorna todas as parcelas de um empréstimo específico com status de pagamento"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista de parcelas retornada com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = LoanInstallmentResponse.class),
                examples = @ExampleObject(
                    name = "Lista de parcelas",
                    value = """
                    [
                      {
                        "id": 1,
                        "loanId": 1,
                        "installmentNumber": 1,
                        "amount": 895.42,
                        "dueDate": "2025-11-27",
                        "paymentDate": "2025-11-25T10:30:00",
                        "status": "PAID"
                      },
                      {
                        "id": 2,
                        "loanId": 1,
                        "installmentNumber": 2,
                        "amount": 895.42,
                        "dueDate": "2025-12-27",
                        "paymentDate": null,
                        "status": "PENDING"
                      },
                      {
                        "id": 3,
                        "loanId": 1,
                        "installmentNumber": 3,
                        "amount": 895.42,
                        "dueDate": "2026-01-27",
                        "paymentDate": null,
                        "status": "PENDING"
                      }
                    ]
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Empréstimo não encontrado ou sem acesso",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Erro ao buscar",
                    value = """
                    {
                      "timestamp": "2025-10-27T10:30:00",
                      "status": 400,
                      "error": "Bad Request",
                      "message": "Empréstimo não encontrado ou você não tem acesso",
                      "path": "/api/loans/1/installments"
                    }
                    """
                )
            )
        )
    })
    @GetMapping("/{loanId}/installments")
    public ResponseEntity<List<LoanInstallmentResponse>> getLoanInstallments(
            @Parameter(description = "ID do empréstimo", required = true)
            @PathVariable Long loanId) {
        List<LoanInstallmentResponse> response = loanService.getLoanInstallments(loanId);
        return ResponseEntity.ok(response);
    }
}
