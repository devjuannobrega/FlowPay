package com.flowpay.banking.entity;

import com.flowpay.banking.enums.UserRole;
import com.flowpay.banking.enums.UserStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entidade que representa um usuário do sistema FlowPay
 */
@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_user_cpf", columnList = "cpf"),
        @Index(name = "idx_user_email", columnList = "email")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "Nome completo é obrigatório")
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    private String nomeCompleto;

    @Column(nullable = false, unique = true, length = 11)
    @NotBlank(message = "CPF é obrigatório")
    @Pattern(regexp = "\\d{11}", message = "CPF deve conter 11 dígitos")
    private String cpf;

    @Column(nullable = false, unique = true, length = 100)
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    private String email;

    @Column(nullable = false)
    @NotBlank(message = "Senha é obrigatória")
    private String senha;

    @Column(nullable = false, length = 15)
    @NotBlank(message = "Telefone é obrigatório")
    @Pattern(regexp = "\\d{10,15}", message = "Telefone deve conter entre 10 e 15 dígitos")
    private String telefone;

    @Column(nullable = false)
    @NotNull(message = "Data de nascimento é obrigatória")
    @Past(message = "Data de nascimento deve ser no passado")
    private LocalDate dataNascimento;

    @Column(length = 20)
    private String rg;

    @Column(length = 200)
    private String endereco;

    @Column(length = 100)
    private String cidade;

    @Column(length = 2)
    private String estado;

    @Column(length = 8)
    @Pattern(regexp = "\\d{8}", message = "CEP deve conter 8 dígitos")
    private String cep;

    @Column(length = 100)
    private String nomeMae;

    @Column(length = 100)
    private String profissao;

    @Column(length = 100)
    private String occupation;

    @Column(length = 100)
    private String empresaTrabalho;

    @Column(length = 100)
    private String employer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserStatus status = UserStatus.PENDENTE;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    @Builder.Default
    private Set<UserRole> roles = new HashSet<>();

    @Column(nullable = false)
    private Boolean twoFactorEnabled = false;

    @Column
    private String twoFactorSecret;

    @Column
    private Integer loginAttempts = 0;

    @Column
    private LocalDateTime lastLoginAt;

    @Column
    private String lastLoginIp;

    @Column(nullable = false)
    private Boolean deleted = false;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Account> accounts;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Notification> notifications;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<KycDocument> kycDocuments;

    /**
     * Calcula a idade do usuário
     */
    @Transient
    public int getIdade() {
        if (dataNascimento == null) return 0;
        return LocalDate.now().getYear() - dataNascimento.getYear();
    }

    /**
     * Verifica se o usuário é maior de idade
     */
    @Transient
    public boolean isMaiorDeIdade() {
        return getIdade() >= 18;
    }

    /**
     * Verifica se a conta está ativa
     */
    @Transient
    public boolean isAtivo() {
        return status == UserStatus.ATIVO;
    }
}
