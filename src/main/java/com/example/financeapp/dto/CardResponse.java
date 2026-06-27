package com.example.financeapp.dto;

import com.example.financeapp.entity.Card;
import lombok.Builder;
import lombok.Data;
import com.example.financeapp.entity.CardStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class CardResponse {
    private Long id;
    private String cardHolderName;
    private String maskedCardNumber;  // •••• •••• •••• 4521
    private String last4;
    private String expiryDate;
    private String cvv;
    private BigDecimal spendingLimit;
    private CardStatus status;
    private LocalDateTime createdAt;

    public static CardResponse from(Card card) {
        return CardResponse.builder()
                .id(card.getId())
                .cardHolderName(card.getCardHolderName())
                .maskedCardNumber("•••• •••• •••• " + card.getLast4())
                .last4(card.getLast4())
                .expiryDate(card.getExpiryDate())
                .cvv(card.getCvv())
                .spendingLimit(card.getSpendingLimit())
                .status(card.getStatus())
                .createdAt(card.getCreatedAt())
                .build();
    }
}