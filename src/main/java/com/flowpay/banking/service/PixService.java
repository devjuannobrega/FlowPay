package com.flowpay.banking.service;

import com.flowpay.banking.dto.pix.PixKeyResponse;
import com.flowpay.banking.dto.pix.PixTransferRequest;
import com.flowpay.banking.dto.pix.RegisterPixKeyRequest;
import com.flowpay.banking.dto.transaction.TransactionResponse;
import com.flowpay.banking.dto.transaction.TransferRequest;
import com.flowpay.banking.entity.Account;
import com.flowpay.banking.entity.PixKey;
import com.flowpay.banking.enums.PixKeyType;
import com.flowpay.banking.enums.TransactionType;
import com.flowpay.banking.exception.AccountNotFoundException;
import com.flowpay.banking.exception.BusinessException;
import com.flowpay.banking.exception.DuplicatePixKeyException;
import com.flowpay.banking.exception.UnauthorizedException;
import com.flowpay.banking.repository.AccountRepository;
import com.flowpay.banking.repository.PixKeyRepository;
import com.flowpay.banking.security.SecurityUtils;
import com.flowpay.banking.util.CpfValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service para gerenciamento de chaves PIX
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PixService {

    private final PixKeyRepository pixKeyRepository;
    private final AccountRepository accountRepository;
    private final TransactionService transactionService;
    private final AccountService accountService;

    /**
     * Registra uma nova chave PIX
     */
    @Transactional
    public PixKeyResponse registerPixKey(RegisterPixKeyRequest request) {
        log.info("Registrando chave PIX tipo {} para conta {}", request.getKeyType(), request.getAccountId());

        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new AccountNotFoundException("Conta não encontrada"));

        accountService.validateAccountForTransaction(account);

        // Verificar permissão
        if (!SecurityUtils.canAccessResource(account.getUser().getId())) {
            throw new UnauthorizedException("Acesso negado");
        }

        // Validar formato da chave
        validatePixKey(request.getKeyType(), request.getKeyValue());

        // Verificar se chave já existe
        if (pixKeyRepository.existsByKeyValueAndActiveTrue(request.getKeyValue())) {
            throw new DuplicatePixKeyException("Chave PIX já cadastrada");
        }

        // Verificar limites por tipo
        long existingKeysOfType = pixKeyRepository.countByAccountAndKeyTypeAndActiveTrue(
                account, request.getKeyType());

        if (request.getKeyType() == PixKeyType.RANDOM && existingKeysOfType >= 5) {
            throw new BusinessException("Você já possui o máximo de 5 chaves aleatórias");
        }

        if (request.getKeyType() != PixKeyType.RANDOM && existingKeysOfType >= 1) {
            throw new BusinessException("Você já possui uma chave " + request.getKeyType());
        }

        // Gerar chave aleatória se necessário
        String keyValue = request.getKeyValue();
        if (request.getKeyType() == PixKeyType.RANDOM) {
            keyValue = generateRandomPixKey();
        }

        PixKey pixKey = PixKey.builder()
                .account(account)
                .keyType(request.getKeyType())
                .keyValue(keyValue)
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();

        pixKey = pixKeyRepository.save(pixKey);
        log.info("Chave PIX registrada com sucesso: {} - {}", pixKey.getKeyType(), pixKey.getKeyValue());

        return mapToPixKeyResponse(pixKey);
    }

    /**
     * Remove uma chave PIX
     */
    @Transactional
    public void deletePixKey(Long pixKeyId) {
        PixKey pixKey = pixKeyRepository.findById(pixKeyId)
                .orElseThrow(() -> new BusinessException("Chave PIX não encontrada"));

        // Verificar permissão
        if (!SecurityUtils.canAccessResource(pixKey.getAccount().getUser().getId())) {
            throw new UnauthorizedException("Acesso negado");
        }

        pixKey.setActive(false);
        pixKeyRepository.save(pixKey);

        log.info("Chave PIX desativada: {}", pixKeyId);
    }

    /**
     * Lista todas as chaves PIX do usuário
     */
    @Transactional(readOnly = true)
    public List<PixKeyResponse> getMyPixKeys() {
        Long userId = SecurityUtils.getCurrentUserId();
        List<Account> accounts = accountRepository.findByUserIdAndDeletedFalse(userId);

        return pixKeyRepository.findByAccountInAndActiveTrue(accounts).stream()
                .map(this::mapToPixKeyResponse)
                .collect(Collectors.toList());
    }

    /**
     * Busca uma chave PIX
     */
    @Transactional(readOnly = true)
    public PixKeyResponse findPixKey(String keyValue) {
        PixKey pixKey = pixKeyRepository.findByKeyValueAndActiveTrue(keyValue)
                .orElseThrow(() -> new BusinessException("Chave PIX não encontrada"));

        return mapToPixKeyResponse(pixKey);
    }

    /**
     * Realiza transferência PIX
     */
    @Transactional
    public TransactionResponse pixTransfer(PixTransferRequest request) {
        log.info("Processando transferência PIX de conta {} para chave {}",
                request.getSourceAccountId(), request.getDestinationPixKey());

        // Buscar conta destino pela chave PIX
        PixKey destinationPixKey = pixKeyRepository.findByKeyValueAndActiveTrue(request.getDestinationPixKey())
                .orElseThrow(() -> new BusinessException("Chave PIX não encontrada"));

        // Criar requisição de transferência
        TransferRequest transferRequest = TransferRequest.builder()
                .sourceAccountId(request.getSourceAccountId())
                .destinationAccountId(destinationPixKey.getAccount().getId())
                .amount(request.getAmount())
                .type(TransactionType.PIX)
                .description(request.getDescription() != null ? request.getDescription() : "PIX")
                .idempotencyKey(request.getIdempotencyKey())
                .build();

        // Executar transferência usando TransactionService
        return transactionService.transfer(transferRequest);
    }

    /**
     * Valida formato da chave PIX
     */
    private void validatePixKey(PixKeyType keyType, String keyValue) {
        switch (keyType) {
            case CPF:
                if (!CpfValidator.isValid(keyValue)) {
                    throw new BusinessException("CPF inválido");
                }
                if (!keyValue.matches("\\d{11}")) {
                    throw new BusinessException("CPF deve conter 11 dígitos");
                }
                break;

            case EMAIL:
                if (!keyValue.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                    throw new BusinessException("Email inválido");
                }
                break;

            case PHONE:
                if (!keyValue.matches("\\d{10,11}")) {
                    throw new BusinessException("Telefone deve conter 10 ou 11 dígitos (DDD + número)");
                }
                break;

            case RANDOM:
                // Chave aleatória será gerada automaticamente
                break;

            default:
                throw new BusinessException("Tipo de chave PIX inválido");
        }
    }

    /**
     * Gera chave PIX aleatória (formato UUID)
     */
    private String generateRandomPixKey() {
        String key;
        do {
            key = UUID.randomUUID().toString();
        } while (pixKeyRepository.existsByKeyValueAndActiveTrue(key));
        return key;
    }

    /**
     * Mapeia PixKey para PixKeyResponse
     */
    private PixKeyResponse mapToPixKeyResponse(PixKey pixKey) {
        return PixKeyResponse.builder()
                .id(pixKey.getId())
                .keyType(pixKey.getKeyType())
                .keyValue(pixKey.getKeyValue())
                .accountId(pixKey.getAccount().getId())
                .accountNumber(pixKey.getAccount().getAccountNumber())
                .isActive(pixKey.getActive())
                .createdAt(pixKey.getCreatedAt())
                .build();
    }
}
