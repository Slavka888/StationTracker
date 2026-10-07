package org.example.stationtracker.DTO;

import org.example.stationtracker.entity.TripStation;

import java.math.BigDecimal;

public record TripStationResponse(
        Long id,
        Long stationId,
        boolean notified,
        Integer position,
        String stationName,
        String line,
        BigDecimal latitude,
        BigDecimal longitude
) {
    public static TripStationResponse from(TripStation tripStation) {
        return new TripStationResponse(
                tripStation.getId(),
                tripStation.getStation().getId(),
                tripStation.isNotified(),
                tripStation.getPosition(),
                tripStation.getStation().getName(),
                tripStation.getStation().getLine(),
                tripStation.getStation().getLatitude(),
                tripStation.getStation().getLongitude()
        );
    }
}
