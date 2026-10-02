package org.example.stationtracker.service;

import lombok.extern.slf4j.Slf4j;
import org.example.stationtracker.DTO.ChangeUserDataRequest;
import org.example.stationtracker.DTO.DeleteAccountRequest;
import org.example.stationtracker.entity.User;
import org.example.stationtracker.repository.TripRepository;
import org.example.stationtracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TripRepository tripRepository;

    @Autowired
    public UserService(UserRepository userRepository, TripRepository tripRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tripRepository = tripRepository;
    }

    @Transactional
    public void updateUser(Long userId, ChangeUserDataRequest changeUserDataRequest) {
        User updatedUser = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        updatedUser.setPassword(
                passwordEncoder.encode(
                        changeUserDataRequest.newPassword()
                )
        );
        updatedUser.setLogin(changeUserDataRequest.newLogin().trim());

        log.info(
                "updated updated user data to user with id={}",
                userId
        );

        userRepository.save(updatedUser);
    }

    @Transactional
    public void deleteUser(DeleteAccountRequest deleteAccountRequest, Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (!passwordEncoder.matches(deleteAccountRequest.password(),  user.getPassword())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Passwords do not match"
            );
        }

        tripRepository.deleteAllByUserId(id);
        userRepository.deleteById(id);
    }
}
