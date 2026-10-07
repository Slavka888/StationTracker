package org.example.stationtracker.service;

import lombok.extern.slf4j.Slf4j;
import org.example.stationtracker.entity.RefreshToken;
import org.example.stationtracker.entity.User;
import org.example.stationtracker.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;

@Slf4j
@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    @Value("${jwt.refresh-token-ttl:P30D}")
    private Duration refreshTokenTtl;
    private final SecureRandom secureRandom = new SecureRandom();

    @Autowired
    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Transactional
    public GeneratedRefreshToken create(User user){
        String rawToken = generateToken();
        String tokenHash = hash(rawToken);

        RefreshToken refreshToken = new RefreshToken(user, tokenHash, Instant.now().plus(refreshTokenTtl));
        refreshTokenRepository.save(refreshToken);

        return new GeneratedRefreshToken(rawToken, refreshTokenTtl.toSeconds());
    }

    @Transactional
    public RotatedRefreshToken rotates(String rawToken){
        String hash = hash(rawToken);

        RefreshToken token = refreshTokenRepository.findByToken(hash)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token"));

        if (token.isExpired() || token.isRevoked()){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token is expired or revoked");
        }

        User user = token.getUser();
        token.revoke();

        GeneratedRefreshToken newToken = create(user);

        return new RotatedRefreshToken(user, newToken.value(), newToken.expiresIn());
    }

    @Transactional
    public void revokeAll(Long userId) {
        refreshTokenRepository.deleteAllByUserId(userId);
    }

    private String generateToken(){
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }

    private String hash(String rawToken){
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        }
        catch (NoSuchAlgorithmException ex){
            throw new IllegalArgumentException(ex);
        }
    }

    public record GeneratedRefreshToken(String value, long expiresIn) {
    }

    public record RotatedRefreshToken(User user, String value, long expiresIn) {
    }
}
