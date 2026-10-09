package org.example.stationtracker.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.stationtracker.DTO.AuthResponse;
import org.example.stationtracker.DTO.LoginRequest;
import org.example.stationtracker.DTO.RefreshRequest;
import org.example.stationtracker.DTO.RegisterRequest;
import org.example.stationtracker.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(
        name = "Authentication",
        description = "Registration, login and token refresh"
)
public class AuthenticationController {
    private final AuthService authService;

    @Autowired
    public AuthenticationController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(
            summary = "Register user",
            description = """
                    Creates a new user and returns
                    access and refresh tokens.
                    """
    )
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        AuthResponse authResponse = authService.register(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(authResponse);
    }

    @Operation(
            summary = "Login",
            description = """
                    Authenticates user credentials and returns
                    access and refresh tokens.
                    """
    )
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        AuthResponse authResponse = authService.login(loginRequest);
        return ResponseEntity.ok().body(authResponse);
    }

    @Operation(
            summary = "Refresh access token",
            description = """
                    Rotates the refresh token and returns
                    a new access/refresh token pair.
                    """
    )
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshRequest refreshRequest) {
        return ResponseEntity.ok(authService.refreshToken(refreshRequest));
    }
}
