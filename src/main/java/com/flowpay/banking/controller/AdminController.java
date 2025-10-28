package com.flowpay.banking.controller;

import com.flowpay.banking.dto.admin.SystemStatsResponse;
import com.flowpay.banking.dto.admin.UserManagementRequest;
import com.flowpay.banking.service.AdminService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controller para administração do sistema
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Administração", description = "Endpoints administrativos para gerenciamento do sistema (requer permissão de Admin)")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    private final AdminService adminService;

    /**
     * Retorna estatísticas do sistema
     * GET /api/admin/stats
     */
    @Operation(
        summary = "Obter estatísticas do sistema",
        description = "Retorna métricas e estatísticas gerais do sistema FlowPay (apenas para administradores)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Estatísticas retornadas com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = SystemStatsResponse.class),
                examples = @ExampleObject(
                    name = "Estatísticas do sistema",
                    value = """
                    {
                      "totalUsers": 15320,
                      "activeUsers": 14890,
                      "inactiveUsers": 430,
                      "totalAccounts": 15320,
                      "totalTransactions": 486750,
                      "totalTransactionVolume": 125678900.50,
                      "totalLoans": 3420,
                      "activeLoans": 2150,
                      "totalLoanAmount": 45890000.00,
                      "totalCards": 18450,
                      "activeCards": 16200,
                      "totalSupportTickets": 890,
                      "openTickets": 125,
                      "resolvedTickets": 765,
                      "systemUptime": "99.98%",
                      "lastUpdated": "2025-10-27T10:30:00"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Erro ao buscar estatísticas",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Erro ao buscar",
                    value = """
                    {
                      "timestamp": "2025-10-27T10:30:00",
                      "status": 400,
                      "error": "Bad Request",
                      "message": "Erro ao calcular estatísticas do sistema",
                      "path": "/api/admin/stats"
                    }
                    """
                )
            )
        )
    })
    @GetMapping("/stats")
    public ResponseEntity<SystemStatsResponse> getSystemStats() {
        SystemStatsResponse response = adminService.getSystemStats();
        return ResponseEntity.ok(response);
    }

    /**
     * Atualiza status de um usuário
     * PATCH /api/admin/users/{userId}
     */
    @Operation(
        summary = "Atualizar status do usuário",
        description = "Permite ao administrador alterar o status de um usuário (ativar, desativar, suspender, etc.)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "Status do usuário atualizado com sucesso"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Usuário não encontrado ou dados inválidos",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "Usuário não encontrado",
                        value = """
                        {
                          "timestamp": "2025-10-27T10:30:00",
                          "status": 400,
                          "error": "Bad Request",
                          "message": "Usuário não encontrado",
                          "path": "/api/admin/users/999"
                        }
                        """
                    ),
                    @ExampleObject(
                        name = "Status inválido",
                        value = """
                        {
                          "timestamp": "2025-10-27T10:30:00",
                          "status": 400,
                          "error": "Bad Request",
                          "message": "Status fornecido é inválido",
                          "path": "/api/admin/users/1"
                        }
                        """
                    ),
                    @ExampleObject(
                        name = "Não pode desativar admin",
                        value = """
                        {
                          "timestamp": "2025-10-27T10:30:00",
                          "status": 400,
                          "error": "Bad Request",
                          "message": "Não é possível alterar status de outro administrador",
                          "path": "/api/admin/users/2"
                        }
                        """
                    )
                }
            )
        )
    })
    @PatchMapping("/users/{userId}")
    public ResponseEntity<Void> updateUserStatus(
            @Parameter(description = "ID do usuário", required = true)
            @PathVariable Long userId,
            @Valid @RequestBody UserManagementRequest request) {
        adminService.updateUserStatus(userId, request);
        return ResponseEntity.noContent().build();
    }
}
