package com.flowpay.banking.controller;

import com.flowpay.banking.dto.notification.NotificationResponse;
import com.flowpay.banking.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller para gestão de notificações
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notificações", description = "Endpoints para visualizar e gerenciar notificações do usuário")
@SecurityRequirement(name = "bearerAuth")
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * Lista todas as notificações do usuário
     * GET /api/notifications/my
     */
    @Operation(
        summary = "Listar minhas notificações",
        description = "Retorna todas as notificações do usuário autenticado, incluindo lidas e não lidas"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista de notificações retornada com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = NotificationResponse.class),
                examples = @ExampleObject(
                    name = "Lista de notificações",
                    value = """
                    [
                      {
                        "id": 1,
                        "userId": 1,
                        "type": "TRANSACTION",
                        "title": "Transferência recebida",
                        "message": "Você recebeu R$ 500,00 de Maria Santos",
                        "read": false,
                        "createdAt": "2025-10-27T10:30:00"
                      },
                      {
                        "id": 2,
                        "userId": 1,
                        "type": "LOAN",
                        "title": "Empréstimo aprovado",
                        "message": "Seu empréstimo de R$ 10.000,00 foi aprovado",
                        "read": true,
                        "createdAt": "2025-10-26T14:20:00"
                      },
                      {
                        "id": 3,
                        "userId": 1,
                        "type": "CARD",
                        "title": "Cartão desbloqueado",
                        "message": "Seu cartão final 4444 foi ativado com sucesso",
                        "read": true,
                        "createdAt": "2025-10-25T09:15:00"
                      }
                    ]
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Erro ao buscar notificações",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Erro ao buscar",
                    value = """
                    {
                      "timestamp": "2025-10-27T10:30:00",
                      "status": 400,
                      "error": "Bad Request",
                      "message": "Erro ao buscar notificações do usuário",
                      "path": "/api/notifications/my"
                    }
                    """
                )
            )
        )
    })
    @GetMapping("/my")
    public ResponseEntity<List<NotificationResponse>> getMyNotifications() {
        List<NotificationResponse> response = notificationService.getMyNotifications();
        return ResponseEntity.ok(response);
    }

    /**
     * Marca uma notificação como lida
     * POST /api/notifications/{notificationId}/read
     */
    @Operation(
        summary = "Marcar notificação como lida",
        description = "Marca uma notificação específica como lida pelo usuário"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "Notificação marcada como lida com sucesso"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Notificação não encontrada ou já está marcada como lida",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "Notificação não encontrada",
                        value = """
                        {
                          "timestamp": "2025-10-27T10:30:00",
                          "status": 400,
                          "error": "Bad Request",
                          "message": "Notificação não encontrada ou você não tem acesso",
                          "path": "/api/notifications/999/read"
                        }
                        """
                    ),
                    @ExampleObject(
                        name = "Já marcada como lida",
                        value = """
                        {
                          "timestamp": "2025-10-27T10:30:00",
                          "status": 400,
                          "error": "Bad Request",
                          "message": "Notificação já está marcada como lida",
                          "path": "/api/notifications/1/read"
                        }
                        """
                    )
                }
            )
        )
    })
    @PostMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(
            @Parameter(description = "ID da notificação", required = true)
            @PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.noContent().build();
    }
}
