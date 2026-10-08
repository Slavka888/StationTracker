package org.example.stationtracker.controller;

import jakarta.validation.Valid;
import org.example.stationtracker.DTO.TripRequest;
import org.example.stationtracker.DTO.TripResponse;
import org.example.stationtracker.entity.Trip;
import org.example.stationtracker.service.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/trips")
public class TripController {
    private final TripService tripService;

    @Autowired
    public TripController(TripService tripService) {
        this.tripService = tripService;
    }

    @GetMapping("/history")
    public Page<TripResponse> findTripByUserId(@AuthenticationPrincipal Jwt jwt, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Long userId =  jwt.getClaim("userId");
        return tripService.getTripHistory(userId, page, size);
    }

    @GetMapping("/{tripId}")
    public ResponseEntity<TripResponse> getTrip(@PathVariable("tripId") Long tripId, @AuthenticationPrincipal Jwt jwt) {
        Long userId =  jwt.getClaim("userId");
        return ResponseEntity.ok(tripService.getTrip(tripId, userId));
    }

    @GetMapping("/current")
    public ResponseEntity<TripResponse> getCurrentTrip(@AuthenticationPrincipal Jwt jwt) {
        Long userId = jwt.getClaim("userId");

        return tripService
                .getCurrentTrip(userId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PostMapping()
    public ResponseEntity<TripResponse> addTrip(@Valid @RequestBody TripRequest tripRequest, @AuthenticationPrincipal Jwt jwt) {
        Trip trip = tripService.createTrip(tripRequest, jwt.getClaim("userId"));

        TripResponse tripResponse = TripResponse.from(trip);
        return ResponseEntity.status(HttpStatus.CREATED).body(tripResponse);
    }

    @PatchMapping("/{tripId}/stations/{tripStationId}")
    public ResponseEntity<Void> markStationNotified(@PathVariable Long tripId, @PathVariable Long tripStationId, @AuthenticationPrincipal Jwt jwt) {
        Long userId =  jwt.getClaim("userId");
        tripService.markStationNotified(tripId, tripStationId, userId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{tripId}/start")
    public ResponseEntity<TripResponse> startTrip(@PathVariable Long tripId, @AuthenticationPrincipal Jwt jwt) {
        Long userId =  jwt.getClaim("userId");
        TripResponse tripResponse = tripService.startTrip(tripId, userId);
        return ResponseEntity.status(HttpStatus.OK).body(tripResponse);
    }

    @PatchMapping("/{tripId}/finish")
    public ResponseEntity<TripResponse> finishTrip(@PathVariable Long tripId, @AuthenticationPrincipal Jwt jwt) {
        Long userId = jwt.getClaim("userId");
        TripResponse tripResponse = tripService.finishTrip(tripId, userId);
        return ResponseEntity.status(HttpStatus.OK).body(tripResponse);
    }

    @PatchMapping("/{tripId}/cancel")
    public ResponseEntity<TripResponse> cancelTrip(@PathVariable Long tripId, @AuthenticationPrincipal Jwt jwt) {
        Long userId = jwt.getClaim("userId");
        TripResponse tripResponse = tripService.cancelTrip(tripId, userId);
        return ResponseEntity.status(HttpStatus.OK).body(tripResponse);
    }

    @DeleteMapping("/history/{tripId}")
    public ResponseEntity<Void> deleteTrip(@AuthenticationPrincipal Jwt jwt, @PathVariable Long tripId) {
        Long userId =  jwt.getClaim("userId");
        tripService.deleteTrip(userId, tripId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/history")
    public ResponseEntity<Void> deleteAllTrips(@AuthenticationPrincipal Jwt jwt) {
        tripService.deleteAllTrips(jwt.getClaim("userId"));
        return ResponseEntity.noContent().build();
    }
}
