package com.flowpay.banking.service;

import com.flowpay.banking.dto.admin.SystemStatsResponse;
import com.flowpay.banking.dto.admin.UserManagementRequest;
import com.flowpay.banking.entity.User;
import com.flowpay.banking.enums.UserStatus;
import com.flowpay.banking.exception.UnauthorizedException;
import com.flowpay.banking.exception.UserNotFoundException;
import com.flowpay.banking.repository.*;
import com.flowpay.banking.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final LoanRepository loanRepository;
    private final SupportTicketRepository ticketRepository;
    private final FraudAlertRepository fraudAlertRepository;

    @Transactional(readOnly = true)
    public SystemStatsResponse getSystemStats() {
        if (!SecurityUtils.isAdmin()) {
            throw new UnauthorizedException("Acesso negado");
        }

        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByStatus(UserStatus.ATIVO);
        long totalAccounts = accountRepository.count();
        long totalTransactions = transactionRepository.count();

        BigDecimal totalVolume = transactionRepository.calculateTotalVolume() != null ?
                transactionRepository.calculateTotalVolume() : BigDecimal.ZERO;

        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(23, 59, 59);

        long todayTransactions = transactionRepository.countByCreatedAtBetween(startOfDay, endOfDay);
        BigDecimal todayVolume = transactionRepository.calculateTotalVolumeByDateRange(startOfDay, endOfDay);

        long openTickets = ticketRepository.countByStatus(com.flowpay.banking.enums.SupportTicketStatus.OPEN);
        long fraudAlerts = fraudAlertRepository.countByStatusNotResolved();

        return SystemStatsResponse.builder()
                .totalUsers(totalUsers)
                .activeUsers(activeUsers)
                .totalAccounts(totalAccounts)
                .totalTransactions(totalTransactions)
                .totalTransactionVolume(totalVolume)
                .todayTransactions(todayTransactions)
                .todayTransactionVolume(todayVolume != null ? todayVolume : BigDecimal.ZERO)
                .openSupportTickets(openTickets)
                .fraudAlerts(fraudAlerts)
                .build();
    }

    @Transactional
    public void updateUserStatus(Long userId, UserManagementRequest request) {
        if (!SecurityUtils.isAdmin()) {
            throw new UnauthorizedException("Acesso negado");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));

        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }

        if (request.getRoles() != null) {
            user.setRoles(request.getRoles());
        }

        userRepository.save(user);
        log.info("Usuário {} atualizado por admin", userId);
    }
}
