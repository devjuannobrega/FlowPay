package com.flowpay.banking.repository;

import com.flowpay.banking.entity.Transaction;
import com.flowpay.banking.enums.TransactionStatus;
import com.flowpay.banking.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository para operações com transações
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByTransactionHash(String transactionHash);

    Optional<Transaction> findByIdempotencyKey(String idempotencyKey);

    List<Transaction> findBySourceAccountId(Long accountId);

    List<Transaction> findByDestinationAccountId(Long accountId);

    List<Transaction> findByStatus(TransactionStatus status);

    List<Transaction> findByType(TransactionType type);

    @Query("SELECT t FROM Transaction t WHERE " +
           "(t.sourceAccount.id = :accountId OR t.destinationAccount.id = :accountId) " +
           "ORDER BY t.createdAt DESC")
    List<Transaction> findByAccountId(@Param("accountId") Long accountId);

    @Query("SELECT t FROM Transaction t WHERE " +
           "(t.sourceAccount.id = :accountId OR t.destinationAccount.id = :accountId) " +
           "AND t.status = :status " +
           "ORDER BY t.createdAt DESC")
    List<Transaction> findByAccountIdAndStatus(@Param("accountId") Long accountId,
                                                @Param("status") TransactionStatus status);

    @Query("SELECT t FROM Transaction t WHERE " +
           "(t.sourceAccount.id = :accountId OR t.destinationAccount.id = :accountId) " +
           "AND t.type = :type " +
           "ORDER BY t.createdAt DESC")
    List<Transaction> findByAccountIdAndType(@Param("accountId") Long accountId,
                                              @Param("type") TransactionType type);

    @Query("SELECT t FROM Transaction t WHERE " +
           "(t.sourceAccount.id = :accountId OR t.destinationAccount.id = :accountId) " +
           "AND t.createdAt BETWEEN :startDate AND :endDate " +
           "ORDER BY t.createdAt DESC")
    List<Transaction> findByAccountIdAndDateRange(@Param("accountId") Long accountId,
                                                   @Param("startDate") LocalDateTime startDate,
                                                   @Param("endDate") LocalDateTime endDate);

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE " +
           "t.sourceAccount.id = :accountId " +
           "AND t.status = 'COMPLETED' " +
           "AND t.createdAt BETWEEN :startDate AND :endDate")
    BigDecimal sumOutgoingByAccountIdAndDateRange(@Param("accountId") Long accountId,
                                                    @Param("startDate") LocalDateTime startDate,
                                                    @Param("endDate") LocalDateTime endDate);

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE " +
           "t.destinationAccount.id = :accountId " +
           "AND t.status = 'COMPLETED' " +
           "AND t.createdAt BETWEEN :startDate AND :endDate")
    BigDecimal sumIncomingByAccountIdAndDateRange(@Param("accountId") Long accountId,
                                                    @Param("startDate") LocalDateTime startDate,
                                                    @Param("endDate") LocalDateTime endDate);

    @Query("SELECT t FROM Transaction t WHERE " +
           "t.status = 'PENDING' " +
           "AND t.scheduledAt IS NOT NULL " +
           "AND t.scheduledAt <= :now")
    List<Transaction> findPendingScheduledTransactions(@Param("now") LocalDateTime now);

    @Query("SELECT COUNT(t) FROM Transaction t WHERE " +
           "t.sourceAccount.id = :accountId " +
           "AND t.status = 'COMPLETED' " +
           "AND t.createdAt >= :startOfDay")
    long countTodayTransactionsByAccountId(@Param("accountId") Long accountId,
                                            @Param("startOfDay") LocalDateTime startOfDay);

    @Query("SELECT t FROM Transaction t WHERE " +
           "t.riskScore >= :minRiskScore " +
           "AND t.status IN ('PENDING', 'PROCESSING') " +
           "ORDER BY t.riskScore DESC")
    List<Transaction> findHighRiskTransactions(@Param("minRiskScore") Integer minRiskScore);

    boolean existsByIdempotencyKey(String idempotencyKey);

    @Query("SELECT COUNT(t) FROM Transaction t WHERE (t.sourceAccount.id = :accountId OR t.destinationAccount.id = :accountId)")
    long countByAccountId(@Param("accountId") Long accountId);

    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.sourceAccount.id = :accountId AND t.createdAt >= :since")
    long countRecentTransactions(@Param("accountId") Long accountId, @Param("since") LocalDateTime since);

    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.status = 'COMPLETED'")
    BigDecimal calculateTotalVolume();

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.status = 'COMPLETED' AND t.createdAt BETWEEN :start AND :end")
    BigDecimal calculateTotalVolumeByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.sourceAccount.id = :accountId AND t.status = 'COMPLETED' AND t.createdAt BETWEEN :start AND :end")
    BigDecimal calculateDailyTotal(@Param("accountId") Long accountId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT t FROM Transaction t WHERE (t.sourceAccount.id = :accountId OR t.destinationAccount.id = :accountId) ORDER BY t.createdAt DESC")
    List<Transaction> findByAccountIdOrderByCreatedAtDesc(@Param("accountId") Long accountId);
}
