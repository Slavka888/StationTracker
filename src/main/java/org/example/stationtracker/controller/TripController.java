package org.example.stationtracker.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(
        name = "Trips",
        description = "Trip creation, lifecycle, tracking and history"
)
@SecurityRequirement(name = "bearerAuth")
public class TripController {
    private final TripService tripService;

    @Autowired
    public TripController(TripService tripService) {
        this.tripService = tripService;
    }

    @Operation(
            summary = "Get trip history",
            description = """
                    Returns completed and cancelled trips
                    of the authenticated user.
                    """
    )
    @GetMapping("/history")
    public Page<TripResponse> findTripByUserId(@AuthenticationPrincipal Jwt jwt, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Long userId =  jwt.getClaim("userId");
        return tripService.getTripHistory(userId, page, size);
    }

    @Operation(
            summary = "Get trip",
            description = """
                    Returns a trip owned by the authenticated user.
                    """
    )
    @GetMapping("/{tripId}")
    public ResponseEntity<TripResponse> getTrip(@PathVariable("tripId") Long tripId, @AuthenticationPrincipal Jwt jwt) {
        Long userId =  jwt.getClaim("userId");
        return ResponseEntity.ok(tripService.getTrip(tripId, userId));
    }

    @Operation(
            summary = "Get current trip",
            description = """
                    Returns the current ACTIVE or CREATED trip.
                    Returns 204 if the user has no unfinished trip.
                    """
    )
    @GetMapping("/current")
    public ResponseEntity<TripResponse> getCurrentTrip(@AuthenticationPrincipal Jwt jwt) {
        Long userId = jwt.getClaim("userId");

        return tripService
                .getCurrentTrip(userId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @Operation(
            summary = "Create trip",
            description = """
                    Creates a CREATED trip from an ordered
                    collection of metro stations.

                    A user can have only one unfinished
                    CREATED/ACTIVE trip.
                    """
    )
    @PostMapping()
    public ResponseEntity<TripResponse> addTrip(@Valid @RequestBody TripRequest tripRequest, @AuthenticationPrincipal Jwt jwt) {
        Trip trip = tripService.createTrip(tripRequest, jwt.getClaim("userId"));

        TripResponse tripResponse = TripResponse.from(trip);
        return ResponseEntity.status(HttpStatus.CREATED).body(tripResponse);
    }

    @Operation(
            summary = "Mark station as notified",
            description = """
                    Marks a station of an ACTIVE trip
                    as already notified.
                    The operation is idempotent.
                    """
    )
    @PatchMapping("/{tripId}/stations/{tripStationId}")
    public ResponseEntity<Void> markStationNotified(@PathVariable Long tripId, @PathVariable Long tripStationId, @AuthenticationPrincipal Jwt jwt) {
        Long userId =  jwt.getClaim("userId");
        tripService.markStationNotified(tripId, tripStationId, userId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Start trip",
            description = """
                    Changes trip state from CREATED to ACTIVE.
                    """
    )
    @PatchMapping("/{tripId}/start")
    public ResponseEntity<TripResponse> startTrip(@PathVariable Long tripId, @AuthenticationPrincipal Jwt jwt) {
        Long userId =  jwt.getClaim("userId");
        TripResponse tripResponse = tripService.startTrip(tripId, userId);
        return ResponseEntity.status(HttpStatus.OK).body(tripResponse);
    }

    @Operation(
            summary = "Finish trip",
            description = """
                    Changes trip state from ACTIVE to COMPLETED.
                    """
    )
    @PatchMapping("/{tripId}/finish")
    public ResponseEntity<TripResponse> finishTrip(@PathVariable Long tripId, @AuthenticationPrincipal Jwt jwt) {
        Long userId = jwt.getClaim("userId");
        TripResponse tripResponse = tripService.finishTrip(tripId, userId);
        return ResponseEntity.status(HttpStatus.OK).body(tripResponse);
    }

    @Operation(
            summary = "Cancel trip",
            description = """
                    Cancels a CREATED or ACTIVE trip.
                    """
    )
    @PatchMapping("/{tripId}/cancel")
    public ResponseEntity<TripResponse> cancelTrip(@PathVariable Long tripId, @AuthenticationPrincipal Jwt jwt) {
        Long userId = jwt.getClaim("userId");
        TripResponse tripResponse = tripService.cancelTrip(tripId, userId);
        return ResponseEntity.status(HttpStatus.OK).body(tripResponse);
    }

    @Operation(
            summary = "Delete trip from history"
    )
    @DeleteMapping("/history/{tripId}")
    public ResponseEntity<Void> deleteTrip(@AuthenticationPrincipal Jwt jwt, @PathVariable Long tripId) {
        Long userId =  jwt.getClaim("userId");
        tripService.deleteTrip(userId, tripId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Clear trip history",
            description = """
                    Deletes all COMPLETED and CANCELLED trips
                    belonging to the authenticated user.
                    """
    )
    @DeleteMapping("/history")
    public ResponseEntity<Void> deleteAllTrips(@AuthenticationPrincipal Jwt jwt) {
        tripService.deleteAllTrips(jwt.getClaim("userId"));
        return ResponseEntity.noContent().build();
    }
}
