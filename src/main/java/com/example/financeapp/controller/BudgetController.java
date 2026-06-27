package com.example.financeapp.controller;

import com.example.financeapp.dto.ApiResponse;
import com.example.financeapp.dto.BudgetRequest;
import com.example.financeapp.dto.BudgetResponse;
import com.example.financeapp.services.BudgetService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/budget")
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    // POST /api/budget
    @PostMapping
    public ApiResponse<BudgetResponse> createBudget(
            Authentication auth,
            @RequestBody BudgetRequest request
    ) {
        try {
            return new ApiResponse<>(true, "Budget created",
                    budgetService.createBudget(auth.getName(), request));
        } catch (RuntimeException ex) {
            return new ApiResponse<>(false, ex.getMessage(), null);
        }
    }

    // GET /api/budget
    @GetMapping
    public ApiResponse<List<BudgetResponse>> getBudgets(Authentication auth) {
        try {
            return new ApiResponse<>(true, "Budgets retrieved",
                    budgetService.getBudgets(auth.getName()));
        } catch (RuntimeException ex) {
            return new ApiResponse<>(false, ex.getMessage(), null);
        }
    }

    // PUT /api/budget/{id}
    @PutMapping("/{id}")
    public ApiResponse<BudgetResponse> updateBudget(
            Authentication auth,
            @PathVariable Long id,
            @RequestBody BudgetRequest request
    ) {
        try {
            return new ApiResponse<>(true, "Budget updated",
                    budgetService.updateBudget(auth.getName(), id, request));
        } catch (RuntimeException ex) {
            return new ApiResponse<>(false, ex.getMessage(), null);
        }
    }

    // DELETE /api/budget/{id}
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteBudget(
            Authentication auth,
            @PathVariable Long id
    ) {
        try {
            budgetService.deleteBudget(auth.getName(), id);
            return new ApiResponse<>(true, "Budget deleted", null);
        } catch (RuntimeException ex) {
            return new ApiResponse<>(false, ex.getMessage(), null);
        }
    }
}