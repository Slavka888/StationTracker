package org.example.stationtracker.service;

import lombok.extern.slf4j.Slf4j;
import org.example.stationtracker.entity.User;
import org.example.stationtracker.entity.UserDevice;
import org.example.stationtracker.repository.UserDeviceRepository;
import org.example.stationtracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
public class DeviceService {
    private final UserDeviceRepository userDeviceRepository;
    private final UserRepository userRepository;

    @Autowired
    public DeviceService(UserDeviceRepository userDeviceRepository, UserRepository userRepository) {
        this.userDeviceRepository = userDeviceRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void register(Long userId, String firebaseInstallationId) {
        User user =  userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Optional<UserDevice> existing = userDeviceRepository.findByFirebaseInstallationId(firebaseInstallationId);

        if (existing.isPresent()) {
            existing.get().refresh();
            return;
        }

        userDeviceRepository.save(new UserDevice(user, firebaseInstallationId));

        log.info(
                "Device registered to user: userId = {}",
                userId
        );
    }
}
