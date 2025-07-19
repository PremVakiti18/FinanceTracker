package main.java.com.financetracker.budget.service;

import com.finance.budget.entity.Budget;
import com.finance.budget.repository.BudgetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BudgetService {
    private final BudgetRepository budgetRepository;

    public Budget createBudget(Budget budget) {
        return budgetRepository.save(budget);
    }

    public List<Budget> getBudgetsByUserId(Long userId) {
        return budgetRepository.findByUserId(userId);
    }

    public Budget updateBudget(Long id, Budget budget) {
        budget.setId(id);
        return budgetRepository.save(budget);
    }

    public void deleteBudget(Long id) {
        budgetRepository.deleteById(id);
    }

    public void updateBudgetSpent(Long userId, String category, BigDecimal amount) {
        List<Budget> budgets = budgetRepository.findActiveBudgetsByUserIdAndDate(userId, LocalDate.now());
        budgets.forEach(budget -> {
            if (budget.getCategory().equals(category)) {
                budget.setSpent(budget.getSpent().add(amount));
                if (budget.getSpent().compareTo(budget.getAmount()) > 0) {
                    budget.setStatus(Budget.BudgetStatus.EXCEEDED);
                }
                budgetRepository.save(budget);
            }
        });
    }
}