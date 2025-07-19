package main.java.com.financetracker.transaction.repository;

import com.financetracker.transaction.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Page<Transaction> findByUserIdOrderByTransactionDateDesc(Long userId, Pageable pageable);

    List<Transaction> findByUserIdAndTransactionDateBetween(Long userId, LocalDateTime startDate,
            LocalDateTime endDate);

    List<Transaction> findByUserIdAndCategory(Long userId, String category);

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.userId = :userId AND t.transactionType = :type")
    BigDecimal getTotalAmountByUserIdAndType(@Param("userId") Long userId,
            @Param("type") Transaction.TransactionType type);

    @Query("SELECT t.category, SUM(t.amount) FROM Transaction t WHERE t.userId = :userId AND t.transactionType = :type GROUP BY t.category")
    List<Object[]> getCategoryWiseExpenses(@Param("userId") Long userId,
            @Param("type") Transaction.TransactionType type);
}