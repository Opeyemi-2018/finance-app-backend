package com.example.financeapp.controller;

import com.example.financeapp.dto.ApiResponse;
import com.example.financeapp.dto.CardResponse;
import com.example.financeapp.dto.FundWalletRequest;
import com.example.financeapp.entity.User;
import com.example.financeapp.entity.Wallet;
import com.example.financeapp.services.CardService;
import com.example.financeapp.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CardController {

    private final CardService cardService;
    private final UserService userService;

    public CardController(CardService cardService, UserService userService) {
        this.cardService = cardService;
        this.userService = userService;
    }



    // GET /api/v1/card
    @GetMapping("/card")
    public ApiResponse<CardResponse> getCard(Authentication auth) {
        try {
            return new ApiResponse<>(true, "Card retrieved",
                    cardService.getCard(auth.getName()));
        } catch (RuntimeException ex) {
            return new ApiResponse<>(false, ex.getMessage(), null);
        }
    }
    
    // PATCH /api/v1/card/freeze
    @PatchMapping("/freeze/card")
    public ApiResponse<CardResponse> freezeCard(Authentication auth) {
        try {
            return new ApiResponse<>(true, "Card frozen",
                    cardService.freezeCard(auth.getName()));
        } catch (RuntimeException ex) {
            return new ApiResponse<>(false, ex.getMessage(), null);
        }
    }

    // PATCH /api/v1/card/unfreeze
    @PatchMapping("/unfreeze/card")
    public ApiResponse<CardResponse> unfreezeCard(Authentication auth) {
        try {
            return new ApiResponse<>(true, "Card unfrozen",
                    cardService.unfreezeCard(auth.getName()));
        } catch (RuntimeException ex) {
            return new ApiResponse<>(false, ex.getMessage(), null);
        }
    }

    // GET /api/card/number — returns full card number
    @GetMapping("/card/reveal")
    public ResponseEntity<ApiResponse<String>> getCardNumber(Authentication auth) {
        try {
            return ResponseEntity.ok(new ApiResponse<>(true, "Card number retrieved",
                    cardService.getCardNumber(auth.getName())));
        } catch (RuntimeException ex) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, ex.getMessage(), null));
        }
    }


    @GetMapping("/wallet")
    public ApiResponse<Wallet> getWallet(Authentication auth) {
        try {
            return new ApiResponse<>(true, "Wallet retrieved",
                    cardService.getWallet(auth.getName()));
        } catch (RuntimeException ex) {
            return new ApiResponse<>(false, ex.getMessage(), null);
        }
    }

    @PostMapping("/wallet/fund")
    public ApiResponse<Wallet> fundWallet(
            Authentication auth,
            @RequestBody FundWalletRequest request
    ) {
        try {
            return new ApiResponse<>(true, "Wallet funded",
                    cardService.fundWallet(auth.getName(), request.getAmount()));
        } catch (RuntimeException ex) {
            return new ApiResponse<>(false, ex.getMessage(), null);
        }
    }
}