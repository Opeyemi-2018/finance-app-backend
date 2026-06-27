package com.example.financeapp.dto;

import com.example.financeapp.entity.BudgetCategory;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class TransactionRequest {
    private String cardNumber;
    private String cvv;
    private String expiryDate;
    private BigDecimal amount;
    private String description;
    private BudgetCategory category;
}