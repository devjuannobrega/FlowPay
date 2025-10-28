package com.flowpay.banking.repository;

import com.flowpay.banking.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository para operações com logs de auditoria
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findByUserId(Long userId);

    List<AuditLog> findByEntityTypeAndEntityId(String entityType, Long entityId);

    List<AuditLog> findByAction(String action);

    @Query("SELECT a FROM AuditLog a WHERE " +
           "a.userId = :userId " +
           "AND a.createdAt BETWEEN :startDate AND :endDate " +
           "ORDER BY a.createdAt DESC")
    List<AuditLog> findByUserIdAndDateRange(@Param("userId") Long userId,
                                             @Param("startDate") LocalDateTime startDate,
                                             @Param("endDate") LocalDateTime endDate);

    @Query("SELECT a FROM AuditLog a WHERE " +
           "a.entityType = :entityType " +
           "AND a.entityId = :entityId " +
           "ORDER BY a.createdAt DESC")
    List<AuditLog> findAuditTrailForEntity(@Param("entityType") String entityType, @Param("entityId") Long entityId);

    @Query("SELECT a FROM AuditLog a WHERE " +
           "a.action = :action " +
           "AND a.createdAt BETWEEN :startDate AND :endDate " +
           "ORDER BY a.createdAt DESC")
    List<AuditLog> findByActionAndDateRange(@Param("action") String action,
                                             @Param("startDate") LocalDateTime startDate,
                                             @Param("endDate") LocalDateTime endDate);

    @Query("SELECT a FROM AuditLog a WHERE " +
           "a.ipAddress = :ipAddress " +
           "ORDER BY a.createdAt DESC")
    List<AuditLog> findByIpAddress(@Param("ipAddress") String ipAddress);

    @Query("SELECT a FROM AuditLog a WHERE " +
           "a.createdAt BETWEEN :startDate AND :endDate " +
           "ORDER BY a.createdAt DESC")
    List<AuditLog> findByDateRange(@Param("startDate") LocalDateTime startDate,
                                    @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(a) FROM AuditLog a WHERE " +
           "a.userId = :userId " +
           "AND a.action = :action " +
           "AND a.createdAt >= :date")
    long countByUserIdAndActionSince(@Param("userId") Long userId,
                                      @Param("action") String action,
                                      @Param("date") LocalDateTime date);
}
