package org.example.stationtracker.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(
        name = "Devices",
        description = "Mobile device registration for Firebase Cloud Messaging"
)
@SecurityRequirement(name = "bearerAuth")
public class DeviceController {
    private final DeviceService deviceService;

    @Autowired
    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @Operation(
            summary = "Register mobile device",
            description = """
                    Associates a Firebase Installation ID
                    with the authenticated user.
                    """
    )
    @PutMapping
    public ResponseEntity<Void> registerDevice(@Valid @RequestBody DeviceRegistrationRequest deviceRegistrationRequest, @AuthenticationPrincipal Jwt jwt) {
        Long userId = jwt.getClaim("userId");

        deviceService.register(userId, deviceRegistrationRequest.firebaseInstallationId());

        return ResponseEntity.noContent().build();
    }
}
