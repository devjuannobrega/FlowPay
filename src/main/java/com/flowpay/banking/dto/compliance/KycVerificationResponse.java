package com.flowpay.banking.dto.compliance;

import com.flowpay.banking.enums.KycStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para resposta de verificação KYC
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KycVerificationResponse {

    private Long id;
    private Long userId;
    private String documentType;
    private KycStatus status;
    private String rejectionReason;
    private String verifiedBy;
    private LocalDateTime submittedAt;
    private LocalDateTime verifiedAt;
}
