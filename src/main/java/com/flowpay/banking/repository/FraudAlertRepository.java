package com.flowpay.banking.repository;

import com.flowpay.banking.entity.FraudAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository para operações com alertas de fraude
 */
@Repository
public interface FraudAlertRepository extends JpaRepository<FraudAlert, Long> {

    List<FraudAlert> findByUserId(Long userId);

    List<FraudAlert> findByAccountId(Long accountId);

    List<FraudAlert> findByTransactionId(Long transactionId);

    List<FraudAlert> findByStatus(String status);

    List<FraudAlert> findByAlertType(String alertType);

    @Query("SELECT f FROM FraudAlert f WHERE f.status = 'OPEN' ORDER BY f.riskScore DESC")
    List<FraudAlert> findOpenAlerts();

    @Query("SELECT f FROM FraudAlert f WHERE f.riskScore >= :minScore ORDER BY f.riskScore DESC")
    List<FraudAlert> findHighRiskAlerts(@Param("minScore") Integer minScore);

    @Query("SELECT f FROM FraudAlert f WHERE " +
           "f.user.id = :userId " +
           "AND f.status = :status " +
           "ORDER BY f.createdAt DESC")
    List<FraudAlert> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") String status);

    @Query("SELECT f FROM FraudAlert f WHERE " +
           "f.account.id = :accountId " +
           "AND f.createdAt BETWEEN :startDate AND :endDate " +
           "ORDER BY f.createdAt DESC")
    List<FraudAlert> findByAccountIdAndDateRange(@Param("accountId") Long accountId,
                                                  @Param("startDate") LocalDateTime startDate,
                                                  @Param("endDate") LocalDateTime endDate);

    @Query("SELECT f FROM FraudAlert f WHERE " +
           "f.status = 'OPEN' " +
           "AND f.reviewedBy IS NULL " +
           "ORDER BY f.riskScore DESC, f.createdAt ASC")
    List<FraudAlert> findUnreviewedAlerts();

    @Query("SELECT COUNT(f) FROM FraudAlert f WHERE " +
           "f.user.id = :userId " +
           "AND f.status = 'CONFIRMED'")
    long countConfirmedFraudsByUserId(@Param("userId") Long userId);

    @Query("SELECT f FROM FraudAlert f WHERE " +
           "f.ipAddress = :ipAddress " +
           "ORDER BY f.createdAt DESC")
    List<FraudAlert> findByIpAddress(@Param("ipAddress") String ipAddress);

    @Query("SELECT COUNT(f) FROM FraudAlert f WHERE f.status != 'RESOLVED'")
    long countByStatusNotResolved();
}
