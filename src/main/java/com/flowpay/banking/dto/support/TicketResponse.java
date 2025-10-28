package com.flowpay.banking.dto.support;

import com.flowpay.banking.enums.SupportTicketStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para resposta de ticket de suporte
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketResponse {

    private Long id;
    private String ticketNumber;
    private Long userId;
    private String subject;
    private String description;
    private String category;
    private Integer priority;
    private SupportTicketStatus status;
    private Long assignedTo;
    private String assignedToName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime resolvedAt;
}
