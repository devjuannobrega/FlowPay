package com.flowpay.banking.exception;

/**
 * Exceção lançada quando uma operação é tentada em uma conta bloqueada
 */
public class AccountBlockedException extends BusinessException {

    public AccountBlockedException() {
        super("Conta bloqueada. Entre em contato com o suporte");
    }

    public AccountBlockedException(String message) {
        super(message);
    }
}
