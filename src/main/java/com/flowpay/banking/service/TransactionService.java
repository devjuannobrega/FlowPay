package com.flowpay.banking.service;

import com.flowpay.banking.dto.transaction.*;
import com.flowpay.banking.entity.Account;
import com.flowpay.banking.entity.Transaction;
import com.flowpay.banking.enums.TransactionStatus;
import com.flowpay.banking.enums.TransactionType;
import com.flowpay.banking.exception.*;
import com.flowpay.banking.repository.AccountRepository;
import com.flowpay.banking.repository.TransactionRepository;
import com.flowpay.banking.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service para gerenciamento de transações com ACID
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final AccountService accountService;

    /**
     * Realiza depósito na conta
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public TransactionResponse deposit(DepositRequest request) {
        log.info("Processando depósito de R$ {} para conta {}", request.getAmount(), request.getAccountId());

        // Verificar idempotência
        if (request.getIdempotencyKey() != null) {
            Transaction existing = transactionRepository.findByIdempotencyKey(request.getIdempotencyKey())
                    .orElse(null);
            if (existing != null) {
                log.warn("Transação duplicada detectada: {}", request.getIdempotencyKey());
                return mapToTransactionResponse(existing);
            }
        }

        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new AccountNotFoundException("Conta não encontrada"));

        accountService.validateAccountForTransaction(account);

        // Validar permissão (apenas admin pode depositar para qualquer conta)
        if (!SecurityUtils.canAccessResource(account.getUser().getId()) && !SecurityUtils.isAdmin()) {
            throw new UnauthorizedException("Acesso negado");
        }

        BigDecimal balanceBefore = account.getSaldo();
        account.setSaldo(account.getSaldo().add(request.getAmount()));
        account.setUpdatedAt(LocalDateTime.now());
        accountRepository.save(account);

        Transaction transaction = Transaction.builder()
                .transactionHash(UUID.randomUUID().toString())
                .idempotencyKey(request.getIdempotencyKey())
                .type(TransactionType.DEPOSIT)
                .amount(request.getAmount())
                .fee(BigDecimal.ZERO)
                .description(request.getDescription() != null ? request.getDescription() : "Depósito")
                .destinationAccount(account)
                .balanceBefore(balanceBefore)
                .balanceAfter(account.getSaldo())
                .status(TransactionStatus.COMPLETED)
                .riskScore(0)
                .createdAt(LocalDateTime.now())
                .processedAt(LocalDateTime.now())
                .build();

        transaction = transactionRepository.save(transaction);
        log.info("Depósito realizado com sucesso: ID {}", transaction.getId());

        return mapToTransactionResponse(transaction);
    }

    /**
     * Realiza saque da conta
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public TransactionResponse withdrawal(WithdrawalRequest request) {
        log.info("Processando saque de R$ {} da conta {}", request.getAmount(), request.getAccountId());

        // Verificar idempotência
        if (request.getIdempotencyKey() != null) {
            Transaction existing = transactionRepository.findByIdempotencyKey(request.getIdempotencyKey())
                    .orElse(null);
            if (existing != null) {
                log.warn("Transação duplicada detectada: {}", request.getIdempotencyKey());
                return mapToTransactionResponse(existing);
            }
        }

        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new AccountNotFoundException("Conta não encontrada"));

        accountService.validateAccountForTransaction(account);

        // Validar permissão
        if (!SecurityUtils.canAccessResource(account.getUser().getId())) {
            throw new UnauthorizedException("Acesso negado");
        }

        // Verificar saldo disponível
        if (account.getSaldoDisponivel().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException("Saldo insuficiente");
        }

        // Verificar limite diário
        validateDailyLimit(account, request.getAmount());

        BigDecimal balanceBefore = account.getSaldo();
        account.setSaldo(account.getSaldo().subtract(request.getAmount()));
        account.setUpdatedAt(LocalDateTime.now());
        accountRepository.save(account);

        Transaction transaction = Transaction.builder()
                .transactionHash(UUID.randomUUID().toString())
                .idempotencyKey(request.getIdempotencyKey())
                .type(TransactionType.WITHDRAWAL)
                .amount(request.getAmount())
                .fee(BigDecimal.ZERO)
                .description(request.getDescription() != null ? request.getDescription() : "Saque")
                .sourceAccount(account)
                .balanceBefore(balanceBefore)
                .balanceAfter(account.getSaldo())
                .status(TransactionStatus.COMPLETED)
                .riskScore(calculateRiskScore(account, request.getAmount(), TransactionType.WITHDRAWAL))
                .createdAt(LocalDateTime.now())
                .processedAt(LocalDateTime.now())
                .build();

        transaction = transactionRepository.save(transaction);
        log.info("Saque realizado com sucesso: ID {}", transaction.getId());

        return mapToTransactionResponse(transaction);
    }

    /**
     * Realiza transferência entre contas com ACID
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public TransactionResponse transfer(TransferRequest request) {
        log.info("Processando transferência de R$ {} da conta {} para conta {}",
                request.getAmount(), request.getSourceAccountId(), request.getDestinationAccountId());

        // Verificar idempotência
        if (request.getIdempotencyKey() != null) {
            Transaction existing = transactionRepository.findByIdempotencyKey(request.getIdempotencyKey())
                    .orElse(null);
            if (existing != null) {
                log.warn("Transação duplicada detectada: {}", request.getIdempotencyKey());
                return mapToTransactionResponse(existing);
            }
        }

        // Verificar se não é transferência para mesma conta
        if (request.getSourceAccountId().equals(request.getDestinationAccountId())) {
            throw new InvalidTransactionException("Não é possível transferir para a mesma conta");
        }

        // Carregar contas (com lock pessimista para evitar race conditions)
        Account sourceAccount = accountRepository.findById(request.getSourceAccountId())
                .orElseThrow(() -> new AccountNotFoundException("Conta de origem não encontrada"));

        Account destAccount = accountRepository.findById(request.getDestinationAccountId())
                .orElseThrow(() -> new AccountNotFoundException("Conta de destino não encontrada"));

        // Validar contas
        accountService.validateAccountForTransaction(sourceAccount);
        accountService.validateAccountForTransaction(destAccount);

        // Validar permissão
        if (!SecurityUtils.canAccessResource(sourceAccount.getUser().getId())) {
            throw new UnauthorizedException("Acesso negado");
        }

        // Verificar saldo disponível
        if (sourceAccount.getSaldoDisponivel().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException("Saldo insuficiente");
        }

        // Verificar limite diário
        validateDailyLimit(sourceAccount, request.getAmount());

        // Calcular taxa (se houver)
        BigDecimal taxa = calculateTransferFee(request.getType(), request.getAmount());
        BigDecimal totalAmount = request.getAmount().add(taxa);

        if (sourceAccount.getSaldoDisponivel().compareTo(totalAmount) < 0) {
            throw new InsufficientBalanceException("Saldo insuficiente para cobrir valor + taxas");
        }

        // Realizar transferência (ACID)
        BigDecimal sourceBalanceBefore = sourceAccount.getSaldo();
        sourceAccount.setSaldo(sourceAccount.getSaldo().subtract(totalAmount));
        sourceAccount.setUpdatedAt(LocalDateTime.now());

        BigDecimal destBalanceBefore = destAccount.getSaldo();
        destAccount.setSaldo(destAccount.getSaldo().add(request.getAmount()));
        destAccount.setUpdatedAt(LocalDateTime.now());

        accountRepository.save(sourceAccount);
        accountRepository.save(destAccount);

        // Criar registro de transação
        Transaction transaction = Transaction.builder()
                .transactionHash(UUID.randomUUID().toString())
                .idempotencyKey(request.getIdempotencyKey())
                .type(request.getType())
                .amount(request.getAmount())
                .fee(taxa)
                .description(request.getDescription() != null ? request.getDescription() : "Transferência")
                .sourceAccount(sourceAccount)
                .destinationAccount(destAccount)
                .balanceBefore(sourceBalanceBefore)
                .balanceAfter(sourceAccount.getSaldo())
                .status(TransactionStatus.COMPLETED)
                .riskScore(calculateRiskScore(sourceAccount, request.getAmount(), request.getType()))
                .createdAt(LocalDateTime.now())
                .processedAt(LocalDateTime.now())
                .build();

        transaction = transactionRepository.save(transaction);
        log.info("Transferência realizada com sucesso: ID {}", transaction.getId());

        return mapToTransactionResponse(transaction);
    }

    /**
     * Retorna histórico de transações da conta
     */
    @Transactional(readOnly = true)
    public List<TransactionResponse> getAccountTransactions(Long accountId, LocalDateTime startDate, LocalDateTime endDate) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Conta não encontrada"));

        if (!SecurityUtils.canAccessResource(account.getUser().getId())) {
            throw new UnauthorizedException("Acesso negado");
        }

        List<Transaction> transactions;
        if (startDate != null && endDate != null) {
            transactions = transactionRepository.findByAccountIdAndDateRange(accountId, startDate, endDate);
        } else {
            transactions = transactionRepository.findByAccountIdOrderByCreatedAtDesc(accountId);
        }

        return transactions.stream()
                .map(this::mapToTransactionResponse)
                .collect(Collectors.toList());
    }

    /**
     * Retorna detalhes de uma transação específica
     */
    @Transactional(readOnly = true)
    public TransactionResponse getTransactionById(Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new BusinessException("Transação não encontrada"));

        // Verificar permissão
        boolean hasAccess = false;
        if (transaction.getSourceAccount() != null) {
            hasAccess = SecurityUtils.canAccessResource(transaction.getSourceAccount().getUser().getId());
        }
        if (!hasAccess && transaction.getDestinationAccount() != null) {
            hasAccess = SecurityUtils.canAccessResource(transaction.getDestinationAccount().getUser().getId());
        }

        if (!hasAccess) {
            throw new UnauthorizedException("Acesso negado");
        }

        return mapToTransactionResponse(transaction);
    }

    /**
     * Estorna uma transação
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public TransactionResponse reverseTransaction(Long transactionId) {
        Transaction originalTransaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new BusinessException("Transação não encontrada"));

        // Apenas admin pode estornar
        if (!SecurityUtils.isAdmin()) {
            throw new UnauthorizedException("Apenas administradores podem estornar transações");
        }

        if (originalTransaction.getStatus() != TransactionStatus.COMPLETED) {
            throw new InvalidTransactionException("Apenas transações completadas podem ser estornadas");
        }

        if (originalTransaction.getReversedTransaction() != null) {
            throw new InvalidTransactionException("Transação já foi estornada");
        }

        // Realizar estorno
        Transaction reversal = Transaction.builder()
                .transactionHash(UUID.randomUUID().toString())
                .type(TransactionType.REVERSAL)
                .amount(originalTransaction.getAmount())
                .fee(BigDecimal.ZERO)
                .description("Estorno de transação " + originalTransaction.getId())
                .sourceAccount(originalTransaction.getDestinationAccount())
                .destinationAccount(originalTransaction.getSourceAccount())
                .status(TransactionStatus.COMPLETED)
                .riskScore(0)
                .createdAt(LocalDateTime.now())
                .processedAt(LocalDateTime.now())
                .build();

        reversal = transactionRepository.save(reversal);

        // Atualizar transação original
        originalTransaction.setReversedTransaction(reversal);
        transactionRepository.save(originalTransaction);

        // Atualizar saldos
        if (originalTransaction.getSourceAccount() != null) {
            Account sourceAccount = originalTransaction.getSourceAccount();
            sourceAccount.setSaldo(sourceAccount.getSaldo().add(originalTransaction.getAmount().add(originalTransaction.getFee())));
            accountRepository.save(sourceAccount);
        }

        if (originalTransaction.getDestinationAccount() != null) {
            Account destAccount = originalTransaction.getDestinationAccount();
            destAccount.setSaldo(destAccount.getSaldo().subtract(originalTransaction.getAmount()));
            accountRepository.save(destAccount);
        }

        log.info("Transação {} estornada com sucesso", transactionId);

        return mapToTransactionResponse(reversal);
    }

    /**
     * Valida limite diário de transações
     */
    private void validateDailyLimit(Account account, BigDecimal valor) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(23, 59, 59);

        BigDecimal dailyTotal = transactionRepository.calculateDailyTotal(
                account.getId(), startOfDay, endOfDay);

        if (dailyTotal == null) {
            dailyTotal = BigDecimal.ZERO;
        }

        if (dailyTotal.add(valor).compareTo(account.getLimiteDiario()) > 0) {
            throw new DailyLimitExceededException(
                    String.format("Limite diário excedido. Limite: R$ %.2f, Usado: R$ %.2f, Tentativa: R$ %.2f",
                            account.getLimiteDiario(), dailyTotal, valor));
        }
    }

    /**
     * Calcula taxa de transferência
     */
    private BigDecimal calculateTransferFee(TransactionType type, BigDecimal valor) {
        // TED/DOC têm taxa, transferências internas não
        if (type == TransactionType.TED || type == TransactionType.DOC) {
            return new BigDecimal("8.50"); // Taxa fixa
        }
        return BigDecimal.ZERO;
    }

    /**
     * Calcula score de risco da transação (0-100)
     */
    private int calculateRiskScore(Account account, BigDecimal valor, TransactionType tipo) {
        int riskScore = 0;

        // Valores muito altos aumentam risco
        if (valor.compareTo(new BigDecimal("10000")) > 0) {
            riskScore += 30;
        } else if (valor.compareTo(new BigDecimal("5000")) > 0) {
            riskScore += 15;
        }

        // Múltiplas transações em curto período
        long recentTransactions = transactionRepository.countRecentTransactions(
                account.getId(), LocalDateTime.now().minusMinutes(5));
        if (recentTransactions > 3) {
            riskScore += 25;
        }

        // Transações em horários atípicos
        int hour = LocalDateTime.now().getHour();
        if (hour < 6 || hour > 22) {
            riskScore += 10;
        }

        // PIX tem risco ligeiramente maior
        if (tipo == TransactionType.PIX) {
            riskScore += 5;
        }

        return Math.min(riskScore, 100);
    }

    /**
     * Mapeia Transaction para TransactionResponse
     */
    private TransactionResponse mapToTransactionResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .transactionHash(transaction.getTransactionHash())
                .tipo(transaction.getType())
                .valor(transaction.getAmount())
                .taxa(transaction.getFee())
                .descricao(transaction.getDescription())
                .status(transaction.getStatus())
                .sourceAccountId(transaction.getSourceAccount() != null ? transaction.getSourceAccount().getId() : null)
                .sourceAccountNumber(transaction.getSourceAccount() != null ? transaction.getSourceAccount().getAccountNumber() : null)
                .destinationAccountId(transaction.getDestinationAccount() != null ? transaction.getDestinationAccount().getId() : null)
                .destinationAccountNumber(transaction.getDestinationAccount() != null ? transaction.getDestinationAccount().getAccountNumber() : null)
                .balanceBefore(transaction.getBalanceBefore())
                .balanceAfter(transaction.getBalanceAfter())
                .riskScore(transaction.getRiskScore())
                .createdAt(transaction.getCreatedAt())
                .processedAt(transaction.getProcessedAt())
                .isReversed(transaction.getReversedTransaction() != null)
                .reversedTransactionId(transaction.getReversedTransaction() != null ? transaction.getReversedTransaction().getId() : null)
                .build();
    }
}
