package com.flowpay.banking.dto.referral;

import com.flowpay.banking.enums.ReferralStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para resposta de indicação
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReferralResponse {

    private Long id;
    private String referralCode;
    private Long referrerId;
    private String referrerName;
    private Long referredUserId;
    private String referredUserName;
    private String referredUserEmail;
    private ReferralStatus status;
    private BigDecimal bonusAmount;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
}
