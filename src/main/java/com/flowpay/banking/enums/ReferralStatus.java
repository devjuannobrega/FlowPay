package com.flowpay.banking.enums;

/**
 * Status do programa de indicação
 */
public enum ReferralStatus {
    /**
     * Indicação pendente (usuário não completou cadastro)
     */
    PENDING("Pendente"),

    /**
     * Indicação completa (usuário cadastrou e ativou conta)
     */
    COMPLETED("Completa"),

    /**
     * Bônus pago
     */
    PAID("Pago"),

    /**
     * Indicação expirada
     */
    EXPIRED("Expirada");

    private final String descricao;

    ReferralStatus(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
