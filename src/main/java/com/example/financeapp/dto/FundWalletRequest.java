package com.example.financeapp.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class FundWalletRequest {
    private BigDecimal amount;
}
