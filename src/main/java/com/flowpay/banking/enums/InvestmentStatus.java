package com.flowpay.banking.enums;

/**
 * Status do investimento
 */
public enum InvestmentStatus {
    /**
     * Investimento ativo/rendendo
     */
    ACTIVE("Ativo"),

    /**
     * Investimento resgatado
     */
    REDEEMED("Resgatado"),

    /**
     * Investimento cancelado
     */
    CANCELLED("Cancelado"),

    /**
     * Investimento vencido
     */
    MATURED("Vencido");

    private final String descricao;

    InvestmentStatus(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
