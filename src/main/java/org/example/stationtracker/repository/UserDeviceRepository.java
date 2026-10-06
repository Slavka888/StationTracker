package org.example.stationtracker.repository;

import org.example.stationtracker.entity.UserDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserDeviceRepository extends JpaRepository<UserDevice, Long> {
    Optional<UserDevice> findByFirebaseInstallationId(String firebaseInstallationId);
    List<UserDevice> findAllByUserIdAndEnabledTrue(Long userId);
}
