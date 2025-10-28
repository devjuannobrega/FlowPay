package com.flowpay.banking.service;

import com.flowpay.banking.dto.loan.*;
import com.flowpay.banking.entity.Account;
import com.flowpay.banking.entity.Loan;
import com.flowpay.banking.entity.LoanInstallment;
import com.flowpay.banking.enums.LoanStatus;
import com.flowpay.banking.exception.*;
import com.flowpay.banking.repository.*;
import com.flowpay.banking.security.SecurityUtils;
import com.flowpay.banking.util.CurrencyUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanRepository loanRepository;
    private final LoanInstallmentRepository installmentRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final AccountService accountService;

    @Transactional
    public LoanResponse applyForLoan(LoanApplicationRequest request) {
        log.info("Processando solicitação de empréstimo de R$ {} para conta {}",
                request.getRequestedAmount(), request.getAccountId());

        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new AccountNotFoundException("Conta não encontrada"));

        accountService.validateAccountForTransaction(account);

        if (!SecurityUtils.canAccessResource(account.getUser().getId())) {
            throw new UnauthorizedException("Acesso negado");
        }

        // Análise de crédito
        int creditScore = calculateCreditScore(account, request.getRequestedAmount(), request.getMonthlyIncome());
        log.info("Credit score calculado: {}", creditScore);

        // Verificar score mínimo
        if (creditScore < 300) {
            Loan loan = createRejectedLoan(account, request, creditScore, "Score de crédito insuficiente");
            return mapToLoanResponse(loan);
        }

        // Calcular valor aprovado (pode ser menor que solicitado)
        BigDecimal approvedAmount = calculateApprovedAmount(request.getRequestedAmount(), creditScore);

        // Calcular taxa de juros baseada no score
        BigDecimal interestRate = calculateInterestRate(creditScore);

        // Calcular valor da parcela (Tabela Price)
        BigDecimal monthlyRate = interestRate.divide(new BigDecimal("12"), 10, BigDecimal.ROUND_HALF_UP);
        BigDecimal installmentAmount = CurrencyUtils.calculatePriceInstallment(
                approvedAmount, monthlyRate.multiply(new BigDecimal("100")), request.getNumberOfInstallments());

        BigDecimal totalAmount = installmentAmount.multiply(new BigDecimal(request.getNumberOfInstallments()));

        // Criar empréstimo
        Loan loan = Loan.builder()
                .account(account)
                .requestedAmount(request.getRequestedAmount())
                .approvedAmount(approvedAmount)
                .monthlyInterestRate(interestRate)
                .installments(request.getNumberOfInstallments())
                .installmentAmount(installmentAmount)
                .totalAmount(totalAmount)
                .status(LoanStatus.APPROVED)
                .creditScore(creditScore)
                .build();

        loan = loanRepository.save(loan);

        // Criar parcelas
        createInstallments(loan);

        log.info("Empréstimo aprovado: {} - R$ {}", loan.getId(), approvedAmount);

        return mapToLoanResponse(loan);
    }

    @Transactional
    public LoanResponse disburseLoan(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new BusinessException("Empréstimo não encontrado"));

        if (!SecurityUtils.canAccessResource(loan.getAccount().getUser().getId()) && !SecurityUtils.isAdmin()) {
            throw new UnauthorizedException("Acesso negado");
        }

        if (loan.getStatus() != LoanStatus.APPROVED) {
            throw new BusinessException("Empréstimo não está aprovado");
        }

        // Creditar valor na conta
        Account account = loan.getAccount();
        account.setSaldo(account.getSaldo().add(loan.getApprovedAmount()));
        accountRepository.save(account);

        loan.setStatus(LoanStatus.ACTIVE);
        loan.setDisbursementDate(LocalDate.now());
        loanRepository.save(loan);

        log.info("Empréstimo desembolsado: {}", loanId);

        return mapToLoanResponse(loan);
    }

    @Transactional
    public void payInstallment(PayLoanInstallmentRequest request) {
        LoanInstallment installment = installmentRepository.findById(request.getInstallmentId())
                .orElseThrow(() -> new BusinessException("Parcela não encontrada"));

        if (installment.getPaymentDate() != null) {
            throw new BusinessException("Parcela já está paga");
        }

        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new AccountNotFoundException("Conta não encontrada"));

        if (!SecurityUtils.canAccessResource(account.getUser().getId())) {
            throw new UnauthorizedException("Acesso negado");
        }

        // Calcular valor com juros se atrasada
        BigDecimal amountToPay = installment.getAmount();
        if (installment.getDueDate().isBefore(LocalDate.now())) {
            long daysLate = LocalDate.now().toEpochDay() - installment.getDueDate().toEpochDay();
            BigDecimal lateFee = CurrencyUtils.calculateLateFee(installment.getAmount());
            BigDecimal lateInterest = CurrencyUtils.calculateDailyInterest(installment.getAmount(), (int) daysLate);
            amountToPay = amountToPay.add(lateFee).add(lateInterest);
            installment.setLatePaymentFee(lateFee);
            installment.setLateInterest(lateInterest);
        }

        // Verificar saldo
        if (account.getSaldoDisponivel().compareTo(amountToPay) < 0) {
            throw new InsufficientBalanceException("Saldo insuficiente");
        }

        // Debitar da conta
        account.setSaldo(account.getSaldo().subtract(amountToPay));
        accountRepository.save(account);

        // Atualizar parcela
        installment.setPaymentDate(LocalDate.now());
        installment.setPaidAmount(amountToPay);
        installment.setStatus(com.flowpay.banking.enums.TransactionStatus.COMPLETED);
        installmentRepository.save(installment);

        // Atualizar empréstimo
        Loan loan = installment.getLoan();
        loan.setPaidAmount(loan.getPaidAmount().add(amountToPay));

        // Verificar se todas as parcelas foram pagas
        long totalInstallments = loan.getInstallments();
        long paidInstallments = installmentRepository.countPaidInstallmentsByLoanId(loan.getId());
        if (paidInstallments >= totalInstallments) {
            loan.setStatus(LoanStatus.PAID_OFF);
        }

        loanRepository.save(loan);

        log.info("Parcela {} paga para empréstimo {}", installment.getId(), loan.getId());
    }

    @Transactional(readOnly = true)
    public List<LoanResponse> getMyLoans() {
        Long userId = SecurityUtils.getCurrentUserId();
        List<Account> accounts = accountRepository.findByUserIdAndDeletedFalse(userId);

        return loanRepository.findByAccountIn(accounts).stream()
                .map(this::mapToLoanResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<LoanInstallmentResponse> getLoanInstallments(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new BusinessException("Empréstimo não encontrado"));

        if (!SecurityUtils.canAccessResource(loan.getAccount().getUser().getId())) {
            throw new UnauthorizedException("Acesso negado");
        }

        return installmentRepository.findByLoanOrderByInstallmentNumber(loan).stream()
                .map(this::mapToInstallmentResponse)
                .collect(Collectors.toList());
    }

    private int calculateCreditScore(Account account, BigDecimal requestedAmount, BigDecimal monthlyIncome) {
        int score = 500; // Base score

        // Tempo de conta
        long monthsSinceCreation = java.time.temporal.ChronoUnit.MONTHS.between(
                account.getCreatedAt().toLocalDate(), LocalDate.now());
        if (monthsSinceCreation > 24) score += 100;
        else if (monthsSinceCreation > 12) score += 50;

        // Histórico de transações
        long transactionCount = transactionRepository.countByAccountId(account.getId());
        if (transactionCount > 100) score += 50;
        else if (transactionCount > 50) score += 25;

        // Relação valor solicitado / renda
        BigDecimal debtToIncomeRatio = requestedAmount.divide(monthlyIncome, 4, BigDecimal.ROUND_HALF_UP);
        if (debtToIncomeRatio.compareTo(new BigDecimal("0.3")) < 0) score += 100;
        else if (debtToIncomeRatio.compareTo(new BigDecimal("0.5")) < 0) score += 50;
        else if (debtToIncomeRatio.compareTo(new BigDecimal("1.0")) > 0) score -= 200;

        // Empréstimos ativos
        long activeLoans = loanRepository.countByAccountAndStatus(account, LoanStatus.ACTIVE);
        score -= (int) (activeLoans * 50);

        return Math.max(0, Math.min(850, score));
    }

    private BigDecimal calculateApprovedAmount(BigDecimal requestedAmount, int creditScore) {
        if (creditScore >= 700) return requestedAmount;
        if (creditScore >= 600) return requestedAmount.multiply(new BigDecimal("0.8"));
        if (creditScore >= 500) return requestedAmount.multiply(new BigDecimal("0.6"));
        return requestedAmount.multiply(new BigDecimal("0.4"));
    }

    private BigDecimal calculateInterestRate(int creditScore) {
        if (creditScore >= 750) return new BigDecimal("1.99"); // 1.99% a.m.
        if (creditScore >= 650) return new BigDecimal("2.49");
        if (creditScore >= 550) return new BigDecimal("2.99");
        if (creditScore >= 450) return new BigDecimal("3.49");
        return new BigDecimal("3.99");
    }

    private void createInstallments(Loan loan) {
        LocalDate firstDueDate = LocalDate.now().plusMonths(1);
        loan.setFirstDueDate(firstDueDate);

        BigDecimal monthlyRate = loan.getMonthlyInterestRate()
                .divide(new BigDecimal("100"), 10, BigDecimal.ROUND_HALF_UP);

        for (int i = 1; i <= loan.getInstallments(); i++) {
            BigDecimal remainingBalance = loan.getApprovedAmount();
            BigDecimal interestAmount = remainingBalance.multiply(monthlyRate);
            BigDecimal principalAmount = loan.getInstallmentAmount().subtract(interestAmount);

            LoanInstallment installment = LoanInstallment.builder()
                    .loan(loan)
                    .installmentNumber(i)
                    .amount(loan.getInstallmentAmount())
                    .principalAmount(principalAmount)
                    .interestAmount(interestAmount)
                    .dueDate(firstDueDate.plusMonths(i - 1))
                    .status(com.flowpay.banking.enums.TransactionStatus.PENDING)
                    .build();

            installmentRepository.save(installment);
        }
    }

    private Loan createRejectedLoan(Account account, LoanApplicationRequest request, int creditScore, String reason) {
        Loan loan = Loan.builder()
                .account(account)
                .requestedAmount(request.getRequestedAmount())
                .approvedAmount(BigDecimal.ZERO)
                .installments(request.getNumberOfInstallments())
                .status(LoanStatus.REJECTED)
                .creditScore(creditScore)
                .rejectionReason(reason)
                .build();
        return loanRepository.save(loan);
    }

    private LoanResponse mapToLoanResponse(Loan loan) {
        return LoanResponse.builder()
                .id(loan.getId())
                .accountId(loan.getAccount().getId())
                .requestedAmount(loan.getRequestedAmount())
                .approvedAmount(loan.getApprovedAmount())
                .interestRate(loan.getMonthlyInterestRate())
                .numberOfInstallments(loan.getInstallments())
                .installmentAmount(loan.getInstallmentAmount())
                .totalAmount(loan.getTotalAmount())
                .outstandingBalance(loan.getRemainingAmount())
                .status(loan.getStatus())
                .creditScore(loan.getCreditScore())
                .rejectionReason(loan.getRejectionReason())
                .disbursementDate(loan.getDisbursementDate())
                .firstDueDate(loan.getFirstDueDate())
                .createdAt(loan.getCreatedAt())
                .build();
    }

    private LoanInstallmentResponse mapToInstallmentResponse(LoanInstallment installment) {
        return LoanInstallmentResponse.builder()
                .id(installment.getId())
                .installmentNumber(installment.getInstallmentNumber())
                .installmentAmount(installment.getAmount())
                .principalAmount(installment.getPrincipalAmount())
                .interestAmount(installment.getInterestAmount())
                .paidAmount(installment.getPaidAmount())
                .dueDate(installment.getDueDate())
                .paymentDate(installment.getPaymentDate())
                .isPaid(installment.getPaymentDate() != null)
                .isOverdue(installment.getDueDate().isBefore(LocalDate.now()) && installment.getPaymentDate() == null)
                .lateFee(installment.getLatePaymentFee())
                .build();
    }
}
