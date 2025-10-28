package com.flowpay.banking.enums;

/**
 * Status de processamento da transação
 */
public enum TransactionStatus {
    /**
     * Transação aguardando processamento
     */
    PENDING("Pendente"),

    /**
     * Transação em processamento
     */
    PROCESSING("Processando"),

    /**
     * Transação concluída com sucesso
     */
    COMPLETED("Concluída"),

    /**
     * Transação falhou
     */
    FAILED("Falhou"),

    /**
     * Transação cancelada
     */
    CANCELLED("Cancelada"),

    /**
     * Transação estornada
     */
    REVERSED("Estornada");

    private final String descricao;

    TransactionStatus(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
