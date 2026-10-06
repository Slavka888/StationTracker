package org.example.stationtracker.DTO;

import jakarta.validation.constraints.NotBlank;

public record DeviceRegistrationRequest(
        @NotBlank
        String firebaseInstallationId
) {
}
