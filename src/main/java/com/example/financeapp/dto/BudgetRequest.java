package com.example.financeapp.dto;


import com.example.financeapp.entity.BudgetCategory;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BudgetRequest {
    private BudgetCategory category;
    private BigDecimal limitAmount;
}