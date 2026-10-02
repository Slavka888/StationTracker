package org.example.stationtracker.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangeUserDataRequest(
        @NotBlank
        String newLogin,
        @NotBlank
        @Size(min = 5, max = 50)
        String newPassword
) {
}
