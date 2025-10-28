package com.flowpay.banking.dto.referral;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para aplicação de código de indicação
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplyReferralRequest {

    @NotBlank(message = "Código de indicação é obrigatório")
    private String referralCode;
}
