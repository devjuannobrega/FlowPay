package com.flowpay.banking.enums;

/**
 * Status do cartão bancário
 */
public enum CardStatus {
    /**
     * Cartão ativo e utilizável
     */
    ACTIVE("Ativo"),

    /**
     * Cartão bloqueado temporariamente
     */
    BLOCKED("Bloqueado"),

    /**
     * Cartão cancelado permanentemente
     */
    CANCELLED("Cancelado"),

    /**
     * Cartão expirado
     */
    EXPIRED("Expirado"),

    /**
     * Cartão pendente de ativação
     */
    PENDING_ACTIVATION("Pendente Ativação");

    private final String descricao;

    CardStatus(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
