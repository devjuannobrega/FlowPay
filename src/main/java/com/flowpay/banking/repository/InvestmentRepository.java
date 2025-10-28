package com.flowpay.banking.repository;

import com.flowpay.banking.entity.Investment;
import com.flowpay.banking.enums.InvestmentStatus;
import com.flowpay.banking.enums.InvestmentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Repository para operações com investimentos
 */
@Repository
public interface InvestmentRepository extends JpaRepository<Investment, Long> {

    List<Investment> findByAccountId(Long accountId);

    List<Investment> findByAccountIdAndDeletedFalse(Long accountId);

    List<Investment> findByInvestmentType(InvestmentType investmentType);

    List<Investment> findByStatus(InvestmentStatus status);

    @Query("SELECT i FROM Investment i WHERE i.account.id = :accountId AND i.status = :status AND i.deleted = false")
    List<Investment> findByAccountIdAndStatus(@Param("accountId") Long accountId, @Param("status") InvestmentStatus status);

    @Query("SELECT i FROM Investment i WHERE " +
           "i.status = 'ACTIVE' " +
           "AND i.maturityDate <= :date " +
           "AND i.deleted = false")
    List<Investment> findMaturedInvestments(@Param("date") LocalDate date);

    @Query("SELECT i FROM Investment i WHERE " +
           "i.status = 'ACTIVE' " +
           "AND i.investmentType = 'SAVINGS' " +
           "AND i.anniversaryDay = :day " +
           "AND i.deleted = false")
    List<Investment> findSavingsInvestmentsByAnniversaryDay(@Param("day") Integer day);

    @Query("SELECT i FROM Investment i WHERE " +
           "i.status = 'ACTIVE' " +
           "AND (i.lastYieldCalculation IS NULL OR i.lastYieldCalculation < :date) " +
           "AND i.deleted = false")
    List<Investment> findInvestmentsNeedingYieldCalculation(@Param("date") LocalDate date);

    @Query("SELECT SUM(i.currentAmount) FROM Investment i WHERE " +
           "i.account.id = :accountId " +
           "AND i.status = 'ACTIVE' " +
           "AND i.deleted = false")
    BigDecimal getTotalInvestedByAccountId(@Param("accountId") Long accountId);

    @Query("SELECT SUM(i.totalYield) FROM Investment i WHERE " +
           "i.account.id = :accountId " +
           "AND i.deleted = false")
    BigDecimal getTotalYieldByAccountId(@Param("accountId") Long accountId);

    @Query("SELECT i FROM Investment i WHERE i.account.user.id = :userId AND i.deleted = false")
    List<Investment> findByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(i) FROM Investment i WHERE i.account.id = :accountId AND i.status = 'ACTIVE' AND i.deleted = false")
    long countActiveInvestmentsByAccountId(@Param("accountId") Long accountId);
}
