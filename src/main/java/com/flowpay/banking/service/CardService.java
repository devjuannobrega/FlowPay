package com.flowpay.banking.service;

import com.flowpay.banking.dto.card.CardResponse;
import com.flowpay.banking.dto.card.CreateCardRequest;
import com.flowpay.banking.dto.card.UpdateCardLimitRequest;
import com.flowpay.banking.entity.Account;
import com.flowpay.banking.entity.Card;
import com.flowpay.banking.enums.CardStatus;
import com.flowpay.banking.enums.CardType;
import com.flowpay.banking.exception.AccountNotFoundException;
import com.flowpay.banking.exception.BusinessException;
import com.flowpay.banking.exception.UnauthorizedException;
import com.flowpay.banking.repository.AccountRepository;
import com.flowpay.banking.repository.CardRepository;
import com.flowpay.banking.security.SecurityUtils;
import com.flowpay.banking.util.LuhnAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final AccountRepository accountRepository;
    private final AccountService accountService;

    @Transactional
    public CardResponse createCard(CreateCardRequest request) {
        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new AccountNotFoundException("Conta não encontrada"));

        accountService.validateAccountForTransaction(account);

        if (!SecurityUtils.canAccessResource(account.getUser().getId())) {
            throw new UnauthorizedException("Acesso negado");
        }

        long activeCards = cardRepository.countByAccountAndStatusNot(account, CardStatus.CANCELLED);
        if (activeCards >= 3) {
            throw new BusinessException("Você já possui o máximo de 3 cartões ativos");
        }

        String cardNumber = generateCardNumber();
        String cvv = generateCVV();

        Card card = Card.builder()
                .account(account)
                .cardNumber(cardNumber)
                .cardHolderName(account.getUser().getNomeCompleto().toUpperCase())
                .expiryDate(LocalDate.now().plusYears(5))
                .cvv(cvv)
                .cardType(request.getCardType())
                .status(CardStatus.PENDING_ACTIVATION)
                .creditLimit(request.getCreditLimit() != null ? request.getCreditLimit() : BigDecimal.ZERO)
                .usedLimit(BigDecimal.ZERO)
                .createdAt(LocalDateTime.now())
                .build();

        card = cardRepository.save(card);
        log.info("Cartão criado: {} para conta {}", maskCardNumber(cardNumber), account.getId());

        return mapToCardResponse(card);
    }

    @Transactional(readOnly = true)
    public List<CardResponse> getMyCards() {
        Long userId = SecurityUtils.getCurrentUserId();
        List<Account> accounts = accountRepository.findByUserIdAndDeletedFalse(userId);

        return cardRepository.findByAccountInAndStatusNot(accounts, CardStatus.CANCELLED).stream()
                .map(this::mapToCardResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void activateCard(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new BusinessException("Cartão não encontrado"));

        if (!SecurityUtils.canAccessResource(card.getAccount().getUser().getId())) {
            throw new UnauthorizedException("Acesso negado");
        }

        if (card.getStatus() != CardStatus.PENDING_ACTIVATION) {
            throw new BusinessException("Cartão não pode ser ativado");
        }

        card.setStatus(CardStatus.ACTIVE);
        cardRepository.save(card);
        log.info("Cartão ativado: {}", cardId);
    }

    @Transactional
    public void blockCard(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new BusinessException("Cartão não encontrado"));

        if (!SecurityUtils.canAccessResource(card.getAccount().getUser().getId())) {
            throw new UnauthorizedException("Acesso negado");
        }

        card.setStatus(CardStatus.BLOCKED);
        cardRepository.save(card);
        log.info("Cartão bloqueado: {}", cardId);
    }

    @Transactional
    public void cancelCard(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new BusinessException("Cartão não encontrado"));

        if (!SecurityUtils.canAccessResource(card.getAccount().getUser().getId())) {
            throw new UnauthorizedException("Acesso negado");
        }

        card.setStatus(CardStatus.CANCELLED);
        cardRepository.save(card);
        log.info("Cartão cancelado: {}", cardId);
    }

    @Transactional
    public CardResponse updateLimit(Long cardId, UpdateCardLimitRequest request) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new BusinessException("Cartão não encontrado"));

        if (!SecurityUtils.canAccessResource(card.getAccount().getUser().getId())) {
            throw new UnauthorizedException("Acesso negado");
        }

        if (card.getCardType() != CardType.CREDIT) {
            throw new BusinessException("Apenas cartões de crédito possuem limite");
        }

        card.setCreditLimit(request.getNovoLimite());
        cardRepository.save(card);

        log.info("Limite do cartão atualizado: {}", cardId);
        return mapToCardResponse(card);
    }

    private String generateCardNumber() {
        // Gera número válido pelo algoritmo de Luhn
        StringBuilder sb = new StringBuilder("5437"); // Prefixo Mastercard
        Random random = new Random();

        for (int i = 0; i < 11; i++) {
            sb.append(random.nextInt(10));
        }

        String checkDigit = LuhnAlgorithm.generateCheckDigit(sb.toString());
        return sb.toString() + checkDigit;
    }

    private String generateCVV() {
        Random random = new Random();
        return String.format("%03d", random.nextInt(1000));
    }

    private String maskCardNumber(String cardNumber) {
        if (cardNumber.length() < 16) return cardNumber;
        return "**** **** **** " + cardNumber.substring(12);
    }

    private CardResponse mapToCardResponse(Card card) {
        return CardResponse.builder()
                .id(card.getId())
                .cardNumber(maskCardNumber(card.getCardNumber()))
                .cardholderName(card.getCardHolderName())
                .expiryDate(card.getExpiryDate())
                .cardType(card.getCardType())
                .status(card.getStatus())
                .creditLimit(card.getCreditLimit())
                .availableLimit(card.getAvailableLimit())
                .usedLimit(card.getUsedLimit())
                .isVirtual(false)
                .accountId(card.getAccount().getId())
                .createdAt(card.getCreatedAt())
                .build();
    }
}
