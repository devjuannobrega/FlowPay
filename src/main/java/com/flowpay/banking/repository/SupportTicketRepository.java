package com.flowpay.banking.repository;

import com.flowpay.banking.entity.SupportTicket;
import com.flowpay.banking.enums.SupportTicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository para operações com tickets de suporte
 */
@Repository
public interface SupportTicketRepository extends JpaRepository<SupportTicket, Long> {

    Optional<SupportTicket> findByTicketNumber(String ticketNumber);

    Optional<SupportTicket> findByTicketNumberAndDeletedFalse(String ticketNumber);

    List<SupportTicket> findByUserId(Long userId);

    List<SupportTicket> findByUserIdAndDeletedFalse(Long userId);

    List<SupportTicket> findByStatus(SupportTicketStatus status);

    List<SupportTicket> findByCategory(String category);

    List<SupportTicket> findByAssignedTo(Long assignedTo);

    @Query("SELECT t FROM SupportTicket t WHERE t.status = :status AND t.deleted = false ORDER BY t.priority DESC, t.createdAt ASC")
    List<SupportTicket> findByStatusOrderedByPriority(@Param("status") SupportTicketStatus status);

    @Query("SELECT t FROM SupportTicket t WHERE " +
           "t.user.id = :userId " +
           "AND t.status = :status " +
           "AND t.deleted = false " +
           "ORDER BY t.createdAt DESC")
    List<SupportTicket> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") SupportTicketStatus status);

    @Query("SELECT t FROM SupportTicket t WHERE " +
           "t.status IN ('OPEN', 'IN_PROGRESS') " +
           "AND t.assignedTo IS NULL " +
           "AND t.deleted = false " +
           "ORDER BY t.priority DESC, t.createdAt ASC")
    List<SupportTicket> findUnassignedOpenTickets();

    @Query("SELECT t FROM SupportTicket t WHERE " +
           "t.assignedTo = :agentId " +
           "AND t.status IN ('OPEN', 'IN_PROGRESS', 'WAITING_CUSTOMER') " +
           "AND t.deleted = false " +
           "ORDER BY t.priority DESC")
    List<SupportTicket> findActiveTicketsByAgent(@Param("agentId") Long agentId);

    @Query("SELECT t FROM SupportTicket t WHERE " +
           "t.createdAt BETWEEN :startDate AND :endDate " +
           "AND t.deleted = false " +
           "ORDER BY t.createdAt DESC")
    List<SupportTicket> findByDateRange(@Param("startDate") LocalDateTime startDate,
                                         @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(t) FROM SupportTicket t WHERE " +
           "t.user.id = :userId " +
           "AND t.status IN ('OPEN', 'IN_PROGRESS', 'WAITING_CUSTOMER') " +
           "AND t.deleted = false")
    long countActiveTicketsByUserId(@Param("userId") Long userId);

    @Query(value = "SELECT AVG(EXTRACT(EPOCH FROM (resolved_at - created_at))) / 3600 FROM support_tickets " +
           "WHERE status = 'RESOLVED' " +
           "AND resolved_at IS NOT NULL " +
           "AND created_at BETWEEN :startDate AND :endDate",
           nativeQuery = true)
    Double getAverageResolutionTimeInHours(@Param("startDate") LocalDateTime startDate,
                                            @Param("endDate") LocalDateTime endDate);

    long countByStatus(SupportTicketStatus status);
}
