package org.example.stationtracker.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "trip_station")
@Getter
@NoArgsConstructor
public class TripStation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "station_id",  nullable = false)
    private Station station;

    @Column(name = "position", nullable = false)
    private Integer position;
    @Column(name = "notified_at")
    private Instant notifiedAt;

    public TripStation(Trip trip, Station station, Integer position) {
        this.trip = trip;
        this.station = station;
        this.position = position;
    }

    public void markNotified() {
        this.notifiedAt = Instant.now();
    }

    public boolean isNotified() {
        return this.notifiedAt != null;
    }
}
