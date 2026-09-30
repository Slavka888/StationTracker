package org.example.stationtracker.service;

import jakarta.transaction.Transactional;
import org.example.stationtracker.DTO.AuthResponse;
import org.example.stationtracker.DTO.LoginRequest;
import org.example.stationtracker.DTO.RegisterRequest;
import org.example.stationtracker.entity.User;
import org.example.stationtracker.repository.UserRepository;
import org.example.stationtracker.security.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;

    @Autowired
    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JWTService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest registerRequest) {
        String login = registerRequest.login();

        if (userRepository.existsUserByLogin(login)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User with login " + login + " already exists");
        }

        String password = passwordEncoder.encode(registerRequest.password());
        User user = new User(login, password);
        userRepository.save(user);

        String token = jwtService.generateAccessToken(user);

        return new AuthResponse(
                token,
                "Bearer",
                jwtService.getExpiresInSeconds()
        );
    }

    public AuthResponse login(LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken
                            .unauthenticated(
                                    loginRequest.login(),
                                    loginRequest.password()
                            )
            );
        }
        catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid login or password");
        }

        User user = userRepository.findUserByLogin(loginRequest.login())
                .orElseThrow();

        String token = jwtService.generateAccessToken(user);

        return new AuthResponse(
                token,
                "Bearer",
                jwtService.getExpiresInSeconds()
        );
    }
}
