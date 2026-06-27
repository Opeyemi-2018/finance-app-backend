package com.example.financeapp.repository;

import com.example.financeapp.entity.User;
import com.example.financeapp.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WalletRepository
        extends JpaRepository<Wallet, Long> {

    Optional<Wallet> findByUser(User user);
}