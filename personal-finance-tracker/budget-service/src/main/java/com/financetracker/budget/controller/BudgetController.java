package main.java.com.financetracker.budget.controller;

import com.finance.budget.entity.Budget;
import com.finance.budget.service.BudgetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/budgets")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class BudgetController {
    private final BudgetService budgetService;

    @PostMapping
    public ResponseEntity<Budget> createBudget(@RequestBody Budget budget) {
        return ResponseEntity.ok(budgetService.createBudget(budget));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Budget>> getBudgetsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(budgetService.getBudgetsByUserId(userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Budget> updateBudget(@PathVariable Long id, @RequestBody Budget budget) {
        return ResponseEntity.ok(budgetService.updateBudget(id, budget));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBudget(@PathVariable Long id) {
        budgetService.deleteBudget(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/update-spent")
    public ResponseEntity<Void> updateBudgetSpent(@RequestParam Long userId,
            @RequestParam String category,
            @RequestParam BigDecimal amount) {
        budgetService.updateBudgetSpent(userId, category, amount);
        return ResponseEntity.ok().build();
    }
}