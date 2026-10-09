package org.example.stationtracker.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(
        name = "Users",
        description = "User account management"
)
@SecurityRequirement(name = "bearerAuth")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    @Operation(
            summary = "Update account",
            description = """
                    Changes login and password of the
                    authenticated user.
                    """
    )
    @PutMapping("/update")
    public ResponseEntity<Void> updateUser(@Valid @RequestBody ChangeUserDataRequest user, @AuthenticationPrincipal Jwt jwt) {
        Long userId = jwt.getClaim("userId");
        userService.updateUser(userId, user);

        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Delete account",
            description = """
                    Deletes the authenticated user and
                    associated application data.
                    Password confirmation is required.
                    """
    )
    @DeleteMapping()
    public ResponseEntity<Void> deleteUser(@Valid @RequestBody DeleteAccountRequest deleteAccountRequest, @AuthenticationPrincipal Jwt jwt) {
        Long userId = jwt.getClaim("userId");
        userService.deleteUser(deleteAccountRequest, userId);
        return ResponseEntity.noContent().build();
    }
}
