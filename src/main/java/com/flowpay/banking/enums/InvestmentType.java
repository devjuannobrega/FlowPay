package com.flowpay.banking.enums;

/**
 * Tipos de investimento
 */
public enum InvestmentType {
    /**
     * Poupança
     */
    SAVINGS("Poupança"),

    /**
     * CDB - Certificado de Depósito Bancário
     */
    CDB("CDB"),

    /**
     * Tesouro Direto
     */
    TESOURO_DIRETO("Tesouro Direto"),

    /**
     * Fundo de investimento
     */
    INVESTMENT_FUND("Fundo de Investimento");

    private final String descricao;

    InvestmentType(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
