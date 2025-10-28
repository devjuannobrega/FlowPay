package com.flowpay.banking.repository;

import com.flowpay.banking.entity.Account;
import com.flowpay.banking.enums.AccountStatus;
import com.flowpay.banking.enums.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repository para operações com contas bancárias
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByAccountNumberAndAgencia(String accountNumber, String agencia);

    Optional<Account> findByAccountNumberAndAgenciaAndDeletedFalse(String accountNumber, String agencia);

    List<Account> findByUserId(Long userId);

    List<Account> findByUserIdAndDeletedFalse(Long userId);

    List<Account> findByType(AccountType type);

    List<Account> findByStatus(AccountStatus status);

    List<Account> findByUserIdAndStatus(Long userId, AccountStatus status);

    boolean existsByAccountNumberAndAgencia(String accountNumber, String agencia);

    @Query("SELECT a FROM Account a WHERE a.user.id = :userId AND a.type = :type AND a.deleted = false")
    List<Account> findByUserIdAndType(@Param("userId") Long userId, @Param("type") AccountType type);

    @Query("SELECT a FROM Account a WHERE a.status = 'ACTIVE' AND a.deleted = false")
    List<Account> findAllActiveAccounts();

    @Query("SELECT COUNT(a) FROM Account a WHERE a.user.id = :userId AND a.deleted = false")
    long countByUserId(@Param("userId") Long userId);

    @Query("SELECT SUM(a.saldo) FROM Account a WHERE a.user.id = :userId AND a.deleted = false")
    BigDecimal getTotalBalanceByUserId(@Param("userId") Long userId);

    @Query("SELECT a FROM Account a WHERE a.saldo > :minBalance AND a.status = 'ACTIVE' AND a.deleted = false")
    List<Account> findAccountsWithBalanceGreaterThan(@Param("minBalance") BigDecimal minBalance);

    @Query("SELECT a FROM Account a WHERE a.accountNumber = :accountNumber AND a.status = 'ACTIVE' AND a.deleted = false")
    Optional<Account> findActiveAccountByNumber(@Param("accountNumber") String accountNumber);

    Optional<Account> findByIdAndDeletedFalse(Long id);

    boolean existsByUserIdAndTypeAndDeletedFalse(Long userId, AccountType type);

    Optional<Account> findByAgenciaAndAccountNumberAndDigitoVerificadorAndDeletedFalse(String agencia, String accountNumber, String digitoVerificador);
}
