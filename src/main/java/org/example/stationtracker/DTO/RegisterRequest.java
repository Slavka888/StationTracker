package org.example.stationtracker.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank
        @Size(max = 50)
        String login,
        @NotBlank
        @Size(min = 5, max = 50)
        String password
) {
}
