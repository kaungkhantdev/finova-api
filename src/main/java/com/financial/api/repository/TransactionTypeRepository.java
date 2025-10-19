package com.financial.api.repository;

import com.financial.api.entity.TransactionType;
import com.financial.api.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransactionTypeRepository extends JpaRepository<TransactionType, Long> {
    /**
     * Find all system currencies or currencies owned by the user (not deleted)
     */
    @Query("SELECT c FROM TransactionType c WHERE (c.user = :user OR c.isSystem = true) AND c.isDeleted = false")
    Page<TransactionType> findByUserOrIsSystemTrueAndIsDeletedFalse(@Param("user") User user, Pageable pageable);

    /**
     * Check if user already has a category with the given name
     */
    @Query("SELECT COUNT(c) > 0 FROM TransactionType c WHERE c.user = :user AND LOWER(c.name) = LOWER(:name) AND c.isDeleted = false")
    boolean existsByUserAndNameIgnoreCaseAndIsDeletedFalse(@Param("user") User user, @Param("name") String name);
}
