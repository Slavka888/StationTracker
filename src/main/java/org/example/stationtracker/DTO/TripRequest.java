package org.example.stationtracker.DTO;

import jakarta.validation.constraints.NotEmpty;
import org.example.stationtracker.enums.NotificationType;

import java.util.List;

public record TripRequest(
        @NotEmpty
        List<Long> stationIds,
        NotificationType notificationType
) {
}
