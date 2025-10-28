package com.flowpay.banking.repository;

import com.flowpay.banking.entity.Payment;
import com.flowpay.banking.enums.PaymentType;
import com.flowpay.banking.enums.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository para operações com pagamentos
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByAccountId(Long accountId);

    List<Payment> findByAccountIdAndDeletedFalse(Long accountId);

    List<Payment> findByPaymentType(PaymentType paymentType);

    List<Payment> findByStatus(TransactionStatus status);

    Optional<Payment> findByBarcode(String barcode);

    @Query("SELECT p FROM Payment p WHERE p.account.id = :accountId AND p.status = :status AND p.deleted = false")
    List<Payment> findByAccountIdAndStatus(@Param("accountId") Long accountId, @Param("status") TransactionStatus status);

    @Query("SELECT p FROM Payment p WHERE p.dueDate = :date AND p.status = 'PENDING' AND p.deleted = false")
    List<Payment> findPaymentsDueToday(@Param("date") LocalDate date);

    @Query("SELECT p FROM Payment p WHERE p.dueDate < :date AND p.status = 'PENDING' AND p.deleted = false")
    List<Payment> findOverduePayments(@Param("date") LocalDate date);

    @Query("SELECT p FROM Payment p WHERE " +
           "p.scheduledDate = :date " +
           "AND p.status = 'PENDING' " +
           "AND p.deleted = false")
    List<Payment> findScheduledPaymentsForDate(@Param("date") LocalDate date);

    @Query("SELECT p FROM Payment p WHERE p.recurring = true AND p.status = 'PENDING' AND p.deleted = false")
    List<Payment> findRecurringPayments();

    @Query("SELECT p FROM Payment p WHERE " +
           "p.account.id = :accountId " +
           "AND p.paymentDate BETWEEN :startDate AND :endDate " +
           "AND p.deleted = false")
    List<Payment> findByAccountIdAndPaymentDateBetween(@Param("accountId") Long accountId,
                                                         @Param("startDate") LocalDate startDate,
                                                         @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(p) FROM Payment p WHERE p.account.id = :accountId AND p.status = 'COMPLETED' AND p.deleted = false")
    long countCompletedPaymentsByAccountId(@Param("accountId") Long accountId);
}
