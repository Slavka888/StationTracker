package org.example.stationtracker.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.example.stationtracker.DTO.AuthResponse;
import org.example.stationtracker.DTO.LoginRequest;
import org.example.stationtracker.DTO.RefreshRequest;
import org.example.stationtracker.DTO.RegisterRequest;
import org.example.stationtracker.entity.User;
import org.example.stationtracker.repository.RefreshTokenRepository;
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

@Slf4j
@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final RefreshTokenService refreshTokenService;

    @Autowired
    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JWTService jwtService,
            RefreshTokenService refreshTokenService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest registerRequest) {
        String login = registerRequest.login();

        if (userRepository.existsUserByLogin(login)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User with this login already exists");
        }

        String password = passwordEncoder.encode(registerRequest.password());
        User user = new User(login, password);
        userRepository.save(user);

        AuthResponse response = createAuthResponse(user);

        log.info(
                "New user with login={} registered",
                login
        );
        return response;
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
            log.error("Authentication Exception", e);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid login or password");
        }

        User user = userRepository.findUserByLogin(loginRequest.login())
                .orElseThrow();

        AuthResponse response = createAuthResponse(user);

        log.info(
                "User with login={} logged in",
                loginRequest.login()
        );
        return response;
    }

    private AuthResponse createAuthResponse(User user) {
        String accessToken = jwtService.generateAccessToken(user);
        RefreshTokenService.GeneratedRefreshToken refreshToken = refreshTokenService.create(user);
        return new AuthResponse(
                accessToken,
                refreshToken.value(),
                "Bearer",
                jwtService.getExpiresInSeconds(),
                refreshToken.expiresIn()
        );
    }

    @Transactional
    public AuthResponse refreshToken(RefreshRequest refreshRequest) {
        RefreshTokenService.RotatedRefreshToken rotatedRefreshToken = refreshTokenService.rotates(refreshRequest.refreshToken());

        String accessToken = jwtService.generateAccessToken(rotatedRefreshToken.user());

        log.info(
                "Access token refreshed: userId={}",
                rotatedRefreshToken.user().getId()
        );

        return new AuthResponse(
                accessToken,
                rotatedRefreshToken.value(),
                "Bearer",
                jwtService.getExpiresInSeconds(),
                rotatedRefreshToken.expiresIn()
        );
    }
}
