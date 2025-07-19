package main.java.com.financetracker.transaction.service;

import com.financetracker.transaction.dto.TransactionRequest;
import com.financetracker.transaction.dto.TransactionResponse;
import com.financetracker.transaction.entity.Transaction;
import com.financetracker.transaction.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    public TransactionResponse createTransaction(Long userId, TransactionRequest request) {
        Transaction transaction = new Transaction();
        transaction.setUserId(userId);
        transaction.setAmount(request.getAmount());
        transaction.setDescription(request.getDescription());
        transaction.setTransactionType(request.getTransactionType());
        transaction.setCategory(request.getCategory());
        transaction.setTransactionDate(
                request.getTransactionDate() != null ? request.getTransactionDate() : LocalDateTime.now());
        transaction.setPaymentMethod(request.getPaymentMethod());
        transaction.setTags(request.getTags());

        Transaction savedTransaction = transactionRepository.save(transaction);
        return convertToResponse(savedTransaction);
    }

    public Page<TransactionResponse> getUserTransactions(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Transaction> transactions = transactionRepository.findByUserIdOrderByTransactionDateDesc(userId, pageable);
        return transactions.map(this::convertToResponse);
    }

    public Optional<TransactionResponse> getTransactionById(Long id) {
        return transactionRepository.findById(id)
                .map(this::convertToResponse);
    }

    public TransactionResponse updateTransaction(Long id, TransactionRequest request) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        transaction.setAmount(request.getAmount());
        transaction.setDescription(request.getDescription());
        transaction.setTransactionType(request.getTransactionType());
        transaction.setCategory(request.getCategory());
        if (request.getTransactionDate() != null) {
            transaction.setTransactionDate(request.getTransactionDate());
        }
        transaction.setPaymentMethod(request.getPaymentMethod());
        transaction.setTags(request.getTags());

        Transaction updatedTransaction = transactionRepository.save(transaction);
        return convertToResponse(updatedTransaction);
    }

    public void deleteTransaction(Long id) {
        transactionRepository.deleteById(id);
    }

    public Map<String, Object> getTransactionSummary(Long userId) {
        Map<String, Object> summary = new HashMap<>();

        BigDecimal totalIncome = transactionRepository.getTotalAmountByUserIdAndType(userId,
                Transaction.TransactionType.INCOME);
        BigDecimal totalExpense = transactionRepository.getTotalAmountByUserIdAndType(userId,
                Transaction.TransactionType.EXPENSE);

        summary.put("totalIncome", totalIncome != null ? totalIncome : BigDecimal.ZERO);
        summary.put("totalExpense", totalExpense != null ? totalExpense : BigDecimal.ZERO);
        summary.put("balance", (totalIncome != null ? totalIncome : BigDecimal.ZERO)
                .subtract(totalExpense != null ? totalExpense : BigDecimal.ZERO));

        List<Object[]> categoryExpenses = transactionRepository.getCategoryWiseExpenses(userId,
                Transaction.TransactionType.EXPENSE);
        Map<String, BigDecimal> categoryWiseExpenses = new HashMap<>();
        for (Object[] row : categoryExpenses) {
            categoryWiseExpenses.put((String) row[0], (BigDecimal) row[1]);
        }
        summary.put("categoryWiseExpenses", categoryWiseExpenses);

        return summary;
    }

    private TransactionResponse convertToResponse(Transaction transaction) {
        TransactionResponse response = new TransactionResponse();
        response.setId(transaction.getId());
        response.setUserId(transaction.getUserId());
        response.setAmount(transaction.getAmount());
        response.setDescription(transaction.getDescription());
        response.setTransactionType(transaction.getTransactionType());
        response.setCategory(transaction.getCategory());
        response.setTransactionDate(transaction.getTransactionDate());
        response.setPaymentMethod(transaction.getPaymentMethod());
        response.setTags(transaction.getTags());
        response.setCreatedAt(transaction.getCreatedAt());
        response.setUpdatedAt(transaction.getUpdatedAt());
        return response;
    }
}