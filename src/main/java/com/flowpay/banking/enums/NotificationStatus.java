package com.flowpay.banking.enums;

/**
 * Status da notificação
 */
public enum NotificationStatus {
    /**
     * Notificação não lida
     */
    UNREAD("Não Lida"),

    /**
     * Notificação lida
     */
    READ("Lida"),

    /**
     * Notificação arquivada
     */
    ARCHIVED("Arquivada");

    private final String descricao;

    NotificationStatus(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
