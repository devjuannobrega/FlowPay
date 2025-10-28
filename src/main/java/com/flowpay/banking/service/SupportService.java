package com.flowpay.banking.service;

import com.flowpay.banking.dto.support.*;
import com.flowpay.banking.entity.SupportTicket;
import com.flowpay.banking.entity.User;
import com.flowpay.banking.enums.SupportTicketStatus;
import com.flowpay.banking.exception.UnauthorizedException;
import com.flowpay.banking.exception.UserNotFoundException;
import com.flowpay.banking.repository.SupportTicketRepository;
import com.flowpay.banking.repository.UserRepository;
import com.flowpay.banking.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SupportService {

    private final SupportTicketRepository ticketRepository;
    private final UserRepository userRepository;

    @Transactional
    public TicketResponse createTicket(CreateTicketRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));

        SupportTicket ticket = SupportTicket.builder()
                .ticketNumber("TICK" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .user(user)
                .subject(request.getSubject())
                .description(request.getDescription())
                .category(request.getCategory())
                .priority(convertPriorityToString(request.getPriority()))
                .status(SupportTicketStatus.OPEN)
                .createdAt(LocalDateTime.now())
                .deleted(false)
                .build();

        ticket = ticketRepository.save(ticket);
        log.info("Ticket de suporte criado: {}", ticket.getTicketNumber());

        return mapToResponse(ticket);
    }

    @Transactional(readOnly = true)
    public List<TicketResponse> getMyTickets() {
        Long userId = SecurityUtils.getCurrentUserId();
        return ticketRepository.findByUserIdAndDeletedFalse(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public TicketResponse updateTicket(Long ticketId, UpdateTicketRequest request) {
        SupportTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket não encontrado"));

        // Usuário pode ver seu próprio ticket, admin pode ver todos
        if (!SecurityUtils.getCurrentUserId().equals(ticket.getUser().getId()) && !SecurityUtils.isAdmin()) {
            throw new UnauthorizedException("Acesso negado");
        }

        if (request.getStatus() != null) {
            ticket.setStatus(request.getStatus());
            if (request.getStatus() == SupportTicketStatus.RESOLVED) {
                ticket.setResolvedAt(LocalDateTime.now());
            }
        }

        if (request.getAssignedTo() != null) {
            User assignedUser = userRepository.findById(request.getAssignedTo())
                    .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));
            ticket.setAssignedTo(assignedUser.getId());
        }

        ticket.setUpdatedAt(LocalDateTime.now());
        ticket = ticketRepository.save(ticket);

        return mapToResponse(ticket);
    }

    private TicketResponse mapToResponse(SupportTicket ticket) {
        String assignedToName = null;
        if (ticket.getAssignedTo() != null) {
            assignedToName = userRepository.findById(ticket.getAssignedTo())
                    .map(User::getNomeCompleto)
                    .orElse(null);
        }

        return TicketResponse.builder()
                .id(ticket.getId())
                .ticketNumber(ticket.getTicketNumber())
                .userId(ticket.getUser().getId())
                .subject(ticket.getSubject())
                .description(ticket.getDescription())
                .category(ticket.getCategory())
                .priority(convertPriorityToInteger(ticket.getPriority()))
                .status(ticket.getStatus())
                .assignedTo(ticket.getAssignedTo())
                .assignedToName(assignedToName)
                .createdAt(ticket.getCreatedAt())
                .updatedAt(ticket.getUpdatedAt())
                .resolvedAt(ticket.getResolvedAt())
                .build();
    }

    private String convertPriorityToString(Integer priority) {
        if (priority == null) return "MEDIUM";
        switch (priority) {
            case 1: return "LOW";
            case 2: return "MEDIUM";
            case 3: return "HIGH";
            case 4:
            case 5: return "CRITICAL";
            default: return "MEDIUM";
        }
    }

    private Integer convertPriorityToInteger(String priority) {
        if (priority == null) return 2;
        switch (priority) {
            case "LOW": return 1;
            case "MEDIUM": return 2;
            case "HIGH": return 3;
            case "CRITICAL": return 5;
            default: return 2;
        }
    }
}
