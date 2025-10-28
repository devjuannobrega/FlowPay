package com.flowpay.banking.dto.compliance;

import com.flowpay.banking.enums.KycStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para aprovação/rejeição de KYC
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApproveKycRequest {

    @NotNull(message = "Status é obrigatório")
    private KycStatus status;

    private String notes;
    private String rejectionReason;
}
