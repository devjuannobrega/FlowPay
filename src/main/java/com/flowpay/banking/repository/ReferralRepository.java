package com.flowpay.banking.repository;

import com.flowpay.banking.entity.Referral;
import com.flowpay.banking.enums.ReferralStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para operações com indicações/referrals
 */
@Repository
public interface ReferralRepository extends JpaRepository<Referral, Long> {

    Optional<Referral> findByReferralCode(String referralCode);

    Optional<Referral> findByReferralCodeAndDeletedFalse(String referralCode);

    List<Referral> findByReferrerId(Long referrerId);

    List<Referral> findByReferrerIdAndDeletedFalse(Long referrerId);

    List<Referral> findByReferredUserId(Long referredUserId);

    List<Referral> findByStatus(ReferralStatus status);

    Optional<Referral> findByReferredEmail(String referredEmail);

    @Query("SELECT r FROM Referral r WHERE r.referrer.id = :referrerId AND r.status = :status AND r.deleted = false")
    List<Referral> findByReferrerIdAndStatus(@Param("referrerId") Long referrerId, @Param("status") ReferralStatus status);

    @Query("SELECT COUNT(r) FROM Referral r WHERE r.referrer.id = :referrerId AND r.status = 'COMPLETED' AND r.deleted = false")
    long countCompletedReferralsByReferrerId(@Param("referrerId") Long referrerId);

    @Query("SELECT r FROM Referral r WHERE r.status = 'COMPLETED' AND r.bonusTransaction IS NULL AND r.deleted = false")
    List<Referral> findCompletedReferralsWithoutBonus();

    @Query("SELECT SUM(r.bonusAmount) FROM Referral r WHERE r.referrer.id = :referrerId AND r.status = 'PAID' AND r.deleted = false")
    java.math.BigDecimal getTotalBonusPaidByReferrerId(@Param("referrerId") Long referrerId);

    boolean existsByReferralCode(String referralCode);
}
