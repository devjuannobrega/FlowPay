package com.flowpay.banking.exception;

/**
 * Exceção lançada quando uma operação não autorizada é tentada
 */
public class UnauthorizedException extends BusinessException {

    public UnauthorizedException() {
        super("Operação não autorizada");
    }

    public UnauthorizedException(String message) {
        super(message);
    }
}
