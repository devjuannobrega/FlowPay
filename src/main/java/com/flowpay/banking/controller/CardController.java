package com.flowpay.banking.controller;

import com.flowpay.banking.dto.card.*;
import com.flowpay.banking.service.CardService;
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
 * Controller para gestão de cartões
 */
@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
@Tag(name = "Cartões", description = "Endpoints para gerenciamento de cartões de crédito e débito")
@SecurityRequirement(name = "bearerAuth")
public class CardController {

    private final CardService cardService;

    /**
     * Cria um novo cartão
     * POST /api/cards
     */
    @Operation(
        summary = "Criar novo cartão",
        description = "Solicita a criação de um novo cartão de crédito ou débito vinculado à conta do usuário"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Cartão criado com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CardResponse.class),
                examples = @ExampleObject(
                    name = "Cartão criado",
                    value = """
                    {
                      "id": 1,
                      "accountId": 1,
                      "cardNumber": "5555 **** **** 4444",
                      "cardType": "CREDIT",
                      "cardBrand": "MASTERCARD",
                      "cardStatus": "PENDING",
                      "cardLimit": 5000.00,
                      "availableLimit": 5000.00,
                      "expiryDate": "2028-10-31",
                      "holderName": "JOAO DA SILVA",
                      "createdAt": "2025-10-27T10:30:00"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Dados inválidos ou limite de cartões atingido",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Erro de validação",
                    value = """
                    {
                      "timestamp": "2025-10-27T10:30:00",
                      "status": 400,
                      "error": "Bad Request",
                      "message": "Usuário já possui o número máximo de cartões ativos",
                      "path": "/api/cards"
                    }
                    """
                )
            )
        )
    })
    @PostMapping
    public ResponseEntity<CardResponse> createCard(@Valid @RequestBody CreateCardRequest request) {
        CardResponse response = cardService.createCard(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Lista todos os cartões do usuário
     * GET /api/cards/my
     */
    @Operation(
        summary = "Listar meus cartões",
        description = "Retorna todos os cartões vinculados ao usuário autenticado"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista de cartões retornada com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CardResponse.class),
                examples = @ExampleObject(
                    name = "Lista de cartões",
                    value = """
                    [
                      {
                        "id": 1,
                        "accountId": 1,
                        "cardNumber": "5555 **** **** 4444",
                        "cardType": "CREDIT",
                        "cardBrand": "MASTERCARD",
                        "cardStatus": "ACTIVE",
                        "cardLimit": 5000.00,
                        "availableLimit": 4200.00,
                        "expiryDate": "2028-10-31",
                        "holderName": "JOAO DA SILVA",
                        "createdAt": "2025-10-27T10:30:00"
                      },
                      {
                        "id": 2,
                        "accountId": 1,
                        "cardNumber": "4111 **** **** 1111",
                        "cardType": "DEBIT",
                        "cardBrand": "VISA",
                        "cardStatus": "ACTIVE",
                        "cardLimit": null,
                        "availableLimit": null,
                        "expiryDate": "2027-05-31",
                        "holderName": "JOAO DA SILVA",
                        "createdAt": "2025-09-15T14:20:00"
                      }
                    ]
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Erro ao buscar cartões",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Erro ao buscar",
                    value = """
                    {
                      "timestamp": "2025-10-27T10:30:00",
                      "status": 400,
                      "error": "Bad Request",
                      "message": "Erro ao buscar cartões do usuário",
                      "path": "/api/cards/my"
                    }
                    """
                )
            )
        )
    })
    @GetMapping("/my")
    public ResponseEntity<List<CardResponse>> getMyCards() {
        List<CardResponse> response = cardService.getMyCards();
        return ResponseEntity.ok(response);
    }

    /**
     * Ativa um cartão
     * POST /api/cards/{cardId}/activate
     */
    @Operation(
        summary = "Ativar cartão",
        description = "Ativa um cartão que estava em status PENDING"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "Cartão ativado com sucesso"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Cartão não pode ser ativado ou não existe",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Erro ao ativar",
                    value = """
                    {
                      "timestamp": "2025-10-27T10:30:00",
                      "status": 400,
                      "error": "Bad Request",
                      "message": "Cartão não está em status pendente ou já foi cancelado",
                      "path": "/api/cards/1/activate"
                    }
                    """
                )
            )
        )
    })
    @PostMapping("/{cardId}/activate")
    public ResponseEntity<Void> activateCard(
            @Parameter(description = "ID do cartão a ser ativado", required = true)
            @PathVariable Long cardId) {
        cardService.activateCard(cardId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Bloqueia um cartão
     * POST /api/cards/{cardId}/block
     */
    @Operation(
        summary = "Bloquear cartão",
        description = "Bloqueia temporariamente um cartão ativo (pode ser desbloqueado posteriormente)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "Cartão bloqueado com sucesso"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Cartão não pode ser bloqueado ou não existe",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Erro ao bloquear",
                    value = """
                    {
                      "timestamp": "2025-10-27T10:30:00",
                      "status": 400,
                      "error": "Bad Request",
                      "message": "Cartão não está ativo ou já foi cancelado",
                      "path": "/api/cards/1/block"
                    }
                    """
                )
            )
        )
    })
    @PostMapping("/{cardId}/block")
    public ResponseEntity<Void> blockCard(
            @Parameter(description = "ID do cartão a ser bloqueado", required = true)
            @PathVariable Long cardId) {
        cardService.blockCard(cardId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Cancela um cartão
     * POST /api/cards/{cardId}/cancel
     */
    @Operation(
        summary = "Cancelar cartão",
        description = "Cancela permanentemente um cartão (ação irreversível)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "Cartão cancelado com sucesso"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Cartão não pode ser cancelado ou não existe",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Erro ao cancelar",
                    value = """
                    {
                      "timestamp": "2025-10-27T10:30:00",
                      "status": 400,
                      "error": "Bad Request",
                      "message": "Cartão já foi cancelado anteriormente",
                      "path": "/api/cards/1/cancel"
                    }
                    """
                )
            )
        )
    })
    @PostMapping("/{cardId}/cancel")
    public ResponseEntity<Void> cancelCard(
            @Parameter(description = "ID do cartão a ser cancelado", required = true)
            @PathVariable Long cardId) {
        cardService.cancelCard(cardId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Atualiza o limite do cartão
     * PATCH /api/cards/{cardId}/limit
     */
    @Operation(
        summary = "Atualizar limite do cartão",
        description = "Atualiza o limite de crédito de um cartão (apenas para cartões de crédito)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Limite atualizado com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CardResponse.class),
                examples = @ExampleObject(
                    name = "Limite atualizado",
                    value = """
                    {
                      "id": 1,
                      "accountId": 1,
                      "cardNumber": "5555 **** **** 4444",
                      "cardType": "CREDIT",
                      "cardBrand": "MASTERCARD",
                      "cardStatus": "ACTIVE",
                      "cardLimit": 8000.00,
                      "availableLimit": 7200.00,
                      "expiryDate": "2028-10-31",
                      "holderName": "JOAO DA SILVA",
                      "createdAt": "2025-10-27T10:30:00"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Dados inválidos ou cartão não é de crédito",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Erro ao atualizar limite",
                    value = """
                    {
                      "timestamp": "2025-10-27T10:30:00",
                      "status": 400,
                      "error": "Bad Request",
                      "message": "Apenas cartões de crédito possuem limite ajustável",
                      "path": "/api/cards/1/limit"
                    }
                    """
                )
            )
        )
    })
    @PatchMapping("/{cardId}/limit")
    public ResponseEntity<CardResponse> updateLimit(
            @Parameter(description = "ID do cartão", required = true)
            @PathVariable Long cardId,
            @Valid @RequestBody UpdateCardLimitRequest request) {
        CardResponse response = cardService.updateLimit(cardId, request);
        return ResponseEntity.ok(response);
    }
}
