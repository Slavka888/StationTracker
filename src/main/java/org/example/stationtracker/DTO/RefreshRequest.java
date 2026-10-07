package org.example.stationtracker.DTO;

import jakarta.validation.constraints.NotBlank;

public record RefreshRequest (
        @NotBlank
        String refreshToken
) {
}
