package com.flowpay.banking.exception;

/**
 * Exceção lançada quando uma transação é inválida
 */
public class InvalidTransactionException extends BusinessException {

    public InvalidTransactionException(String message) {
        super(message);
    }

    public InvalidTransactionException(String message, Throwable cause) {
        super(message, cause);
    }
}
