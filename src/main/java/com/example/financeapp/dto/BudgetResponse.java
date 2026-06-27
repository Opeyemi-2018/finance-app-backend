package com.example.financeapp.dto;

import com.example.financeapp.entity.Budget;
import com.example.financeapp.entity.BudgetCategory;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.YearMonth;

@Data
@Builder
public class BudgetResponse {
    private Long id;
    private BudgetCategory category;
    private BigDecimal limitAmount;
    private BigDecimal spentAmount;
    private BigDecimal remaining;
    private double percentageUsed;
    private YearMonth month;
    private int transactionCount;

    public static BudgetResponse from(Budget budget) {
        BigDecimal remaining = budget.getLimitAmount()
                .subtract(budget.getSpentAmount());

        double percentage = budget.getLimitAmount().compareTo(BigDecimal.ZERO) == 0
                ? 0
                : budget.getSpentAmount()
                .divide(budget.getLimitAmount(), 4, java.math.RoundingMode.HALF_UP)
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
                .build();
    }
}