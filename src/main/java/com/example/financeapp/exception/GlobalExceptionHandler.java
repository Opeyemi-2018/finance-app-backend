package com.example.financeapp.exception;

import com.example.financeapp.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<?>> handleBadCredentials(BadCredentialsException ex) {

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse<>(
                        false,
                        "Invalid email or password",
                        null
                ));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<?>> handleInvalidJson(HttpMessageNotReadableException ex) {

        String message = "Invalid request body";

        if (ex.getMessage() != null && ex.getMessage().contains("BudgetCategory")) {
            message = "Invalid category. Valid values are: " +
                    "FOOD_AND_DINING, ENTERTAINMENT, BILLS, PERSONAL_CARE, " +
                    "TRANSPORT, SHOPPING, HEALTH, EDUCATION, OTHERS";
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(false, message, null));
    }
}