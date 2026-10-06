package org.example.stationtracker.DTO;

public record TripStartedEvent(
        Long tripId,
        Long userId
) {
}
