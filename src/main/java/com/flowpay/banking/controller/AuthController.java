package com.flowpay.banking.controller;

import com.flowpay.banking.dto.auth.*;
import com.flowpay.banking.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller para autenticação
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Endpoints para registro, login, refresh token e logout")
public class AuthController {

    private final AuthService authService;

    /**
     * Registra um novo usuário
     * POST /api/auth/register
     */
    @Operation(
        summary = "Registrar novo usuário",
        description = "Cria uma nova conta de usuário no sistema FlowPay e retorna os tokens de autenticação"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Usuário registrado com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = LoginResponse.class),
                examples = @ExampleObject(
                    name = "Registro bem-sucedido",
                    value = """
                    {
                      "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                      "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                      "tokenType": "Bearer",
                      "userId": 1,
                      "email": "usuario@exemplo.com",
                      "nomeCompleto": "João da Silva",
                      "roles": ["ROLE_USER"],
                      "twoFactorRequired": false
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Dados inválidos ou e-mail já cadastrado",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "Validação falhou",
                        value = """
                        {
                          "timestamp": "2025-10-27T10:30:00",
                          "status": 400,
                          "error": "Bad Request",
                          "message": "E-mail já está em uso",
                          "path": "/api/auth/register"
                        }
                        """
                    ),
                    @ExampleObject(
                        name = "Campos obrigatórios",
                        value = """
                        {
                          "timestamp": "2025-10-27T10:30:00",
                          "status": 400,
                          "error": "Bad Request",
                          "message": "nomeCompleto: não pode estar em branco; senha: deve ter no mínimo 8 caracteres",
                          "path": "/api/auth/register"
                        }
                        """
                    )
                }
            )
        )
    })
    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(@Valid @RequestBody RegisterRequest request) {
        LoginResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Realiza login
     * POST /api/auth/login
     */
    @Operation(
        summary = "Realizar login",
        description = "Autentica um usuário e retorna os tokens de acesso. Suporta autenticação de dois fatores (2FA)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Login realizado com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = LoginResponse.class),
                examples = {
                    @ExampleObject(
                        name = "Login bem-sucedido",
                        value = """
                        {
                          "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                          "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                          "tokenType": "Bearer",
                          "userId": 1,
                          "email": "usuario@exemplo.com",
                          "nomeCompleto": "João da Silva",
                          "roles": ["ROLE_USER"],
                          "twoFactorRequired": false
                        }
                        """
                    ),
                    @ExampleObject(
                        name = "2FA requerido",
                        value = """
                        {
                          "accessToken": null,
                          "refreshToken": null,
                          "tokenType": "Bearer",
                          "userId": 1,
                          "email": "usuario@exemplo.com",
                          "nomeCompleto": "João da Silva",
                          "roles": ["ROLE_USER"],
                          "twoFactorRequired": true
                        }
                        """
                    )
                }
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Credenciais inválidas ou dados de requisição incorretos",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "Credenciais inválidas",
                        value = """
                        {
                          "timestamp": "2025-10-27T10:30:00",
                          "status": 400,
                          "error": "Bad Request",
                          "message": "E-mail ou senha inválidos",
                          "path": "/api/auth/login"
                        }
                        """
                    ),
                    @ExampleObject(
                        name = "Campos obrigatórios",
                        value = """
                        {
                          "timestamp": "2025-10-27T10:30:00",
                          "status": 400,
                          "error": "Bad Request",
                          "message": "email: não pode estar em branco",
                          "path": "/api/auth/login"
                        }
                        """
                    )
                }
            )
        )
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Atualiza o access token
     * POST /api/auth/refresh
     */
    @Operation(
        summary = "Atualizar access token",
        description = "Gera um novo access token usando o refresh token fornecido"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Token atualizado com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RefreshTokenResponse.class),
                examples = @ExampleObject(
                    name = "Token renovado",
                    value = """
                    {
                      "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                      "tokenType": "Bearer"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Refresh token inválido ou expirado",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Token inválido",
                    value = """
                    {
                      "timestamp": "2025-10-27T10:30:00",
                      "status": 400,
                      "error": "Bad Request",
                      "message": "Refresh token inválido ou expirado",
                      "path": "/api/auth/refresh"
                    }
                    """
                )
            )
        )
    })
    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        RefreshTokenResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Realiza logout
     * POST /api/auth/logout
     */
    @Operation(
        summary = "Realizar logout",
        description = "Invalida o token de acesso do usuário e encerra a sessão"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "Logout realizado com sucesso - sem conteúdo de retorno"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Token inválido ou ausente",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Token inválido",
                    value = """
                    {
                      "timestamp": "2025-10-27T10:30:00",
                      "status": 400,
                      "error": "Bad Request",
                      "message": "Token de autenticação inválido",
                      "path": "/api/auth/logout"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Não autenticado - Authorization header ausente",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Não autenticado",
                    value = """
                    {
                      "timestamp": "2025-10-27T10:30:00",
                      "status": 401,
                      "error": "Unauthorized",
                      "message": "Token de autenticação ausente",
                      "path": "/api/auth/logout"
                    }
                    """
                )
            )
        )
    })
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String authorization) {
        String token = authorization.substring(7); // Remove "Bearer "
        authService.logout(token);
        return ResponseEntity.noContent().build();
    }
}
