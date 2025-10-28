package com.flowpay.banking.controller;

import com.flowpay.banking.dto.support.*;
import com.flowpay.banking.service.SupportService;
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
 * Controller para gestão de suporte
 */
@RestController
@RequestMapping("/api/support")
@RequiredArgsConstructor
@Tag(name = "Suporte", description = "Endpoints para abertura e gerenciamento de tickets de suporte")
@SecurityRequirement(name = "bearerAuth")
public class SupportController {

    private final SupportService supportService;

    /**
     * Cria um novo ticket de suporte
     * POST /api/support/tickets
     */
    @Operation(
        summary = "Criar ticket de suporte",
        description = "Abre um novo chamado de suporte para o usuário"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Ticket criado com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TicketResponse.class),
                examples = @ExampleObject(
                    name = "Ticket criado",
                    value = """
                    {
                      "id": 1,
                      "userId": 1,
                      "subject": "Problema com transferência",
                      "description": "Tentei fazer uma transferência PIX mas não foi concluída",
                      "category": "TRANSACTION",
                      "priority": "MEDIUM",
                      "status": "OPEN",
                      "createdAt": "2025-10-27T10:30:00",
                      "updatedAt": "2025-10-27T10:30:00",
                      "resolvedAt": null,
                      "assignedTo": null
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Dados inválidos ou erro ao criar ticket",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Erro de validação",
                    value = """
                    {
                      "timestamp": "2025-10-27T10:30:00",
                      "status": 400,
                      "error": "Bad Request",
                      "message": "subject: não pode estar em branco; description: deve ter no mínimo 10 caracteres",
                      "path": "/api/support/tickets"
                    }
                    """
                )
            )
        )
    })
    @PostMapping("/tickets")
    public ResponseEntity<TicketResponse> createTicket(@Valid @RequestBody CreateTicketRequest request) {
        TicketResponse response = supportService.createTicket(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Lista todos os tickets do usuário
     * GET /api/support/tickets/my
     */
    @Operation(
        summary = "Listar meus tickets",
        description = "Retorna todos os tickets de suporte abertos pelo usuário autenticado"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista de tickets retornada com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TicketResponse.class),
                examples = @ExampleObject(
                    name = "Lista de tickets",
                    value = """
                    [
                      {
                        "id": 1,
                        "userId": 1,
                        "subject": "Problema com transferência",
                        "description": "Tentei fazer uma transferência PIX mas não foi concluída",
                        "category": "TRANSACTION",
                        "priority": "MEDIUM",
                        "status": "IN_PROGRESS",
                        "createdAt": "2025-10-27T10:30:00",
                        "updatedAt": "2025-10-27T11:00:00",
                        "resolvedAt": null,
                        "assignedTo": "support@flowpay.com"
                      },
                      {
                        "id": 2,
                        "userId": 1,
                        "subject": "Dúvida sobre empréstimo",
                        "description": "Gostaria de saber as taxas de juros para empréstimo pessoal",
                        "category": "LOAN",
                        "priority": "LOW",
                        "status": "RESOLVED",
                        "createdAt": "2025-10-25T14:20:00",
                        "updatedAt": "2025-10-26T09:15:00",
                        "resolvedAt": "2025-10-26T09:15:00",
                        "assignedTo": "support@flowpay.com"
                      }
                    ]
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Erro ao buscar tickets",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Erro ao buscar",
                    value = """
                    {
                      "timestamp": "2025-10-27T10:30:00",
                      "status": 400,
                      "error": "Bad Request",
                      "message": "Erro ao buscar tickets do usuário",
                      "path": "/api/support/tickets/my"
                    }
                    """
                )
            )
        )
    })
    @GetMapping("/tickets/my")
    public ResponseEntity<List<TicketResponse>> getMyTickets() {
        List<TicketResponse> response = supportService.getMyTickets();
        return ResponseEntity.ok(response);
    }

    /**
     * Atualiza um ticket (Admin/Support)
     * PATCH /api/support/tickets/{ticketId}
     */
    @Operation(
        summary = "Atualizar ticket (Admin/Suporte)",
        description = "Atualiza o status, prioridade ou atribui um ticket a um atendente (requer permissão de Admin ou Suporte)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Ticket atualizado com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TicketResponse.class),
                examples = @ExampleObject(
                    name = "Ticket atualizado",
                    value = """
                    {
                      "id": 1,
                      "userId": 1,
                      "subject": "Problema com transferência",
                      "description": "Tentei fazer uma transferência PIX mas não foi concluída",
                      "category": "TRANSACTION",
                      "priority": "HIGH",
                      "status": "RESOLVED",
                      "createdAt": "2025-10-27T10:30:00",
                      "updatedAt": "2025-10-27T15:30:00",
                      "resolvedAt": "2025-10-27T15:30:00",
                      "assignedTo": "support@flowpay.com"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Ticket não encontrado ou dados inválidos",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "Ticket não encontrado",
                        value = """
                        {
                          "timestamp": "2025-10-27T10:30:00",
                          "status": 400,
                          "error": "Bad Request",
                          "message": "Ticket não encontrado",
                          "path": "/api/support/tickets/999"
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
                          "message": "Status do ticket inválido",
                          "path": "/api/support/tickets/1"
                        }
                        """
                    )
                }
            )
        )
    })
    @PatchMapping("/tickets/{ticketId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPPORT')")
    public ResponseEntity<TicketResponse> updateTicket(
            @Parameter(description = "ID do ticket", required = true)
            @PathVariable Long ticketId,
            @Valid @RequestBody UpdateTicketRequest request) {
        TicketResponse response = supportService.updateTicket(ticketId, request);
        return ResponseEntity.ok(response);
    }
}
