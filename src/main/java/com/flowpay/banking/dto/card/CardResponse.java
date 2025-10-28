package com.flowpay.banking.dto.card;

import com.flowpay.banking.enums.CardStatus;
import com.flowpay.banking.enums.CardType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO para resposta de cartão
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardResponse {

    private Long id;
    private String cardNumber; // Parcialmente mascarado
    private String cardholderName;
    private LocalDate expiryDate;
    private CardType cardType;
    private CardStatus status;
    private BigDecimal creditLimit;
    private BigDecimal availableLimit;
    private BigDecimal usedLimit;
    private boolean isVirtual;
    private Long accountId;
    private LocalDateTime createdAt;
}
