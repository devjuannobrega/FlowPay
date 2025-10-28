package com.flowpay.banking.enums;

/**
 * Status de verificação KYC (Know Your Customer)
 */
public enum KycStatus {
    /**
     * Documentação pendente de envio
     */
    PENDING("Pendente"),

    /**
     * Documentos em análise
     */
    UNDER_REVIEW("Em Análise"),

    /**
     * Documentos aprovados
     */
    APPROVED("Aprovado"),

    /**
     * Documentos rejeitados
     */
    REJECTED("Rejeitado"),

    /**
     * Requer documentação adicional
     */
    REQUIRES_ADDITIONAL_INFO("Requer Informações Adicionais");

    private final String descricao;

    KycStatus(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
