package com.financial.api.repository;

import com.financial.api.entity.Currency;
import com.financial.api.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, Long> {

    /**
     * Find all system currencies or currencies owned by the user (not deleted)
     */
    @Query("SELECT c FROM Currency c WHERE (c.user = :user OR c.isSystem = true) AND c.isDeleted = false")
    Page<Currency> findByUserOrIsSystemTrueAndIsDeletedFalse(@Param("user") User user, Pageable pageable);

    /**
     * Check if user already has a currency with the given code
     */
    boolean existsByUserAndCodeAndIsDeletedFalse(User user, String code);

    /**
     * Find user's currencies only
     */
    List<Currency> findByUserAndIsDeletedFalse(User user);

    /**
     * Find all system currencies
     */
    List<Currency> findByIsSystemTrueAndIsDeletedFalse();
}