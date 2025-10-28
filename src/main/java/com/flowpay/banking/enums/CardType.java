package com.flowpay.banking.enums;

/**
 * Tipos de cartão bancário
 */
public enum CardType {
    /**
     * Cartão de débito
     */
    DEBIT("Débito"),

    /**
     * Cartão de crédito
     */
    CREDIT("Crédito"),

    /**
     * Cartão virtual para compras online
     */
    VIRTUAL("Virtual");

    private final String descricao;

    CardType(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
