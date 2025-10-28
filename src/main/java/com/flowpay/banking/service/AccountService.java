package com.flowpay.banking.service;

import com.flowpay.banking.dto.account.AccountResponse;
import com.flowpay.banking.dto.account.CreateAccountRequest;
import com.flowpay.banking.dto.account.UpdateLimitRequest;
import com.flowpay.banking.entity.Account;
import com.flowpay.banking.entity.User;
import com.flowpay.banking.enums.AccountStatus;
import com.flowpay.banking.enums.AccountType;
import com.flowpay.banking.exception.AccountBlockedException;
import com.flowpay.banking.exception.AccountNotFoundException;
import com.flowpay.banking.exception.BusinessException;
import com.flowpay.banking.exception.UnauthorizedException;
import com.flowpay.banking.exception.UserNotFoundException;
import com.flowpay.banking.repository.AccountRepository;
import com.flowpay.banking.repository.UserRepository;
import com.flowpay.banking.security.SecurityUtils;
import com.flowpay.banking.util.AccountNumberGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service para gestão de contas bancárias
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    /**
     * Cria uma nova conta para o usuário
     */
    @Transactional
    public AccountResponse createAccount(CreateAccountRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        User user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));

        // Verificar se já existe uma conta do mesmo tipo
        if (accountRepository.existsByUserIdAndTypeAndDeletedFalse(userId, request.getTipo())) {
            throw new BusinessException("Você já possui uma conta " + request.getTipo());
        }

        // Gerar número da conta
        String[] accountDetails = AccountNumberGenerator.generateCompleteAccount("0001");

        Account account = Account.builder()
                .user(user)
                .agencia("0001")
                .accountNumber(accountDetails[0])
                .digitoVerificador(accountDetails[1])
                .type(request.getTipo())
                .saldo(request.getDepositoInicial() != null ? request.getDepositoInicial() : BigDecimal.ZERO)
                .saldoBloqueado(BigDecimal.ZERO)
                .limiteDiario(request.getLimiteDiario() != null ? request.getLimiteDiario() : new BigDecimal("5000.00"))
                .status(AccountStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .deleted(false)
                .build();

        // Se for poupança, definir data de aniversário
        if (request.getTipo() == AccountType.SAVINGS) {
            account.setDiaAniversario(LocalDate.now().getDayOfMonth());
        }

        account = accountRepository.save(account);
        log.info("Conta criada: {} para usuário {}", account.getAccountNumber(), userId);

        return mapToAccountResponse(account);
    }

    /**
     * Retorna todas as contas do usuário autenticado
     */
    @Transactional(readOnly = true)
    public List<AccountResponse> getMyAccounts() {
        Long userId = SecurityUtils.getCurrentUserId();
        List<Account> accounts = accountRepository.findByUserIdAndDeletedFalse(userId);
        return accounts.stream()
                .map(this::mapToAccountResponse)
                .collect(Collectors.toList());
    }

    /**
     * Retorna informações de uma conta específica
     */
    @Transactional(readOnly = true)
    public AccountResponse getAccountById(Long accountId) {
        Account account = accountRepository.findByIdAndDeletedFalse(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Conta não encontrada"));

        // Verificar permissão
        if (!SecurityUtils.canAccessResource(account.getUser().getId())) {
            throw new UnauthorizedException("Acesso negado");
        }

        return mapToAccountResponse(account);
    }

    /**
     * Retorna informações de uma conta por número
     */
    @Transactional(readOnly = true)
    public AccountResponse getAccountByNumber(String agencia, String numero, String digito) {
        Account account = accountRepository.findByAgenciaAndAccountNumberAndDigitoVerificadorAndDeletedFalse(
                        agencia, numero, digito)
                .orElseThrow(() -> new AccountNotFoundException("Conta não encontrada"));

        // Verificar permissão
        if (!SecurityUtils.canAccessResource(account.getUser().getId())) {
            throw new UnauthorizedException("Acesso negado");
        }

        return mapToAccountResponse(account);
    }

    /**
     * Atualiza o limite diário da conta
     */
    @Transactional
    public AccountResponse updateLimit(Long accountId, UpdateLimitRequest request) {
        Account account = accountRepository.findByIdAndDeletedFalse(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Conta não encontrada"));

        // Verificar permissão
        if (!SecurityUtils.canAccessResource(account.getUser().getId())) {
            throw new UnauthorizedException("Acesso negado");
        }

        // Validar limite (máximo R$ 50.000)
        if (request.getNovoLimite().compareTo(new BigDecimal("50000.00")) > 0) {
            throw new BusinessException("Limite máximo é R$ 50.000,00");
        }

        account.setLimiteDiario(request.getNovoLimite());
        account.setUpdatedAt(LocalDateTime.now());
        account = accountRepository.save(account);

        log.info("Limite atualizado para conta {}: R$ {}", accountId, request.getNovoLimite());

        return mapToAccountResponse(account);
    }

    /**
     * Bloqueia uma conta
     */
    @Transactional
    public void blockAccount(Long accountId) {
        Account account = accountRepository.findByIdAndDeletedFalse(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Conta não encontrada"));

        // Verificar permissão
        if (!SecurityUtils.canAccessResource(account.getUser().getId())) {
            throw new UnauthorizedException("Acesso negado");
        }

        account.setStatus(AccountStatus.BLOCKED);
        account.setUpdatedAt(LocalDateTime.now());
        accountRepository.save(account);

        log.info("Conta bloqueada: {}", accountId);
    }

    /**
     * Desbloqueia uma conta
     */
    @Transactional
    public void unblockAccount(Long accountId) {
        Account account = accountRepository.findByIdAndDeletedFalse(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Conta não encontrada"));

        // Verificar permissão
        if (!SecurityUtils.canAccessResource(account.getUser().getId())) {
            throw new UnauthorizedException("Acesso negado");
        }

        if (account.getStatus() != AccountStatus.BLOCKED) {
            throw new BusinessException("Conta não está bloqueada");
        }

        account.setStatus(AccountStatus.ACTIVE);
        account.setUpdatedAt(LocalDateTime.now());
        accountRepository.save(account);

        log.info("Conta desbloqueada: {}", accountId);
    }

    /**
     * Fecha uma conta
     */
    @Transactional
    public void closeAccount(Long accountId) {
        Account account = accountRepository.findByIdAndDeletedFalse(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Conta não encontrada"));

        // Verificar permissão
        if (!SecurityUtils.canAccessResource(account.getUser().getId())) {
            throw new UnauthorizedException("Acesso negado");
        }

        // Verificar se há saldo
        if (account.getSaldo().compareTo(BigDecimal.ZERO) != 0) {
            throw new BusinessException("Não é possível fechar conta com saldo. Por favor, transfira o saldo primeiro.");
        }

        account.setStatus(AccountStatus.CLOSED);
        account.setDeleted(true);
        account.setUpdatedAt(LocalDateTime.now());
        accountRepository.save(account);

        log.info("Conta fechada: {}", accountId);
    }

    /**
     * Verifica se a conta está ativa e disponível para operações
     */
    public void validateAccountForTransaction(Account account) {
        if (account.getDeleted()) {
            throw new AccountNotFoundException("Conta não encontrada");
        }

        if (account.getStatus() == AccountStatus.CLOSED) {
            throw new AccountBlockedException("Conta está fechada");
        }

        if (account.getStatus() == AccountStatus.BLOCKED) {
            throw new AccountBlockedException("Conta está bloqueada");
        }

        if (account.getStatus() == AccountStatus.UNDER_REVIEW) {
            throw new AccountBlockedException("Conta está em análise");
        }
    }

    /**
     * Mapeia Account para AccountResponse
     */
    private AccountResponse mapToAccountResponse(Account account) {
        return AccountResponse.builder()
                .id(account.getId())
                .agencia(account.getAgencia())
                .numero(account.getAccountNumber())
                .digitoVerificador(account.getDigitoVerificador())
                .tipo(account.getType())
                .saldo(account.getSaldo())
                .saldoDisponivel(account.getSaldoDisponivel())
                .saldoBloqueado(account.getSaldoBloqueado())
                .limiteDiario(account.getLimiteDiario())
                .status(account.getStatus())
                .dataAniversarioPoupanca(account.getDiaAniversario() != null ? LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonthValue(), account.getDiaAniversario()) : null)
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .build();
    }
}
