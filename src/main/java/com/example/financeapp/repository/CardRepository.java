package com.example.financeapp.repository;

import com.example.financeapp.entity.Card;
import com.example.financeapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {
    Optional<Card> findByUser(User user);
    boolean existsByUser(User user);
    Optional<Card> findByCardNumber(String cardNumber);
}