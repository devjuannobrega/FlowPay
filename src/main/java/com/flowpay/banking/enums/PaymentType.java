package com.flowpay.banking.enums;

/**
 * Tipos de pagamento
 */
public enum PaymentType {
    /**
     * Pagamento de boleto bancário
     */
    BOLETO("Boleto"),

    /**
     * Pagamento de conta de água
     */
    WATER_BILL("Conta de Água"),

    /**
     * Pagamento de conta de luz
     */
    ELECTRICITY_BILL("Conta de Luz"),

    /**
     * Pagamento de conta de telefone
     */
    PHONE_BILL("Conta de Telefone"),

    /**
     * Pagamento de conta de internet
     */
    INTERNET_BILL("Conta de Internet"),

    /**
     * Recarga de celular
     */
    MOBILE_RECHARGE("Recarga de Celular"),

    /**
     * Pagamento de tributos/impostos
     */
    TAX("Tributo/Imposto"),

    /**
     * Outros pagamentos
     */
    OTHER("Outros");

    private final String descricao;

    PaymentType(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
