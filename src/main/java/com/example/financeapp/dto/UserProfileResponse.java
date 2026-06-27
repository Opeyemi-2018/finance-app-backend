package com.example.financeapp.dto;

import com.example.financeapp.entity.CardStatus;
import com.example.financeapp.entity.User;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class UserProfileResponse {

    // User info
    private Long id;
    private String userName;
    private String email;
    private String role;
    private LocalDateTime createdAt;

    // Wallet info
    private BigDecimal walletBalance;

    // Card info
    private String maskedCardNumber;
    private String last4;
    private String cardHolderName;
    private String expiryDate;
    private String cvv;
    private BigDecimal spendingLimit;
    private CardStatus cardStatus;

    public static UserProfileResponse from(User user) {
        UserProfileResponse.UserProfileResponseBuilder builder = UserProfileResponse.builder()
                .id(user.getId())
                .userName(user.getUserName())
                .email(user.getEmail())
                .role(user.getRole())
                .createdAt(user.getCreatedAt());

        // Safely attach wallet info
        if (user.getWallet() != null) {
            builder.walletBalance(user.getWallet().getBalance());
        }

        // Safely attach card info
        if (user.getCard() != null) {
            builder
                    .maskedCardNumber("•••• •••• •••• " + user.getCard().getLast4())
                    .last4(user.getCard().getLast4())
                    .cardHolderName(user.getCard().getCardHolderName())
                    .expiryDate(user.getCard().getExpiryDate())
                    .cvv(user.getCard().getCvv())
                    .spendingLimit(user.getCard().getSpendingLimit())
                    .cardStatus(user.getCard().getStatus());
        }

        return builder.build();
    }
}