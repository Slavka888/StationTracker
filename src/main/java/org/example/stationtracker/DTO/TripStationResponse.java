package org.example.stationtracker.DTO;

import org.example.stationtracker.entity.TripStation;

public record TripStationResponse(
        Long id,
        Long stationId,
        boolean notified,
        Integer position,
        String stationName
) {
    public static TripStationResponse from(TripStation tripStation) {
        return new TripStationResponse(
                tripStation.getId(),
                tripStation.getStation().getId(),
                tripStation.isNotified(),
                tripStation.getPosition(),
                tripStation.getStation().getName()
        );
    }
}
