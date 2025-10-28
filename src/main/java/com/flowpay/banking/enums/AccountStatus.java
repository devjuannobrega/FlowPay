package com.flowpay.banking.enums;

/**
 * Status da conta bancária
 */
public enum AccountStatus {
    /**
     * Conta ativa e operacional
     */
    ACTIVE("Ativa"),

    /**
     * Conta bloqueada temporariamente
     */
    BLOCKED("Bloqueada"),

    /**
     * Conta encerrada permanentemente
     */
    CLOSED("Encerrada"),

    /**
     * Conta em análise
     */
    UNDER_REVIEW("Em Análise");

    private final String descricao;

    AccountStatus(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
