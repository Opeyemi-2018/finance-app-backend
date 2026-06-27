package com.example.financeapp.services;

import com.example.financeapp.dto.PagedResponse;
import com.example.financeapp.dto.TransactionRequest;
import com.example.financeapp.dto.TransactionResponse;
import com.example.financeapp.entity.*;
import com.example.financeapp.repository.BudgetRepository;
import com.example.financeapp.repository.CardRepository;
import com.example.financeapp.repository.TransactionRepository;
import com.example.financeapp.repository.WalletRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CardRepository cardRepository;
    private final WalletRepository walletRepository;
    private final UserService userService;
    private final BudgetService budgetService;
    private final BudgetRepository budgetRepository;

    // Update constructor
    public TransactionService(
            TransactionRepository transactionRepository,
            CardRepository cardRepository,
            WalletRepository walletRepository,
            UserService userService,
            BudgetService budgetService,
            BudgetRepository budgetRepository   // ← add this
    ) {
        this.transactionRepository = transactionRepository;
        this.cardRepository = cardRepository;
        this.walletRepository = walletRepository;
        this.userService = userService;
        this.budgetService = budgetService;
        this.budgetRepository = budgetRepository;  // ← add this
    }

    // ── Make a payment ────────────────────────────────────────
    @Transactional
    public TransactionResponse pay(String email, TransactionRequest request) {
        User user = userService.getByEmail(email);

        // 1. Find card by card number user entered
        Card card = cardRepository.findByCardNumber(request.getCardNumber())
                .orElseThrow(() -> new RuntimeException("Card not found"));

        // 2. Verify card belongs to logged-in user
        if (!card.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Card does not belong to this account");
        }

        // 3. Verify CVV
        if (!card.getCvv().equals(request.getCvv())) {
            throw new RuntimeException("Invalid CVV");
        }

        // 4. Verify expiry date
        if (!card.getExpiryDate().equals(request.getExpiryDate())) {
            throw new RuntimeException("Invalid expiry date");
        }

        // 5. Get wallet
        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        // 6. Check budget exists for this category
        boolean budgetExists = budgetRepository
                .existsByUserAndCategoryAndMonth(
                        user, request.getCategory(), YearMonth.now()
                );

        if (!budgetExists) {
            throw new RuntimeException(
                    "No budget set for category: " + request.getCategory()
            );
        }

        // 7. Decision engine
        if (card.getStatus() == CardStatus.FROZEN) {
            throw new RuntimeException("Card is frozen");
        }
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Amount must be greater than zero");
        }
        if (request.getAmount().compareTo(card.getSpendingLimit()) > 0) {
            throw new RuntimeException(
                    "Amount exceeds card spending limit of $"
                            + card.getSpendingLimit().toPlainString()
            );
        }
        if (wallet.getBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Insufficient wallet balance");
        }

        // 8. All passed — build transaction
        Transaction transaction = Transaction.builder()
                .user(user)
                .amount(request.getAmount())
                .description(request.getDescription())
                .category(request.getCategory())
                .status(TransactionStatus.APPROVED)
                .declineReason(null)
                .build();

        // 9. Deduct from wallet
        wallet.setBalance(wallet.getBalance().subtract(request.getAmount()));
        walletRepository.save(wallet);

        // 10. Update budget spentAmount
        budgetService.addToSpentAmount(
                user, request.getCategory(), request.getAmount()
        );

        // 11. Save and return
        return TransactionResponse.from(transactionRepository.save(transaction));
    }

    // ── Get all transactions paginated ────────────────────────
// ── Get all transactions paginated ────────────────────────
    public PagedResponse<TransactionResponse> getTransactions(
            String email, int page, int size, String search
    ) {
        User user = userService.getByEmail(email);
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<TransactionResponse> result;

        if (search != null && !search.trim().isEmpty()) {
            // search hits the DB directly
            result = transactionRepository
                    .findByUserAndDescriptionContainingIgnoreCaseOrderByCreatedAtDesc(
                            user, search.trim(), pageable
                    )
                    .map(TransactionResponse::from);
        } else {
            result = transactionRepository
                    .findByUserOrderByCreatedAtDesc(user, pageable)
                    .map(TransactionResponse::from);
        }

        return PagedResponse.from(result);
    }

    // ── Get latest 3 transactions for a category ─────────────
    public List<TransactionResponse> getLatestByCategory(
            String email, BudgetCategory category
    ) {
        User user = userService.getByEmail(email);
        return transactionRepository
                .findTop3ByUserAndCategoryAndStatusOrderByCreatedAtDesc(
                        user, category, TransactionStatus.APPROVED
                )
                .stream()
                .map(TransactionResponse::from)
                .toList();
    }


}

