package com.example.financeapp.services;

import com.example.financeapp.dto.BudgetRequest;
import com.example.financeapp.dto.BudgetResponse;
import com.example.financeapp.entity.Budget;
import com.example.financeapp.entity.BudgetCategory;
import com.example.financeapp.entity.TransactionStatus;
import com.example.financeapp.entity.User;
import com.example.financeapp.repository.BudgetRepository;
import com.example.financeapp.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

@Service
public class BudgetService {

    private final UserService userService;
    private final BudgetRepository budgetRepository;

    private final TransactionRepository transactionRepository;

    public BudgetService(
            UserService userService,
            BudgetRepository budgetRepository,
            TransactionRepository transactionRepository  // ← add
    ) {
        this.userService = userService;
        this.budgetRepository = budgetRepository;
        this.transactionRepository = transactionRepository;  // ← add
    }

    // ── Create budget ─────────────────────────────────────────
    @Transactional
    public BudgetResponse createBudget(String email, BudgetRequest request) {
        User user = userService.getByEmail(email);

        // Prevent duplicate budget for same category + month
        if (budgetRepository.existsByUserAndCategoryAndMonth(
                user, request.getCategory(), YearMonth.now())) {
            throw new RuntimeException(
                    "You already have a " + request.getCategory() + " budget for this month"
            );
        }

        Budget budget = Budget.builder()
                .user(user)
                .category(request.getCategory())
                .limitAmount(request.getLimitAmount())
                .build();
        // spentAmount and month are auto-set in @PrePersist

        return BudgetResponse.from(budgetRepository.save(budget));
    }

    // ── Get current month budgets ─────────────────────────────
    // In BudgetService — new method that builds BudgetResponse WITH transaction count
    public List<BudgetResponse> getBudgets(String email) {
        User user = userService.getByEmail(email);
        List<Budget> budgets = budgetRepository.findByUserAndMonth(user, YearMonth.now());

        return budgets.stream()
                .map(budget -> {
                    // count approved transactions for this category
                    int count = transactionRepository.countByUserAndCategoryAndStatus(
                            user,
                            budget.getCategory(),
                            TransactionStatus.APPROVED
                    );

                    // build response with count
                    BigDecimal remaining = budget.getLimitAmount()
                            .subtract(budget.getSpentAmount());

                    double percentage = budget.getLimitAmount()
                            .compareTo(BigDecimal.ZERO) == 0 ? 0
                            : budget.getSpentAmount()
                            .divide(budget.getLimitAmount(), 4,
                                    java.math.RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100))
                            .doubleValue();

                    return BudgetResponse.builder()
                            .id(budget.getId())
                            .category(budget.getCategory())
                            .limitAmount(budget.getLimitAmount())
                            .spentAmount(budget.getSpentAmount())
                            .remaining(remaining)
                            .percentageUsed(percentage)
                            .month(budget.getMonth())
                            .transactionCount(count)   // ← the count
                            .build();
                })
                .toList();
    }

    // ── Update budget limit ───────────────────────────────────
    @Transactional
    public BudgetResponse updateBudget(String email, Long budgetId, BudgetRequest request) {
        User user = userService.getByEmail(email);

        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new RuntimeException("Budget not found"));

        // Make sure this budget belongs to the logged-in user
        if (!budget.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        if (request.getLimitAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Limit must be greater than zero");
        }

        budget.setLimitAmount(request.getLimitAmount());
        return BudgetResponse.from(budgetRepository.save(budget));
    }

    // ── Delete budget ─────────────────────────────────────────
    @Transactional
    public void deleteBudget(String email, Long budgetId) {
        User user = userService.getByEmail(email);

        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new RuntimeException("Budget not found"));

        if (!budget.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        budgetRepository.delete(budget);
    }

    // ── Called internally when a transaction happens ──────────
    // This is what links transactions to budgets automatically
    @Transactional
    public void addToSpentAmount(User user, BudgetCategory category, BigDecimal amount) {
        budgetRepository
                .findByUserAndCategoryAndMonth(user, category, YearMonth.now())
                .ifPresent(budget -> {
                    budget.setSpentAmount(budget.getSpentAmount().add(amount));
                    budgetRepository.save(budget);
                });
        // If no budget exists for this category, silently skip — no error
    }
}