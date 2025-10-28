package com.flowpay.banking.exception;

/**
 * Exceção lançada quando um usuário não é encontrado
 */
public class UserNotFoundException extends BusinessException {

    public UserNotFoundException() {
        super("Usuário não encontrado");
    }

    public UserNotFoundException(Long userId) {
        super("Usuário não encontrado: " + userId);
    }

    public UserNotFoundException(String identifier) {
        super("Usuário não encontrado: " + identifier);
    }
}
