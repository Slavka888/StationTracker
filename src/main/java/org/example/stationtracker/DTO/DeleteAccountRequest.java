package org.example.stationtracker.DTO;

import jakarta.validation.constraints.NotBlank;

public record DeleteAccountRequest(
        @NotBlank
        String password
) {
}
