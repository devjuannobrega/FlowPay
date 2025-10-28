package com.flowpay.banking.enums;

/**
 * Tipos de conta bancária
 */
public enum AccountType {
    /**
     * Conta corrente - permite transações ilimitadas
     */
    CHECKING("Conta Corrente"),

    /**
     * Conta poupança - com rendimento
     */
    SAVINGS("Conta Poupança");

    private final String descricao;

    AccountType(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
