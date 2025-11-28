package com.financial.api.repository;

import com.financial.api.entity.Transaction;
import com.financial.api.entity.User;
import com.financial.api.repository.projection.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query("SELECT c FROM Transaction c WHERE (c.user = :user) AND c.isDeleted = false")
    Page<Transaction> findByUserAndIsDeletedFalse(@Param("user") User user, Pageable pageable);

    @Query(
            value = """
                    SELECT
                        DATE(created_at) AS date,
                        SUM(CASE WHEN transaction_type_id = 1 THEN amount ELSE 0 END) AS expense,
                        SUM(CASE WHEN transaction_type_id = 2 THEN amount ELSE 0 END) AS income
                    FROM transactions
                    WHERE user_id = :userId
                      AND is_deleted = 0
                      AND created_at >= DATE_SUB(CURDATE(), INTERVAL :days DAY)
                    GROUP BY DATE(created_at)
                    ORDER BY DATE(created_at)
                    """,
            nativeQuery = true
    )
    List<TransactionByDateProjection> getTransactionByDate(@Param("userId") Long userId, @Param("days") Integer days);

    @Query(
            value = """
                    SELECT
                        MONTH(created_at) AS month,
                        SUM(CASE WHEN transaction_type_id = 1 THEN amount ELSE 0 END) AS expense,
                        SUM(CASE WHEN transaction_type_id = 2 THEN amount ELSE 0 END) AS income
                    FROM transactions
                    WHERE user_id = :userId
                      AND is_deleted = 0
                    GROUP BY MONTH(created_at)
                    ORDER BY MONTH(created_at);
                    """,
            nativeQuery = true
    )
    List<TransactionByMonthProjection> getTransactionByMonth(@Param("userId") Long userId);

    @Query(
            value = """
                    SELECT
                        SUM(amount) as dailyAmount,
                        DATE(created_at) as date
                    FROM transactions
                    WHERE transaction_type_id = :transactionTypeId
                        AND user_id = :userId
                        AND is_deleted = 0
                        AND DATE(created_at) = DATE(NOW())
                    GROUP BY DATE(created_at)
                    """,
            nativeQuery = true
    )
    DailyAmountProjection getDailyAmount(
            @Param("userId") Long userId,
            @Param("transactionTypeId") Long transactionTypeId
    );

    @Query(
            value = """
                    SELECT
                        SUM(amount) AS weekly_amount,
                        DATE_SUB(DATE(MIN(created_at)), INTERVAL WEEKDAY(MIN(created_at)) DAY) as week_start,
                        DATE_ADD(DATE_SUB(DATE(MIN(created_at)), INTERVAL WEEKDAY(MIN(created_at)) DAY), INTERVAL 6 DAY) as week_end
                    FROM transactions
                    WHERE transaction_type_id = :transactionTypeId
                        AND user_id = :userId
                        AND is_deleted = 0
                        AND created_at >= DATE_SUB(NOW(), INTERVAL 7 DAY)
                    GROUP BY YEARWEEK(created_at, 1);
                    """,
            nativeQuery = true
    )
    WeeklyAmountProjection getWeeklyAmount(
            @Param("userId") Long userId,
            @Param("transactionTypeId") Long transactionTypeId
    );

    @Query(
            value = """
                    SELECT
                    	SUM(amount) as monthly_amount,
                    	MONTH(created_at) as month
                    FROM transactions
                    	WHERE transaction_type_id = :transactionTypeId
                    	AND user_id = :userId
                    	AND is_deleted = 0
                    	AND MONTH(created_at) = MONTH(NOW())
                    GROUP BY MONTH(created_at);
                    """,
            nativeQuery = true
    )
    MonthlyAmountProjection getMonthlyAmount(
            @Param("userId") Long userId,
            @Param("transactionTypeId") Long transactionTypeId
    );

    @Query(
            value = """
        SELECT
            CASE
                WHEN t.category_id IN (1, 2) THEN c.name
                ELSE 'Other'
            END as categoryName,
            COUNT(*) AS transactionCount,
            SUM(t.amount) as totalAmount,
            ROUND(SUM(t.amount) * 100.0 / SUM(SUM(t.amount)) OVER(), 2) as percent
        FROM transactions t 
        LEFT JOIN categories c ON t.category_id = c.id
        WHERE t.user_id = :userId
          AND t.is_deleted = 0
          AND t.transaction_type_id = :transactionTypeId
        GROUP BY
            CASE 
                WHEN t.category_id IN (1, 2) THEN c.name
                ELSE 'Other'
            END
        ORDER BY totalAmount DESC
        """,
            nativeQuery = true
    )
    List<TransactionByCategoryProjection> getTransactionsByCategory(
            @Param("userId") Long userId,
            @Param("transactionTypeId") Long transactionTypeId
    );

    @Query(
            value = """
        SELECT
            curr.income AS currentIncome,
            prev.income AS previousIncome,
            ROUND(((curr.income - prev.income) / NULLIF(prev.income, 0)) * 100, 1) AS incomeChangePercent,
            curr.expense AS currentExpense,
            prev.expense AS previousExpense,
            ROUND(((curr.expense - prev.expense) / NULLIF(prev.expense, 0)) * 100, 1) AS expenseChangePercent
        FROM (
            SELECT
                SUM(CASE WHEN transaction_type_id = 2 THEN amount ELSE 0 END) AS income,
                SUM(CASE WHEN transaction_type_id = 1 THEN amount ELSE 0 END) AS expense
            FROM transactions
            WHERE user_id = :userId
              AND is_deleted = 0
              AND DATE_FORMAT(created_at, '%Y-%m') = DATE_FORMAT(NOW(), '%Y-%m')
        ) curr
        CROSS JOIN (
            SELECT
                SUM(CASE WHEN transaction_type_id = 2 THEN amount ELSE 0 END) AS income,
                SUM(CASE WHEN transaction_type_id = 1 THEN amount ELSE 0 END) AS expense
            FROM transactions
            WHERE user_id = :userId
              AND is_deleted = 0
              AND DATE_FORMAT(created_at, '%Y-%m') = DATE_FORMAT(DATE_SUB(NOW(), INTERVAL 1 MONTH), '%Y-%m')
        ) prev
        """,
            nativeQuery = true
    )
    MonthlyComparisonProjection getMonthlyComparison(@Param("userId") Long userId);
}
