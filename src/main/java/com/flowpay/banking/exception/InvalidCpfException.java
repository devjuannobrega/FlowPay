package com.flowpay.banking.exception;

/**
 * Exceção lançada quando um CPF é inválido
 */
public class InvalidCpfException extends BusinessException {

    public InvalidCpfException() {
        super("CPF inválido");
    }

    public InvalidCpfException(String cpf) {
        super("CPF inválido: " + cpf);
    }
}
