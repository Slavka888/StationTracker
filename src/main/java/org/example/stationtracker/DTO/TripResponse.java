package org.example.stationtracker.DTO;

import org.example.stationtracker.entity.Trip;
import org.example.stationtracker.enums.NotificationType;
import org.example.stationtracker.enums.TripStatus;

import java.time.Instant;
import java.util.List;

public record TripResponse(
    Long id,
    TripStatus status,
    NotificationType notificationType,
    Instant createdAt,
    List<TripStationResponse> stations
) {
    public static TripResponse from(Trip trip) {
        return new TripResponse(
                trip.getId(),
                trip.getTripStatus(),
                trip.getNotificationType(),
                trip.getCreatedAt(),
                trip.getStations()
                        .stream()
                        .map(el -> TripStationResponse.from(el))
                        .toList()
        );
    }
}
