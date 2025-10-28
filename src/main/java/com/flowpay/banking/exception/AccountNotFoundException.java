package com.flowpay.banking.exception;

/**
 * Exceção lançada quando uma conta não é encontrada
 */
public class AccountNotFoundException extends BusinessException {

    public AccountNotFoundException() {
        super("Conta não encontrada");
    }

    public AccountNotFoundException(Long accountId) {
        super("Conta não encontrada: " + accountId);
    }

    public AccountNotFoundException(String accountNumber, String agencia) {
        super(String.format("Conta não encontrada: Agência %s, Conta %s", agencia, accountNumber));
    }

    public AccountNotFoundException(String message) {
        super(message);
    }
}
