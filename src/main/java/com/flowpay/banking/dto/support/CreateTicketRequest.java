package com.flowpay.banking.dto.support;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para criação de ticket de suporte
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTicketRequest {

    @NotBlank(message = "Assunto é obrigatório")
    private String subject;

    @NotBlank(message = "Descrição é obrigatória")
    private String description;

    @NotBlank(message = "Categoria é obrigatória")
    private String category;

    @NotNull(message = "Prioridade é obrigatória")
    private Integer priority; // 1 (baixa) a 5 (crítica)
}
