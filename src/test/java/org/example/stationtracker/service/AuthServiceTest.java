package org.example.stationtracker.service;

import org.example.stationtracker.DTO.AuthResponse;
import org.example.stationtracker.DTO.LoginRequest;
import org.example.stationtracker.DTO.RegisterRequest;
import org.example.stationtracker.entity.User;
import org.example.stationtracker.repository.UserRepository;
import org.example.stationtracker.security.JWTService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JWTService jwtService;
    @Mock
    private RefreshTokenService refreshTokenService;
    @InjectMocks
    private AuthService authService;

    @Test
    void shouldRegisterUser() {
        RegisterRequest registerRequest = new RegisterRequest("test-user", "password");

        when(
                userRepository.existsUserByLogin("test-user")
        ).thenReturn(false);

        when(
                passwordEncoder.encode("password")
        ).thenReturn("{bcrypt}hash");

        when(
                jwtService.generateAccessToken(any(User.class))
        ).thenReturn("access-token");

        when(
                jwtService.getExpiresInSeconds()
        ).thenReturn(3600L);

        when(
                refreshTokenService.create(any(User.class))
        ).thenReturn(new RefreshTokenService.GeneratedRefreshToken("refresh-token", 259200L));

        AuthResponse authResponse = authService.register(registerRequest);

        assertThat(authResponse.accessToken()).isEqualTo("access-token");

        assertThat(authResponse.refreshToken()).isEqualTo("refresh-token");
        assertThat(authResponse.tokenType()).isEqualTo("Bearer");

        verify(passwordEncoder).encode("password");
        verify(userRepository).save(any(User.class));

    }

    @Test
    void shouldRejectDuplicateLogin() {

        when(
                userRepository.existsUserByLogin("test-user")
        ).thenReturn(true);


        ResponseStatusException exception = catchThrowableOfType(
                () -> authService.register(new RegisterRequest("test-user","password")), ResponseStatusException.class
        );

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

        verify(
                userRepository,
                never()
        ).save(any());
    }
    

    @Test
    void shouldRejectInvalidCredentials() {

        when(
                authenticationManager.authenticate(any(org.springframework.security.core.Authentication.class))
        ).thenThrow(new BadCredentialsException("Bad credentials"));


        ResponseStatusException exception = catchThrowableOfType(
                () -> authService.login(new LoginRequest("test-user","wrong-password")), ResponseStatusException.class
        );


        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

        verify(
                userRepository,
                never()
        ).findUserByLogin(anyString());
    }


}
