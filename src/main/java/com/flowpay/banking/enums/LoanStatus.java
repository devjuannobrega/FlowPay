package com.flowpay.banking.enums;

/**
 * Status do empréstimo
 */
public enum LoanStatus {
    /**
     * Empréstimo em análise
     */
    PENDING("Pendente"),

    /**
     * Empréstimo aprovado
     */
    APPROVED("Aprovado"),

    /**
     * Empréstimo recusado
     */
    REJECTED("Recusado"),

    /**
     * Empréstimo ativo (sendo pago)
     */
    ACTIVE("Ativo"),

    /**
     * Empréstimo quitado
     */
    PAID_OFF("Quitado"),

    /**
     * Empréstimo em atraso
     */
    OVERDUE("Em Atraso"),

    /**
     * Empréstimo inadimplente
     */
    DEFAULTED("Inadimplente"),

    /**
     * Empréstimo cancelado
     */
    CANCELLED("Cancelado");

    private final String descricao;

    LoanStatus(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
