package org.example.stationtracker.DTO;

public record AuthResponse(
        String accessToken,
        String tokenType,
        Long expiresIn
) {
}
