package com.flowpay.banking.dto.compliance;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para envio de documento KYC
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KycDocumentRequest {

    @NotBlank(message = "Tipo de documento é obrigatório")
    private String documentType; // "RG", "CNH", "COMPROVANTE_RESIDENCIA"

    @NotBlank(message = "URL do documento é obrigatória")
    private String documentUrl;

    private String additionalInfo;
}
