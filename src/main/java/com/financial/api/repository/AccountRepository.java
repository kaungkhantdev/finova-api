package com.financial.api.repository;

import com.financial.api.entity.Account;
import com.financial.api.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AccountRepository extends JpaRepository<Account, Long> {
    @Query("SELECT COUNT(c) > 0 FROM Category c WHERE c.user = :user AND LOWER(c.name) = LOWER(:name) AND c.isDeleted = false")
    boolean existsByUserAndNameIgnoreCaseAndIsDeletedFalse(@Param("user") User user, @Param("name") String name);

    @Query("SELECT c FROM Account c WHERE (c.user = :user) AND c.isDeleted = false")
    Page<Account> findByUserAndIsDeletedFalse(@Param("user") User user, Pageable pageable);

}
