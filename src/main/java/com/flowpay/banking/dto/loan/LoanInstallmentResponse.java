package com.flowpay.banking.dto.loan;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para resposta de parcela de empréstimo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanInstallmentResponse {

    private Long id;
    private Integer installmentNumber;
    private BigDecimal installmentAmount;
    private BigDecimal principalAmount;
    private BigDecimal interestAmount;
    private BigDecimal paidAmount;
    private LocalDate dueDate;
    private LocalDate paymentDate;
    private boolean isPaid;
    private boolean isOverdue;
    private BigDecimal lateFee;
}
