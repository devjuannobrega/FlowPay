package com.flowpay.banking.enums;

/**
 * Tipos de chave PIX
 */
public enum PixKeyType {
    /**
     * CPF/CNPJ como chave PIX
     */
    CPF("CPF/CNPJ"),

    /**
     * Email como chave PIX
     */
    EMAIL("E-mail"),

    /**
     * Telefone como chave PIX
     */
    PHONE("Telefone"),

    /**
     * Chave aleatória (UUID)
     */
    RANDOM("Chave Aleatória");

    private final String descricao;

    PixKeyType(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
