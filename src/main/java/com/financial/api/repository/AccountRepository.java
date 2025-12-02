package com.financial.api.repository;

import com.financial.api.entity.Account;
import com.financial.api.entity.User;
import com.financial.api.repository.projection.AccountWithTotalsProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {
    @Query("SELECT COUNT(c) > 0 FROM Account c WHERE c.user = :user AND LOWER(c.name) = LOWER(:name) AND c.isDeleted = false")
    boolean existsByUserAndNameIgnoreCaseAndIsDeletedFalse(@Param("user") User user, @Param("name") String name);

    @Query(
            value = """
                   SELECT
                       ac.id AS account_id,
                       ac.name AS account_name,
                       ac.amount AS amount,
                       ac.description AS description,
                       cu.currency AS currency,
                       cu.currency_code AS currency_code,
                       cu.symbol AS currency_symbol,

                       SUM(CASE WHEN tr.transaction_type_id = 1 THEN tr.amount ELSE 0 END) AS total_income,
                       SUM(CASE WHEN tr.transaction_type_id = 2 THEN tr.amount ELSE 0 END) AS total_expense
                   FROM accounts ac
                   LEFT JOIN transactions tr ON ac.id = tr.account_id
                   LEFT JOIN currencies cu ON cu.id = ac.currency_id
                   WHERE ac.user_id = :userId
                   GROUP BY ac.id, ac.name
                   """,
            nativeQuery = true
    )
    Page<AccountWithTotalsProjection> findByUserAndIsDeletedFalse(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT c FROM Account c WHERE (c.user = :user) AND c.isDeleted = false")
    List<Account> findAllByUserAndIsDeletedFalse(@Param("user") User user);

    @Query("SELECT SUM(a.amount) FROM Account a WHERE a.user = :user AND a.isDeleted = false")
    BigDecimal getCurrentUserBalance(@Param("user") User user);
}
