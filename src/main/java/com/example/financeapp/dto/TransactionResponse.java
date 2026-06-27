package com.example.financeapp.dto;

import com.example.financeapp.entity.BudgetCategory;
import com.example.financeapp.entity.Transaction;
import com.example.financeapp.entity.TransactionStatus;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class TransactionResponse {
    private Long id;
    private BigDecimal amount;
    private String description;
    private BudgetCategory category;
    private TransactionStatus status;
    private String declineReason;
    private LocalDateTime createdAt;

    public static TransactionResponse from(Transaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .amount(transaction.getAmount())
                .description(transaction.getDescription())
                .category(transaction.getCategory())
                .status(transaction.getStatus())
                .declineReason(transaction.getDeclineReason())
                .createdAt(transaction.getCreatedAt())
                .build();
    }


}