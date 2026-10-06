package org.example.stationtracker.controller;

import jakarta.validation.Valid;
import org.example.stationtracker.DTO.DeviceRegistrationRequest;
import org.example.stationtracker.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/devices")
public class DeviceController {
    private final DeviceService deviceService;

    @Autowired
    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @PutMapping
    public ResponseEntity<Void> registerDevice(@Valid @RequestBody DeviceRegistrationRequest deviceRegistrationRequest, @AuthenticationPrincipal Jwt jwt) {
        Long userId = jwt.getClaim("userId");

        deviceService.register(userId, deviceRegistrationRequest.firebaseInstallationId());

        return ResponseEntity.noContent().build();
    }
}
