package com.financial.api.repository;

import com.financial.api.entity.Transaction;
import com.financial.api.entity.TransactionType;
import com.financial.api.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query("SELECT c FROM Transaction c WHERE (c.user = :user) AND c.isDeleted = false")
    Page<Transaction> findByUserAndIsDeletedFalse(@Param("user") User user, Pageable pageable);
}
