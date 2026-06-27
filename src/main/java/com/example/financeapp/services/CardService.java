package com.example.financeapp.services;

import com.example.financeapp.dto.CardResponse;
import com.example.financeapp.entity.Card;
import com.example.financeapp.entity.CardStatus;
import com.example.financeapp.entity.User;
import com.example.financeapp.entity.Wallet;
import com.example.financeapp.repository.CardRepository;
import com.example.financeapp.repository.WalletRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CardService {

    private final CardRepository cardRepository;
    private final WalletRepository walletRepository;
    private final UserService userService;

    public CardService(
            CardRepository cardRepository,
            WalletRepository walletRepository,
            UserService userService
    ) {
        this.cardRepository = cardRepository;
        this.walletRepository = walletRepository;
        this.userService = userService;
    }

    // ── Get card ─────────────────────────────────────────────
    public CardResponse getCard(String email) {
        User user = userService.getByEmail(email);
        Card card = cardRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("No card found"));
        return CardResponse.from(card);
    }

    public String getCardNumber(String email) {
        User user = userService.getByEmail(email);
        Card card = cardRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("No card found"));
        return card.getCardNumber();
    }

    // ── Freeze card ──────────────────────────────────────────
    @Transactional
    public CardResponse freezeCard(String email) {
        User user = userService.getByEmail(email);
        Card card = cardRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("No card found"));

        if (card.getStatus() == CardStatus.FROZEN) {
            throw new RuntimeException("Card is already frozen");
        }

        card.setStatus(CardStatus.FROZEN);
        return CardResponse.from(cardRepository.save(card));
    }

    // ── Unfreeze card ────────────────────────────────────────
    @Transactional
    public CardResponse unfreezeCard(String email) {
        User user = userService.getByEmail(email);
        Card card = cardRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("No card found"));

        if (card.getStatus() == CardStatus.ACTIVE) {
            throw new RuntimeException("Card is already active");
        }

        card.setStatus(CardStatus.ACTIVE);
        return CardResponse.from(cardRepository.save(card));
    }

    // ── Fund wallet ──────────────────────────────────────────
    @Transactional
    public Wallet fundWallet(String email, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Amount must be greater than zero");
        }

        User user = userService.getByEmail(email);
        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        wallet.setBalance(wallet.getBalance().add(amount));
        return walletRepository.save(wallet);
    }

    // ── Get wallet ───────────────────────────────────────────
    public Wallet getWallet(String email) {
        User user = userService.getByEmail(email);
        return walletRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));
    }
}