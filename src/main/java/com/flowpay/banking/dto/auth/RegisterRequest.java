package com.flowpay.banking.dto.auth;

import com.flowpay.banking.enums.AccountType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO para requisição de registro de novo usuário
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requisição para cadastro de novo usuário no sistema")
public class RegisterRequest {

    @NotBlank(message = "Nome completo é obrigatório")
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    @Schema(description = "Nome completo do usuário", example = "João da Silva Santos", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nomeCompleto;

    @NotBlank(message = "CPF é obrigatório")
    @Pattern(regexp = "\\d{11}", message = "CPF deve conter 11 dígitos")
    @Schema(description = "CPF do usuário (apenas números)", example = "12345678900", requiredMode = Schema.RequiredMode.REQUIRED)
    private String cpf;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    @Schema(description = "E-mail do usuário", example = "joao.silva@exemplo.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 8, max = 50, message = "Senha deve ter entre 8 e 50 caracteres")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]{8,}$",
        message = "Senha deve conter pelo menos uma letra maiúscula, uma minúscula, um número e um caractere especial"
    )
    @Schema(description = "Senha forte contendo letras maiúsculas, minúsculas, números e caracteres especiais", example = "Senha@123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String senha;

    @NotBlank(message = "Telefone é obrigatório")
    @Pattern(regexp = "\\d{10,11}", message = "Telefone deve conter 10 ou 11 dígitos (DDD + número)")
    @Schema(description = "Telefone com DDD (apenas números)", example = "11987654321", requiredMode = Schema.RequiredMode.REQUIRED)
    private String telefone;

    @NotNull(message = "Data de nascimento é obrigatória")
    @Past(message = "Data de nascimento deve ser no passado")
    @Schema(description = "Data de nascimento do usuário", example = "1990-05-15", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate dataNascimento;

    @NotNull(message = "Tipo de conta é obrigatório")
    @Schema(description = "Tipo de conta bancária a ser criada", example = "CORRENTE", requiredMode = Schema.RequiredMode.REQUIRED)
    private AccountType tipoConta;

    @NotBlank(message = "Endereço é obrigatório")
    @Schema(description = "Endereço completo (rua, número, complemento)", example = "Rua das Flores, 123, Apto 45", requiredMode = Schema.RequiredMode.REQUIRED)
    private String endereco;

    @NotBlank(message = "Cidade é obrigatória")
    @Schema(description = "Cidade de residência", example = "São Paulo", requiredMode = Schema.RequiredMode.REQUIRED)
    private String cidade;

    @NotBlank(message = "Estado é obrigatório")
    @Size(min = 2, max = 2, message = "Estado deve ter 2 caracteres (UF)")
    @Schema(description = "Sigla do estado (UF)", example = "SP", requiredMode = Schema.RequiredMode.REQUIRED)
    private String estado;

    @NotBlank(message = "CEP é obrigatório")
    @Pattern(regexp = "\\d{8}", message = "CEP deve conter 8 dígitos")
    @Schema(description = "CEP do endereço (apenas números)", example = "01234567", requiredMode = Schema.RequiredMode.REQUIRED)
    private String cep;

    // Campos opcionais
    @Schema(description = "Nome da mãe do usuário", example = "Maria Santos", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String nomeMae;

    @Schema(description = "Profissão do usuário", example = "Engenheiro de Software", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String profissao;

    @Schema(description = "Empresa onde trabalha", example = "Tech Solutions Ltda", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String empresaTrabalho;
}
