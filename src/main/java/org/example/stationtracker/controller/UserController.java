package org.example.stationtracker.controller;

import jakarta.validation.Valid;
import org.example.stationtracker.DTO.ChangeUserDataRequest;
import org.example.stationtracker.DTO.DeleteAccountRequest;
import org.example.stationtracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    @PutMapping("/update")
    public ResponseEntity<Void> updateUser(@Valid @RequestBody ChangeUserDataRequest user, @AuthenticationPrincipal Jwt jwt) {
        Long userId = jwt.getClaim("userId");
        userService.updateUser(userId, user);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping()
    public ResponseEntity<Void> deleteUser(@Valid @RequestBody DeleteAccountRequest deleteAccountRequest, @AuthenticationPrincipal Jwt jwt) {
        Long userId = jwt.getClaim("userId");
        userService.deleteUser(deleteAccountRequest, userId);
        return ResponseEntity.noContent().build();
    }
}
