package com.flowpay.banking.entity;

import com.flowpay.banking.enums.KycStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entidade que representa documentos KYC (Know Your Customer)
 */
@Entity
@Table(name = "kyc_documents",
    indexes = {
        @Index(name = "idx_kyc_user", columnList = "user_id"),
        @Index(name = "idx_kyc_status", columnList = "status")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KycDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "Usuário é obrigatório")
    private User user;

    /**
     * Tipo de documento (RG, CNH, COMPROVANTE_RESIDENCIA, SELFIE, etc.)
     */
    @Column(nullable = false, length = 50)
    @NotBlank(message = "Tipo de documento é obrigatório")
    private String documentType;

    /**
     * Caminho do arquivo no storage
     */
    @Column(nullable = false, length = 500)
    @NotBlank(message = "Caminho do arquivo é obrigatório")
    private String filePath;

    /**
     * Nome original do arquivo
     */
    @Column(length = 255)
    private String originalFileName;

    /**
     * Tipo MIME do arquivo
     */
    @Column(length = 100)
    private String mimeType;

    /**
     * Tamanho do arquivo em bytes
     */
    @Column
    private Long fileSize;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private KycStatus status = KycStatus.PENDING;

    /**
     * ID do analista que revisou
     */
    @Column
    private Long reviewedBy;

    /**
     * Comentários da análise
     */
    @Column(columnDefinition = "TEXT")
    private String reviewComments;

    /**
     * Data/hora da revisão
     */
    @Column
    private LocalDateTime reviewedAt;

    /**
     * Motivo da rejeição (se status = REJECTED)
     */
    @Column(length = 500)
    private String rejectionReason;

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
    public boolean isApproved() {
        return status == KycStatus.APPROVED;
    }
}
