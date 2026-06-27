package com.example.financeapp.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private BigDecimal amount;
    private String description;  // e.g. "Rent payment", "Netflix"

    @Enumerated(EnumType.STRING)
    private BudgetCategory category;  // reuse your existing enum

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;  // APPROVED or DECLINED

    private String declineReason;  // why it was declined (null if approved)

    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}