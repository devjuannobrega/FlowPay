package com.flowpay.banking.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para estatísticas do sistema
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemStatsResponse {

    private Long totalUsers;
    private Long activeUsers;
    private Long totalAccounts;
    private Long totalTransactions;
    private BigDecimal totalTransactionVolume;
    private Long todayTransactions;
    private BigDecimal todayTransactionVolume;
    private Long pendingLoans;
    private Long activeLoans;
    private Long openSupportTickets;
    private Long fraudAlerts;
}
