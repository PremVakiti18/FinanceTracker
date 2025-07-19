package main.java.com.financetracker.budget.repository;

import com.finance.budget.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {
    List<Budget> findByUserId(Long userId);

    List<Budget> findByUserIdAndCategory(Long userId, String category);

    @Query("SELECT b FROM Budget b WHERE b.userId = ?1 AND b.startDate <= ?2 AND b.endDate >= ?2")
    List<Budget> findActiveBudgetsByUserIdAndDate(Long userId, LocalDate date);
}