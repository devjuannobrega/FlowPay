package com.flowpay.banking.repository;

import com.flowpay.banking.entity.KycDocument;
import com.flowpay.banking.enums.KycStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository para operações com documentos KYC
 */
@Repository
public interface KycDocumentRepository extends JpaRepository<KycDocument, Long> {

    List<KycDocument> findByUserId(Long userId);

    List<KycDocument> findByUserIdAndDeletedFalse(Long userId);

    List<KycDocument> findByStatus(KycStatus status);

    List<KycDocument> findByDocumentType(String documentType);

    @Query("SELECT k FROM KycDocument k WHERE k.user.id = :userId AND k.status = :status AND k.deleted = false")
    List<KycDocument> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") KycStatus status);

    @Query("SELECT k FROM KycDocument k WHERE " +
           "k.user.id = :userId " +
           "AND k.documentType = :documentType " +
           "AND k.deleted = false " +
           "ORDER BY k.createdAt DESC")
    List<KycDocument> findByUserIdAndDocumentType(@Param("userId") Long userId, @Param("documentType") String documentType);

    @Query("SELECT k FROM KycDocument k WHERE k.status = 'PENDING' AND k.deleted = false ORDER BY k.createdAt ASC")
    List<KycDocument> findPendingDocuments();

    @Query("SELECT k FROM KycDocument k WHERE k.status = 'UNDER_REVIEW' AND k.deleted = false")
    List<KycDocument> findDocumentsUnderReview();

    @Query("SELECT COUNT(k) FROM KycDocument k WHERE " +
           "k.user.id = :userId " +
           "AND k.status = 'APPROVED' " +
           "AND k.deleted = false")
    long countApprovedDocumentsByUserId(@Param("userId") Long userId);

    @Query("SELECT k FROM KycDocument k WHERE k.reviewedBy = :reviewerId AND k.deleted = false")
    List<KycDocument> findByReviewerId(@Param("reviewerId") Long reviewerId);

    @Query("SELECT DISTINCT k.user.id FROM KycDocument k WHERE " +
           "k.status = 'APPROVED' " +
           "AND k.deleted = false " +
           "GROUP BY k.user.id " +
           "HAVING COUNT(k) >= :minDocuments")
    List<Long> findUserIdsWithMinimumApprovedDocuments(@Param("minDocuments") long minDocuments);
}
