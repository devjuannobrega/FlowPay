package com.flowpay.banking.dto.loan;

import com.flowpay.banking.enums.LoanStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO para resposta de empréstimo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanResponse {

    private Long id;
    private Long accountId;
    private String loanNumber;
    private BigDecimal requestedAmount;
    private BigDecimal approvedAmount;
    private BigDecimal interestRate;
    private Integer numberOfInstallments;
    private BigDecimal installmentAmount;
    private BigDecimal totalAmount;
    private BigDecimal outstandingBalance;
    private LoanStatus status;
    private Integer creditScore;
    private String rejectionReason;
    private LocalDate disbursementDate;
    private LocalDate firstDueDate;
    private LocalDateTime createdAt;
}
