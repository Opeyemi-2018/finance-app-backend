package com.example.financeapp.repository;

import com.example.financeapp.entity.Budget;
import com.example.financeapp.entity.BudgetCategory;
import com.example.financeapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    List<Budget> findByUser(User user);
    List<Budget> findByUserAndMonth(User user, YearMonth month);
    Optional<Budget> findByUserAndCategoryAndMonth(User user, BudgetCategory category, YearMonth month);
    boolean existsByUserAndCategoryAndMonth(User user, BudgetCategory category, YearMonth month);
}
