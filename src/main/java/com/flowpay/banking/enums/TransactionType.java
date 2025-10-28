package com.flowpay.banking.enums;

/**
 * Tipos de transação bancária
 */
public enum TransactionType {
    /**
     * Depósito em conta
     */
    DEPOSIT("Depósito"),

    /**
     * Saque da conta
     */
    WITHDRAWAL("Saque"),

    /**
     * Transferência entre contas
     */
    TRANSFER("Transferência"),

    /**
     * Transação PIX
     */
    PIX("PIX"),

    /**
     * TED - Transferência Eletrônica Disponível
     */
    TED("TED"),

    /**
     * DOC - Documento de Ordem de Crédito
     */
    DOC("DOC"),

    /**
     * Pagamento de boleto/conta
     */
    PAYMENT("Pagamento"),

    /**
     * Compra com cartão de débito
     */
    DEBIT_CARD_PURCHASE("Compra Débito"),

    /**
     * Compra com cartão de crédito
     */
    CREDIT_CARD_PURCHASE("Compra Crédito"),

    /**
     * Aplicação em investimento
     */
    INVESTMENT("Investimento"),

    /**
     * Resgate de investimento
     */
    WITHDRAWAL_INVESTMENT("Resgate Investimento"),

    /**
     * Desembolso de empréstimo
     */
    LOAN_DISBURSEMENT("Desembolso Empréstimo"),

    /**
     * Pagamento de parcela de empréstimo
     */
    LOAN_PAYMENT("Pagamento Empréstimo"),

    /**
     * Estorno de transação
     */
    REVERSAL("Estorno"),

    /**
     * Taxa/tarifa bancária
     */
    FEE("Tarifa");

    private final String descricao;

    TransactionType(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
