package com.example.financeapp.services;

import com.example.financeapp.dto.RegisterRequest;
import com.example.financeapp.entity.Card;
import com.example.financeapp.entity.CardStatus;
import com.example.financeapp.entity.User;
import com.example.financeapp.entity.Wallet;
import com.example.financeapp.repository.CardRepository;
import com.example.financeapp.repository.UserRepository;
import com.example.financeapp.repository.WalletRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final CardRepository cardRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            WalletRepository walletRepository,
            CardRepository cardRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.cardRepository = cardRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // 1. Create user
        User user = User.builder()
                .userName(request.getUserName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("USER")
                .build();

        // 2. Create wallet
        Wallet wallet = Wallet.builder()
                .balance(BigDecimal.ZERO)
                .build();
        wallet.setUser(user);
        user.setWallet(wallet);

        // 3. Auto-generate card
        Card card = generateCard(user);
        user.setCard(card);

        // 4. Save user (cascades wallet + card)
        return userRepository.save(user);
    }

    private Card generateCard(User user) {
        SecureRandom random = new SecureRandom();

        StringBuilder cardNum = new StringBuilder();
        for (int i = 0; i < 16; i++) cardNum.append(random.nextInt(10));
        String cardNumber = cardNum.toString();
        String last4 = cardNumber.substring(12);

        String cvv = String.format("%03d", random.nextInt(1000));

        String expiry = LocalDate.now()
                .plusYears(3)
                .format(DateTimeFormatter.ofPattern("MM/yy"));

        return Card.builder()
                .user(user)
                .cardNumber(cardNumber)
                .last4(last4)
                .cardHolderName(user.getUserName())
                .expiryDate(expiry)
                .cvv(cvv)
                .spendingLimit(new BigDecimal("500000.00"))
                .status(CardStatus.ACTIVE)
                .build();
    }

    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}