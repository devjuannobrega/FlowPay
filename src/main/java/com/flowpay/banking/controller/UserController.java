package com.flowpay.banking.controller;

import com.flowpay.banking.dto.user.ChangePasswordRequest;
import com.flowpay.banking.dto.user.UpdateUserRequest;
import com.flowpay.banking.dto.user.UserResponse;
import com.flowpay.banking.service.UserService;
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
 * Controller para gestão de usuários
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Usuários", description = "Endpoints para gerenciamento de perfil e configurações de usuário")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    /**
     * Retorna informações do usuário autenticado
     * GET /api/users/me
     */
    @Operation(
        summary = "Obter perfil do usuário autenticado",
        description = "Retorna as informações do perfil do usuário que está atualmente autenticado"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Dados do usuário retornados com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserResponse.class),
                examples = @ExampleObject(
                    name = "Perfil do usuário",
                    value = """
                    {
                      "id": 1,
                      "email": "usuario@exemplo.com",
                      "nomeCompleto": "João da Silva",
                      "cpf": "123.456.789-00",
                      "telefone": "(11) 98765-4321",
                      "dataNascimento": "1990-05-15",
                      "endereco": "Rua das Flores, 123",
                      "cidade": "São Paulo",
                      "estado": "SP",
                      "cep": "01234-567",
                      "twoFactorEnabled": false,
                      "accountStatus": "ACTIVE",
                      "createdAt": "2025-01-15T10:30:00",
                      "roles": ["ROLE_USER"]
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Erro ao buscar dados do usuário",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Erro ao buscar",
                    value = """
                    {
                      "timestamp": "2025-10-27T10:30:00",
                      "status": 400,
                      "error": "Bad Request",
                      "message": "Usuário não encontrado",
                      "path": "/api/users/me"
                    }
                    """
                )
            )
        )
    })
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser() {
        UserResponse response = userService.getCurrentUser();
        return ResponseEntity.ok(response);
    }

    /**
     * Retorna informações de um usuário específico
     * GET /api/users/{userId}
     */
    @Operation(
        summary = "Obter usuário por ID",
        description = "Retorna as informações de um usuário específico (requer permissão para acessar o recurso)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Dados do usuário retornados com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserResponse.class),
                examples = @ExampleObject(
                    name = "Dados do usuário",
                    value = """
                    {
                      "id": 2,
                      "email": "maria@exemplo.com",
                      "nomeCompleto": "Maria Santos",
                      "cpf": "987.654.321-00",
                      "telefone": "(11) 91234-5678",
                      "dataNascimento": "1985-08-20",
                      "endereco": "Av. Paulista, 1000",
                      "cidade": "São Paulo",
                      "estado": "SP",
                      "cep": "01310-100",
                      "twoFactorEnabled": true,
                      "accountStatus": "ACTIVE",
                      "createdAt": "2025-02-10T14:20:00",
                      "roles": ["ROLE_USER"]
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Usuário não encontrado ou sem permissão",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Erro de acesso",
                    value = """
                    {
                      "timestamp": "2025-10-27T10:30:00",
                      "status": 400,
                      "error": "Bad Request",
                      "message": "Usuário não encontrado ou sem permissão de acesso",
                      "path": "/api/users/2"
                    }
                    """
                )
            )
        )
    })
    @GetMapping("/{userId}")
    @PreAuthorize("@securityUtils.canAccessResource(#userId)")
    public ResponseEntity<UserResponse> getUserById(
            @Parameter(description = "ID do usuário", required = true)
            @PathVariable Long userId) {
        UserResponse response = userService.getUserById(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Atualiza informações do usuário
     * PUT /api/users/{userId}
     */
    @Operation(
        summary = "Atualizar dados do usuário",
        description = "Atualiza as informações do perfil de um usuário (requer permissão para acessar o recurso)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Dados do usuário atualizados com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserResponse.class),
                examples = @ExampleObject(
                    name = "Usuário atualizado",
                    value = """
                    {
                      "id": 1,
                      "email": "usuario@exemplo.com",
                      "nomeCompleto": "João da Silva Santos",
                      "cpf": "123.456.789-00",
                      "telefone": "(11) 98888-7777",
                      "dataNascimento": "1990-05-15",
                      "endereco": "Rua das Acácias, 456",
                      "cidade": "São Paulo",
                      "estado": "SP",
                      "cep": "01234-999",
                      "twoFactorEnabled": false,
                      "accountStatus": "ACTIVE",
                      "createdAt": "2025-01-15T10:30:00",
                      "roles": ["ROLE_USER"]
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Dados inválidos ou sem permissão",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Erro de validação",
                    value = """
                    {
                      "timestamp": "2025-10-27T10:30:00",
                      "status": 400,
                      "error": "Bad Request",
                      "message": "telefone: formato inválido",
                      "path": "/api/users/1"
                    }
                    """
                )
            )
        )
    })
    @PutMapping("/{userId}")
    @PreAuthorize("@securityUtils.canAccessResource(#userId)")
    public ResponseEntity<UserResponse> updateUser(
            @Parameter(description = "ID do usuário", required = true)
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserRequest request) {
        UserResponse response = userService.updateUser(userId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Altera a senha do usuário
     * POST /api/users/{userId}/change-password
     */
    @Operation(
        summary = "Alterar senha do usuário",
        description = "Permite ao usuário alterar sua senha atual fornecendo a senha antiga e a nova senha"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "Senha alterada com sucesso"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Senha atual incorreta ou nova senha inválida",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "Senha atual incorreta",
                        value = """
                        {
                          "timestamp": "2025-10-27T10:30:00",
                          "status": 400,
                          "error": "Bad Request",
                          "message": "Senha atual está incorreta",
                          "path": "/api/users/1/change-password"
                        }
                        """
                    ),
                    @ExampleObject(
                        name = "Nova senha inválida",
                        value = """
                        {
                          "timestamp": "2025-10-27T10:30:00",
                          "status": 400,
                          "error": "Bad Request",
                          "message": "A nova senha deve ter no mínimo 8 caracteres",
                          "path": "/api/users/1/change-password"
                        }
                        """
                    )
                }
            )
        )
    })
    @PostMapping("/{userId}/change-password")
    public ResponseEntity<Void> changePassword(
            @Parameter(description = "ID do usuário", required = true)
            @PathVariable Long userId,
            @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(userId, request);
        return ResponseEntity.noContent().build();
    }

    /**
     * Ativa 2FA
     * POST /api/users/{userId}/enable-2fa
     */
    @Operation(
        summary = "Ativar autenticação de dois fatores (2FA)",
        description = "Habilita a autenticação de dois fatores para aumentar a segurança da conta do usuário"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "2FA ativado com sucesso"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Erro ao ativar 2FA ou já está ativo",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "2FA já ativo",
                    value = """
                    {
                      "timestamp": "2025-10-27T10:30:00",
                      "status": 400,
                      "error": "Bad Request",
                      "message": "A autenticação de dois fatores já está ativa",
                      "path": "/api/users/1/enable-2fa"
                    }
                    """
                )
            )
        )
    })
    @PostMapping("/{userId}/enable-2fa")
    public ResponseEntity<Void> enable2FA(
            @Parameter(description = "ID do usuário", required = true)
            @PathVariable Long userId) {
        userService.enable2FA(userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Desativa 2FA
     * POST /api/users/{userId}/disable-2fa
     */
    @Operation(
        summary = "Desativar autenticação de dois fatores (2FA)",
        description = "Desabilita a autenticação de dois fatores da conta do usuário"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "2FA desativado com sucesso"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Erro ao desativar 2FA ou já está inativo",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "2FA já inativo",
                    value = """
                    {
                      "timestamp": "2025-10-27T10:30:00",
                      "status": 400,
                      "error": "Bad Request",
                      "message": "A autenticação de dois fatores já está inativa",
                      "path": "/api/users/1/disable-2fa"
                    }
                    """
                )
            )
        )
    })
    @PostMapping("/{userId}/disable-2fa")
    public ResponseEntity<Void> disable2FA(
            @Parameter(description = "ID do usuário", required = true)
            @PathVariable Long userId) {
        userService.disable2FA(userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Deleta o usuário
     * DELETE /api/users/{userId}
     */
    @Operation(
        summary = "Deletar usuário",
        description = "Remove permanentemente um usuário do sistema (requer ser o próprio usuário ou administrador)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "Usuário deletado com sucesso"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Erro ao deletar usuário ou sem permissão",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Sem permissão",
                    value = """
                    {
                      "timestamp": "2025-10-27T10:30:00",
                      "status": 400,
                      "error": "Bad Request",
                      "message": "Você não tem permissão para deletar este usuário",
                      "path": "/api/users/1"
                    }
                    """
                )
            )
        )
    })
    @DeleteMapping("/{userId}")
    @PreAuthorize("@securityUtils.canAccessResource(#userId) or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID do usuário", required = true)
            @PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
