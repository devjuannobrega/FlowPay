package com.flowpay.banking.repository;

import com.flowpay.banking.entity.Loan;
import com.flowpay.banking.entity.LoanInstallment;
import com.flowpay.banking.enums.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository para operações com parcelas de empréstimos
 */
@Repository
public interface LoanInstallmentRepository extends JpaRepository<LoanInstallment, Long> {

    List<LoanInstallment> findByLoanId(Long loanId);

    List<LoanInstallment> findByStatus(TransactionStatus status);

    @Query("SELECT li FROM LoanInstallment li WHERE li.loan.id = :loanId AND li.status = :status")
    List<LoanInstallment> findByLoanIdAndStatus(@Param("loanId") Long loanId, @Param("status") TransactionStatus status);

    @Query("SELECT li FROM LoanInstallment li WHERE " +
           "li.dueDate = :date " +
           "AND li.status = 'PENDING'")
    List<LoanInstallment> findInstallmentsDueToday(@Param("date") LocalDate date);

    @Query("SELECT li FROM LoanInstallment li WHERE " +
           "li.dueDate < :date " +
           "AND li.status = 'PENDING'")
    List<LoanInstallment> findOverdueInstallments(@Param("date") LocalDate date);

    @Query("SELECT li FROM LoanInstallment li WHERE " +
           "li.loan.account.id = :accountId " +
           "AND li.status = 'PENDING' " +
           "ORDER BY li.dueDate ASC")
    List<LoanInstallment> findPendingInstallmentsByAccountId(@Param("accountId") Long accountId);

    @Query("SELECT li FROM LoanInstallment li WHERE " +
           "li.loan.id = :loanId " +
           "AND li.installmentNumber = :installmentNumber")
    LoanInstallment findByLoanIdAndInstallmentNumber(@Param("loanId") Long loanId,
                                                      @Param("installmentNumber") Integer installmentNumber);

    @Query("SELECT COUNT(li) FROM LoanInstallment li WHERE " +
           "li.loan.id = :loanId " +
           "AND li.status = 'COMPLETED'")
    long countPaidInstallmentsByLoanId(@Param("loanId") Long loanId);

    @Query("SELECT li FROM LoanInstallment li WHERE " +
           "li.dueDate BETWEEN :startDate AND :endDate " +
           "AND li.status = 'PENDING' " +
           "ORDER BY li.dueDate ASC")
    List<LoanInstallment> findInstallmentsDueBetween(@Param("startDate") LocalDate startDate,
                                                      @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(li) FROM LoanInstallment li WHERE li.loan = :loan AND li.status != 'COMPLETED'")
    long countUnpaidInstallmentsByLoan(@Param("loan") Loan loan);

    List<LoanInstallment> findByLoanOrderByInstallmentNumber(Loan loan);
}
