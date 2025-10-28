package com.flowpay.banking.controller;

import com.flowpay.banking.dto.pix.PixKeyResponse;
import com.flowpay.banking.dto.pix.PixTransferRequest;
import com.flowpay.banking.dto.pix.RegisterPixKeyRequest;
import com.flowpay.banking.dto.transaction.TransactionResponse;
import com.flowpay.banking.service.PixService;
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
 * Controller para operações PIX
 */
@RestController
@RequestMapping("/api/pix")
@RequiredArgsConstructor
@Tag(name = "PIX", description = "Gerenciamento de chaves PIX e transferências instantâneas")
@SecurityRequirement(name = "bearerAuth")
public class PixController {

    private final PixService pixService;

    /**
     * Registra uma nova chave PIX
     * POST /api/pix/keys
     */
    @Operation(summary = "Registrar chave PIX", description = "Registra nova chave PIX (CPF, E-mail, Telefone ou Aleatória)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Chave PIX registrada com sucesso",
            content = @Content(schema = @Schema(implementation = PixKeyResponse.class),
                examples = @ExampleObject(value = """
                    {"id": 1, "accountId": 1, "keyType": "EMAIL", "keyValue": "user@email.com", "status": "ACTIVE"}
                    """))),
        @ApiResponse(responseCode = "400", description = "Chave já cadastrada ou inválida",
            content = @Content(examples = @ExampleObject(value = "{\"message\": \"Chave PIX já cadastrada\"}")))
    })
    @PostMapping("/keys")
    public ResponseEntity<PixKeyResponse> registerPixKey(@Valid @RequestBody RegisterPixKeyRequest request) {
        PixKeyResponse response = pixService.registerPixKey(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Remove uma chave PIX
     * DELETE /api/pix/keys/{pixKeyId}
     */
    @Operation(summary = "Remover chave PIX", description = "Remove uma chave PIX previamente cadastrada")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Chave PIX removida com sucesso"),
        @ApiResponse(responseCode = "400", description = "Chave não encontrada",
            content = @Content(examples = @ExampleObject(value = "{\"message\": \"Chave PIX não encontrada\"}")))
    })
    @DeleteMapping("/keys/{pixKeyId}")
    public ResponseEntity<Void> deletePixKey(
        @Parameter(description = "ID da chave PIX", example = "1") @PathVariable Long pixKeyId
    ) {
        pixService.deletePixKey(pixKeyId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Lista todas as chaves PIX do usuário
     * GET /api/pix/keys/my
     */
    @Operation(summary = "Listar minhas chaves PIX", description = "Retorna todas as chaves PIX do usuário autenticado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de chaves PIX",
            content = @Content(examples = @ExampleObject(value = """
                [{"id": 1, "keyType": "EMAIL", "keyValue": "user@email.com", "status": "ACTIVE"},
                 {"id": 2, "keyType": "PHONE", "keyValue": "+5511999999999", "status": "ACTIVE"}]
                """)))
    })
    @GetMapping("/keys/my")
    public ResponseEntity<List<PixKeyResponse>> getMyPixKeys() {
        List<PixKeyResponse> response = pixService.getMyPixKeys();
        return ResponseEntity.ok(response);
    }

    /**
     * Busca uma chave PIX
     * GET /api/pix/keys/search?key=xxx
     */
    @Operation(summary = "Buscar chave PIX", description = "Busca informações de uma chave PIX para transferência")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Chave PIX encontrada",
            content = @Content(schema = @Schema(implementation = PixKeyResponse.class))),
        @ApiResponse(responseCode = "400", description = "Chave não encontrada",
            content = @Content(examples = @ExampleObject(value = "{\"message\": \"Chave PIX não cadastrada\"}")))
    })
    @GetMapping("/keys/search")
    public ResponseEntity<PixKeyResponse> findPixKey(
        @Parameter(description = "Valor da chave PIX", example = "user@email.com") @RequestParam String key
    ) {
        PixKeyResponse response = pixService.findPixKey(key);
        return ResponseEntity.ok(response);
    }

    /**
     * Realiza transferência PIX
     * POST /api/pix/transfer
     */
    @Operation(summary = "Transferir via PIX", description = "Realiza transferência instantânea usando chave PIX")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Transferência PIX realizada com sucesso",
            content = @Content(schema = @Schema(implementation = TransactionResponse.class),
                examples = @ExampleObject(value = """
                    {"id": 5, "tipo": "PIX", "valor": 50.00, "status": "COMPLETED", "transactionHash": "pix123"}
                    """))),
        @ApiResponse(responseCode = "400", description = "Saldo insuficiente ou chave inválida",
            content = @Content(examples = @ExampleObject(value = "{\"message\": \"Chave PIX não encontrada\"}")))
    })
    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponse> pixTransfer(@Valid @RequestBody PixTransferRequest request) {
        TransactionResponse response = pixService.pixTransfer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
