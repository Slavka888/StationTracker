package org.example.stationtracker.DTO;

import jakarta.validation.constraints.NotBlank;
import org.example.stationtracker.enums.NotificationType;

import java.util.List;

public record TripRequest(
        @NotBlank
        List<Long> stationIds,
        @NotBlank
        NotificationType notificationType
) {
}
