package com.example.financeapp.controller;

import com.example.financeapp.dto.ApiResponse;
import com.example.financeapp.dto.PagedResponse;
import com.example.financeapp.dto.TransactionRequest;
import com.example.financeapp.dto.TransactionResponse;
import com.example.financeapp.entity.BudgetCategory;
import com.example.financeapp.entity.TransactionStatus;
import com.example.financeapp.services.TransactionService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transaction")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    // POST /api/transaction/pay
    @PostMapping("/pay")
    public ResponseEntity<ApiResponse<TransactionResponse>> pay(
            Authentication auth,
            @RequestBody TransactionRequest request
    ) {
        try {
            TransactionResponse response = transactionService.pay(auth.getName(), request);
            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Transaction approved", response)
            );
        } catch (RuntimeException ex) {
            // All decline reasons come here as exceptions — 400 + no data
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, ex.getMessage(), null));
        }
    }

    // GET /api/transaction?page=0
    // GET /api/transaction?page=0&size=5&search=rent
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<TransactionResponse>>> getTransactions(
            Authentication auth,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String search
    ) {
        try {
            return ResponseEntity.ok(new ApiResponse<>(true, "Transactions retrieved",
                    transactionService.getTransactions(auth.getName(), page, size, search)));
        } catch (RuntimeException ex) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, ex.getMessage(), null));
        }
    }

    // GET /api/transaction/category/BILLS
    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getLatestByCategory(
            Authentication auth,
            @PathVariable BudgetCategory category
    ) {
        try {
            return ResponseEntity.ok(new ApiResponse<>(true, "Transactions retrieved",
                    transactionService.getLatestByCategory(auth.getName(), category)));
        } catch (RuntimeException ex) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, ex.getMessage(), null));
        }
    }
}