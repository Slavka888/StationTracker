package org.example.stationtracker.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "destionation_station_id", nullable = false)
    private Station destinationStation;

    private Instant startedAt;
    private Instant endedAt;
    private Instant notifiedAt;

    public Trip(User user, Station destinationStation) {
        this.user = user;
        this.destinationStation = destinationStation;
        this.startedAt = Instant.now();
    }

    public void markNotified() {
        this.notifiedAt = Instant.now();
    }

    public void markFinished() {
        this.endedAt = Instant.now();
    }
}
