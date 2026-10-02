package org.example.stationtracker.controller;

import jakarta.validation.Valid;
import org.example.stationtracker.DTO.TripRequest;
import org.example.stationtracker.DTO.TripResponse;
import org.example.stationtracker.entity.Trip;
import org.example.stationtracker.service.TripService;
import org.springframework.beans.factory.annotation.Autowired;
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


    @PostMapping()
    public ResponseEntity<TripResponse> addTrip(@Valid @RequestBody TripRequest tripRequest, @AuthenticationPrincipal Jwt jwt) {
        Trip trip = tripService.createTrip(tripRequest, jwt.getClaim("userId"));

        TripResponse tripResponse = TripResponse.from(trip);
        return ResponseEntity.status(HttpStatus.CREATED).body(tripResponse);
    }
}
