package com.example.financeapp.repository;

import com.example.financeapp.entity.BudgetCategory;
import com.example.financeapp.entity.Transaction;
import com.example.financeapp.entity.TransactionStatus;
import com.example.financeapp.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // All transactions paginated
    Page<Transaction> findByUserOrderByCreatedAtDesc(
            User user, Pageable pageable
    );

    // Search by description — hits DB directly
    Page<Transaction> findByUserAndDescriptionContainingIgnoreCaseOrderByCreatedAtDesc(
            User user, String description, Pageable pageable
    );

    // Latest 3 approved for a category — budget cards
    List<Transaction> findTop3ByUserAndCategoryAndStatusOrderByCreatedAtDesc(
            User user, BudgetCategory category, TransactionStatus status
    );

    // Count approved per category
    int countByUserAndCategoryAndStatus(
            User user, BudgetCategory category, TransactionStatus status
    );
}