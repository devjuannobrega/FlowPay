package com.flowpay.banking.enums;

/**
 * Tipos de notificação
 */
public enum NotificationType {
    /**
     * Notificação por email
     */
    EMAIL("E-mail"),

    /**
     * Notificação por SMS
     */
    SMS("SMS"),

    /**
     * Notificação push (mobile)
     */
    PUSH("Push"),

    /**
     * Notificação interna do app
     */
    IN_APP("In-App");

    private final String descricao;

    NotificationType(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
