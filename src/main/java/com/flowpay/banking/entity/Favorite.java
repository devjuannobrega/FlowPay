package com.flowpay.banking.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entidade que representa contatos/contas favoritas do usuário
 */
@Entity
@Table(name = "favorites",
    indexes = {
        @Index(name = "idx_favorite_user", columnList = "user_id"),
        @Index(name = "idx_favorite_type", columnList = "favoriteType")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "Usuário é obrigatório")
    private User user;

    /**
     * Tipo: ACCOUNT, PIX_KEY, BOLETO
     */
    @Column(nullable = false, length = 20)
    @NotBlank(message = "Tipo é obrigatório")
    private String favoriteType;

    /**
     * Nome/apelido do favorito
     */
    @Column(nullable = false, length = 100)
    @NotBlank(message = "Nome é obrigatório")
    private String name;

    /**
     * Dados do favorito (JSON com informações específicas)
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    @NotBlank(message = "Dados são obrigatórios")
    private String favoriteData;

    @Column(nullable = false)
    @Builder.Default
    private Boolean deleted = false;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
