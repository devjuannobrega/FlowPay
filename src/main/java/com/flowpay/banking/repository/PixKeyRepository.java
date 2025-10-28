package com.flowpay.banking.repository;

import com.flowpay.banking.entity.Account;
import com.flowpay.banking.entity.PixKey;
import com.flowpay.banking.enums.PixKeyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PixKeyRepository extends JpaRepository<PixKey, Long> {

    Optional<PixKey> findByKeyValue(String keyValue);

    Optional<PixKey> findByKeyValueAndActiveTrue(String keyValue);

    Optional<PixKey> findByKeyValueAndActiveTrueAndDeletedFalse(String keyValue);

    List<PixKey> findByAccountId(Long accountId);

    List<PixKey> findByAccountIdAndDeletedFalse(Long accountId);

    List<PixKey> findByAccountIdAndActiveTrue(Long accountId);

    List<PixKey> findByKeyType(PixKeyType keyType);

    boolean existsByKeyValue(String keyValue);

    boolean existsByKeyValueAndActiveTrue(String keyValue);

    @Query("SELECT COUNT(p) FROM PixKey p WHERE p.account.id = :accountId AND p.active = true AND p.deleted = false")
    long countActiveKeysByAccountId(@Param("accountId") Long accountId);

    @Query("SELECT p FROM PixKey p WHERE p.account.user.id = :userId AND p.deleted = false")
    List<PixKey> findByUserId(@Param("userId") Long userId);

    @Query("SELECT p FROM PixKey p WHERE p.keyType = :keyType AND p.active = true AND p.deleted = false")
    List<PixKey> findActiveKeysByType(@Param("keyType") PixKeyType keyType);

    long countByAccountAndKeyTypeAndActiveTrue(Account account, PixKeyType keyType);

    @Query("SELECT p FROM PixKey p WHERE p.account IN :accounts AND p.active = true")
    List<PixKey> findByAccountInAndActiveTrue(@Param("accounts") List<Account> accounts);
}