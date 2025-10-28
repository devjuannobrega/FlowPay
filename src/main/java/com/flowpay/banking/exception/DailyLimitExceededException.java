package com.flowpay.banking.exception;

import java.math.BigDecimal;

/**
 * Exceção lançada quando o limite diário é excedido
 */
public class DailyLimitExceededException extends BusinessException {

    public DailyLimitExceededException() {
        super("Limite diário excedido");
    }

    public DailyLimitExceededException(BigDecimal limit, BigDecimal used, BigDecimal attempted) {
        super(String.format("Limite diário excedido. Limite: R$ %.2f, Utilizado: R$ %.2f, Tentativa: R$ %.2f",
                limit, used, attempted));
    }

    public DailyLimitExceededException(String message) {
        super(message);
    }
}
