package com.flowpay.banking.enums;

/**
 * Status do ticket de suporte
 */
public enum SupportTicketStatus {
    /**
     * Ticket aberto/novo
     */
    OPEN("Aberto"),

    /**
     * Ticket em atendimento
     */
    IN_PROGRESS("Em Atendimento"),

    /**
     * Aguardando resposta do cliente
     */
    WAITING_CUSTOMER("Aguardando Cliente"),

    /**
     * Ticket resolvido
     */
    RESOLVED("Resolvido"),

    /**
     * Ticket fechado
     */
    CLOSED("Fechado");

    private final String descricao;

    SupportTicketStatus(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
