package org.example.stationtracker.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "user_devices")
@NoArgsConstructor
@Getter
public class UserDevice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "firebase_installation_id", nullable = false, length = 255)
    private String firebaseInstallationId;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled;

    public UserDevice(User user, String firebaseInstallationId) {
        this.user = user;
        this.firebaseInstallationId = firebaseInstallationId;
        this.updatedAt = Instant.now();
        this.enabled = true;
    }

    public void refresh() {
        this.updatedAt = Instant.now();
        this.enabled = true;
    }
}
