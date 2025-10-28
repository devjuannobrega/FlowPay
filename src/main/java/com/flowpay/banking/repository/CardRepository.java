package com.flowpay.banking.repository;

import com.flowpay.banking.entity.Account;
import com.flowpay.banking.entity.Card;
import com.flowpay.banking.enums.CardStatus;
import com.flowpay.banking.enums.CardType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository para operações com cartões
 */
@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    Optional<Card> findByCardNumber(String cardNumber);

    Optional<Card> findByCardNumberAndDeletedFalse(String cardNumber);

    List<Card> findByAccountId(Long accountId);

    List<Card> findByAccountIdAndDeletedFalse(Long accountId);

    List<Card> findByAccountIdAndStatus(Long accountId, CardStatus status);

    List<Card> findByCardType(CardType cardType);

    List<Card> findByStatus(CardStatus status);

    boolean existsByCardNumber(String cardNumber);

    @Query("SELECT c FROM Card c WHERE c.account.id = :accountId AND c.cardType = :cardType AND c.deleted = false")
    List<Card> findByAccountIdAndCardType(@Param("accountId") Long accountId, @Param("cardType") CardType cardType);

    @Query("SELECT c FROM Card c WHERE c.status = 'ACTIVE' AND c.expiryDate < :date AND c.deleted = false")
    List<Card> findExpiredCards(@Param("date") LocalDate date);

    @Query("SELECT c FROM Card c WHERE c.status = 'ACTIVE' AND c.expiryDate BETWEEN :startDate AND :endDate AND c.deleted = false")
    List<Card> findCardsExpiringBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT c FROM Card c WHERE c.account.user.id = :userId AND c.deleted = false")
    List<Card> findByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(c) FROM Card c WHERE c.account.id = :accountId AND c.status = 'ACTIVE' AND c.deleted = false")
    long countActiveCardsByAccountId(@Param("accountId") Long accountId);

    @Query("SELECT c FROM Card c WHERE c.cardType = 'CREDIT' AND c.status = 'ACTIVE' AND c.deleted = false")
    List<Card> findAllActiveCreditCards();

    long countByAccountAndStatusNot(Account account, CardStatus status);

    List<Card> findByAccountInAndStatusNot(List<Account> accounts, CardStatus status);
}
