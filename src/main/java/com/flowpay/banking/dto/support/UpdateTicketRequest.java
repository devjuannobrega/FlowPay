package com.flowpay.banking.dto.support;

import com.flowpay.banking.enums.SupportTicketStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para atualização de ticket de suporte
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTicketRequest {

    private SupportTicketStatus status;
    private Long assignedTo;
    private String response;
}
