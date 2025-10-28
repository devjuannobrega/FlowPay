package com.flowpay.banking.exception;

/**
 * Exceção lançada quando uma transação duplicada é detectada (idempotência)
 */
public class DuplicateTransactionException extends BusinessException {

    public DuplicateTransactionException() {
        super("Transação duplicada detectada");
    }

    public DuplicateTransactionException(String idempotencyKey) {
        super("Transação duplicada detectada com a chave: " + idempotencyKey);
    }
}
