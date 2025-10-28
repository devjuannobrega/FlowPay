package com.flowpay.banking.exception;

/**
 * Exceção lançada quando uma chave PIX já está cadastrada
 */
public class DuplicatePixKeyException extends BusinessException {

    public DuplicatePixKeyException(String keyValue) {
        super("Chave PIX já cadastrada: " + keyValue);
    }

    public DuplicatePixKeyException(String message, Throwable cause) {
        super(message, cause);
    }
}
