package com.flowpay.banking.entity;

import com.flowpay.banking.enums.SupportTicketStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entidade que representa um ticket de suporte
 */
@Entity
@Table(name = "support_tickets",
    indexes = {
        @Index(name = "idx_ticket_user", columnList = "user_id"),
        @Index(name = "idx_ticket_status", columnList = "status"),
        @Index(name = "idx_ticket_assigned", columnList = "assigned_to")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupportTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Número do protocolo (ex: TK-2024-00001)
     */
    @Column(nullable = false, unique = true, length = 30)
    private String ticketNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "Usuário é obrigatório")
    private User user;

    /**
     * Categoria (TECHNICAL, FINANCIAL, ACCOUNT, CARD, etc.)
     */
    @Column(nullable = false, length = 50)
    @NotBlank(message = "Categoria é obrigatória")
    private String category;

    /**
     * Prioridade (LOW, MEDIUM, HIGH, CRITICAL)
     */
    @Column(nullable = false, length = 20)
    @Builder.Default
    private String priority = "MEDIUM";

    @Column(nullable = false, length = 200)
    @NotBlank(message = "Assunto é obrigatório")
    private String subject;

    @Column(nullable = false, columnDefinition = "TEXT")
    @NotBlank(message = "Descrição é obrigatória")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private SupportTicketStatus status = SupportTicketStatus.OPEN;

    /**
     * ID do agente de suporte atribuído
     */
    @Column(name = "assigned_to")
    private Long assignedTo;

    /**
     * Resposta/resolução do ticket
     */
    @Column(columnDefinition = "TEXT")
    private String resolution;

    /**
     * Data/hora da primeira resposta
     */
    @Column
    private LocalDateTime firstResponseAt;

    /**
     * Data/hora da resolução
     */
    @Column
    private LocalDateTime resolvedAt;

    /**
     * Data/hora do fechamento
     */
    @Column
    private LocalDateTime closedAt;

    /**
     * Avaliação do atendimento (1-5)
     */
    @Column
    @Min(value = 1)
    @Max(value = 5)
    private Integer rating;

    /**
     * Comentário da avaliação
     */
    @Column(length = 500)
    private String ratingComment;

    @Column(nullable = false)
    @Builder.Default
    private Boolean deleted = false;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Transient
    public boolean isOpen() {
        return status == SupportTicketStatus.OPEN;
    }

    @Transient
    public boolean isClosed() {
        return status == SupportTicketStatus.CLOSED;
    }

    @Transient
    public long getResponseTimeInHours() {
        if (firstResponseAt == null) return 0;
        return java.time.Duration.between(createdAt, firstResponseAt).toHours();
    }

    @Transient
    public long getResolutionTimeInHours() {
        if (resolvedAt == null) return 0;
        return java.time.Duration.between(createdAt, resolvedAt).toHours();
    }
}
