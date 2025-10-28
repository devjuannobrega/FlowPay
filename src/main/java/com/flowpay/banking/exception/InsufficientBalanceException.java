package com.flowpay.banking.exception;

import java.math.BigDecimal;

/**
 * Exceção lançada quando o saldo é insuficiente para uma operação
 */
public class InsufficientBalanceException extends BusinessException {

    public InsufficientBalanceException() {
        super("Saldo insuficiente para realizar a operação");
    }

    public InsufficientBalanceException(BigDecimal available, BigDecimal required) {
        super(String.format("Saldo insuficiente. Disponível: R$ %.2f, Necessário: R$ %.2f",
                available, required));
    }

    public InsufficientBalanceException(String message) {
        super(message);
    }
}
