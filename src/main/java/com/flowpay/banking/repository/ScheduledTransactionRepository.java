package com.flowpay.banking.repository;

import com.flowpay.banking.entity.ScheduledTransaction;
import com.flowpay.banking.enums.TransactionStatus;
import com.flowpay.banking.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository para operações com transações agendadas
 */
@Repository
public interface ScheduledTransactionRepository extends JpaRepository<ScheduledTransaction, Long> {

    List<ScheduledTransaction> findBySourceAccountId(Long accountId);

    List<ScheduledTransaction> findBySourceAccountIdAndDeletedFalse(Long accountId);

    List<ScheduledTransaction> findByStatus(TransactionStatus status);

    List<ScheduledTransaction> findByTransactionType(TransactionType transactionType);

    @Query("SELECT st FROM ScheduledTransaction st WHERE " +
           "st.scheduledDate = :date " +
           "AND st.status = 'PENDING' " +
           "AND st.deleted = false")
    List<ScheduledTransaction> findTransactionsScheduledForDate(@Param("date") LocalDate date);

    @Query("SELECT st FROM ScheduledTransaction st WHERE " +
           "st.scheduledDate <= :date " +
           "AND st.status = 'PENDING' " +
           "AND st.deleted = false")
    List<ScheduledTransaction> findPendingTransactionsUntilDate(@Param("date") LocalDate date);

    @Query("SELECT st FROM ScheduledTransaction st WHERE " +
           "st.recurrenceType IS NOT NULL " +
           "AND st.status = 'PENDING' " +
           "AND (st.recurrenceEndDate IS NULL OR st.recurrenceEndDate >= :date) " +
           "AND st.deleted = false")
    List<ScheduledTransaction> findRecurringTransactions(@Param("date") LocalDate date);

    @Query("SELECT st FROM ScheduledTransaction st WHERE " +
           "st.sourceAccount.id = :accountId " +
           "AND st.status = :status " +
           "AND st.deleted = false " +
           "ORDER BY st.scheduledDate ASC")
    List<ScheduledTransaction> findByAccountIdAndStatus(@Param("accountId") Long accountId,
                                                         @Param("status") TransactionStatus status);

    @Query("SELECT COUNT(st) FROM ScheduledTransaction st WHERE " +
           "st.sourceAccount.id = :accountId " +
           "AND st.status = 'PENDING' " +
           "AND st.deleted = false")
    long countPendingByAccountId(@Param("accountId") Long accountId);
}
