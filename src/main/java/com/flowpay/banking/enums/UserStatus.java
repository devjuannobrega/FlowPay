package com.flowpay.banking.enums;

/**
 * Status do usuário no sistema
 */
public enum UserStatus {
    /**
     * Usuário ativo e operacional
     */
    ATIVO,

    /**
     * Cadastro pendente de verificação/documentação
     */
    PENDENTE,

    /**
     * Usuário temporariamente bloqueado
     */
    BLOQUEADO,

    /**
     * Usuário suspenso por questões de compliance
     */
    SUSPENSO,

    /**
     * Conta inativa/fechada
     */
    INATIVO
}
