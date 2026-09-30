package org.example.stationtracker.security;

import org.example.stationtracker.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
public class JWTService {
    private final JwtEncoder jwtEncoder;
    private final Duration accessTokenTtl;

    public JWTService(JwtEncoder jwtEncoder, @Value("${jwt.access-token-ttl:PT1H}") Duration accessTokenTtl) {
        this.jwtEncoder = jwtEncoder;
        this.accessTokenTtl = accessTokenTtl;
    }

    public String generateAccessToken(User user) {

        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("station-tracker")
                .issuedAt(now)
                .expiresAt(now.plus(accessTokenTtl))
                .subject(user.getLogin())
                .claim("userId", user.getId())
                .build();

        return jwtEncoder
                .encode(
                        JwtEncoderParameters.from(claims)
                )
                .getTokenValue();
    }

    public long getExpiresInSeconds() {
        return accessTokenTtl.toSeconds();
    }
}
