package com.flowpay.banking.dto.user;

import com.flowpay.banking.enums.UserRole;
import com.flowpay.banking.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO para resposta com informações do usuário
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String nomeCompleto;
    private String cpf;
    private String email;
    private String telefone;
    private LocalDate dataNascimento;
    private Integer idade;
    private String endereco;
    private String cidade;
    private String estado;
    private String cep;
    private String nomeMae;
    private String profissao;
    private String empresaTrabalho;
    private UserStatus status;
    private Set<UserRole> roles;
    private boolean twoFactorEnabled;
    private LocalDateTime lastLogin;
    private LocalDateTime createdAt;
}
