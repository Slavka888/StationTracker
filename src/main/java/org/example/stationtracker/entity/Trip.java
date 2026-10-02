package org.example.stationtracker.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.stationtracker.enums.NotificationType;
import org.example.stationtracker.enums.TripStatus;
import org.hibernate.annotations.BatchSize;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Table(name = "trips")
@Entity
@NoArgsConstructor
@Getter
public class Trip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id",  nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TripStatus tripStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false)
    private NotificationType  notificationType;

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("position ASC")
    @BatchSize(size = 20)
    private List<TripStation> stations = new ArrayList<>();

    private Instant createdAt;
    private Instant startedAt;
    private Instant endedAt;

    public Trip(User user, NotificationType notificationType) {
        this.user = user;
        this.createdAt = Instant.now();
        this.notificationType = notificationType;
        this.tripStatus = TripStatus.CREATED;
    }

    public void markFinished() {
        if (this.tripStatus != TripStatus.ACTIVE) {
            throw new IllegalStateException("Trip must be in ACTIVE state");
        }
        this.tripStatus = TripStatus.COMPLETED;
        this.endedAt = Instant.now();
    }

    public void markStarted() {
        if (this.tripStatus != TripStatus.CREATED) {
            throw new IllegalStateException("Trip must be CREATED before marking started");
        }
        this.startedAt = Instant.now();
        this.tripStatus = TripStatus.ACTIVE;
    }

    public void addStation(Station station) {
        TripStation tripStation = new TripStation(
                this,
                station,
                stations.size()
        );

        this.stations.add(tripStation);
    }
}
