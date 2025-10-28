package com.flowpay.banking.repository;



import com.flowpay.banking.entity.Account;
import com.flowpay.banking.entity.Loan;
import com.flowpay.banking.enums.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Repository para operações com empréstimos
 */
@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    List<Loan> findByAccountId(Long accountId);

    List<Loan> findByAccountIdAndDeletedFalse(Long accountId);

    List<Loan> findByStatus(LoanStatus status);

    @Query("SELECT l FROM Loan l WHERE l.account.id = :accountId AND l.status = :status AND l.deleted = false")
    List<Loan> findByAccountIdAndStatus(@Param("accountId") Long accountId, @Param("status") LoanStatus status);

    @Query("SELECT l FROM Loan l WHERE l.status = 'PENDING' AND l.deleted = false")
    List<Loan> findPendingLoans();

    @Query("SELECT l FROM Loan l WHERE l.status IN ('ACTIVE', 'OVERDUE') AND l.deleted = false")
    List<Loan> findActiveLoans();

    @Query("SELECT l FROM Loan l WHERE l.account.user.id = :userId AND l.deleted = false")
    List<Loan> findByUserId(@Param("userId") Long userId);

    @Query("SELECT SUM(l.approvedAmount) FROM Loan l WHERE " +
           "l.account.id = :accountId " +
           "AND l.status IN ('ACTIVE', 'OVERDUE') " +
           "AND l.deleted = false")
    BigDecimal getTotalActiveLoansByAccountId(@Param("accountId") Long accountId);

    @Query("SELECT SUM(l.paidAmount) FROM Loan l WHERE " +
           "l.account.id = :accountId " +
           "AND l.deleted = false")
    BigDecimal getTotalPaidByAccountId(@Param("accountId") Long accountId);

    @Query("SELECT COUNT(l) FROM Loan l WHERE " +
           "l.account.id = :accountId " +
           "AND l.status = 'ACTIVE' " +
           "AND l.deleted = false")
    long countActiveLoansByAccountId(@Param("accountId") Long accountId);

    @Query("SELECT l FROM Loan l WHERE " +
           "l.creditScore IS NOT NULL " +
           "AND l.creditScore < :minScore " +
           "AND l.status = 'PENDING' " +
           "AND l.deleted = false")
    List<Loan> findLoansWithLowCreditScore(@Param("minScore") Integer minScore);

    List<Loan> findByAccountIn(List<Account> accounts);

    long countByAccountAndStatus(Account account, LoanStatus status);
}
