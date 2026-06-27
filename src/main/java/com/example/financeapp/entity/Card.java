package com.example.financeapp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.example.financeapp.entity.CardStatus;

@Entity
@Table(name = "cards")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    private String cardNumber;
    private String last4;
    private String cardHolderName;
    private String expiryDate;
    private String cvv;
    private BigDecimal spendingLimit;

    @Enumerated(EnumType.STRING)
    private CardStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}