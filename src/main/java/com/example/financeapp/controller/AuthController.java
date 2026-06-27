package com.example.financeapp.controller;

import com.example.financeapp.dto.*;
import com.example.financeapp.entity.User;
import com.example.financeapp.jwt.JwtService;
import com.example.financeapp.services.UserService;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(
            UserService userService,
            AuthenticationManager authenticationManager,
            JwtService jwtService
    ) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    // REGISTER
    @PostMapping("/register")
    public ApiResponse<?> register(@RequestBody RegisterRequest request) {

        try {
            User user = userService.register(request);

            return new ApiResponse<>(
                    true,
                    "User registered successfully",
                    user
            );

        } catch (RuntimeException ex) {

            return new ApiResponse<>(
                    false,
                    ex.getMessage(),
                    null
            );
        }
    }

    // LOGIN
    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(
            @RequestBody LoginRequest request
    ) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userService.getByEmail(request.getEmail());

        String token = jwtService.generateToken(user.getEmail());

        AuthResponse response = AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .userName(user.getUserName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();

        return new ApiResponse<>(
                true,
                "Login successful",
                response
        );
    }


    @GetMapping("/my-profile")
    public ApiResponse<UserProfileResponse> getMe(Authentication auth) {
        try {
            User user = userService.getByEmail(auth.getName());
            return new ApiResponse<>(true, "User retrieved", UserProfileResponse.from(user));
        } catch (RuntimeException ex) {
            return new ApiResponse<>(false, ex.getMessage(), null);
        }
    }
}