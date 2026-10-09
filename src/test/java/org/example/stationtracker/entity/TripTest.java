package org.example.stationtracker.entity;

import org.example.stationtracker.enums.NotificationType;
import org.example.stationtracker.enums.TripStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class TripTest {
    @Test
    void newTripShouldBeCreated() {
        Trip trip = createTrip();
        assertThat(trip.getTripStatus()).isEqualTo(TripStatus.CREATED);
        assertThat(trip.getCreatedAt()).isNotNull();
        assertThat(trip.getStartedAt()).isNotNull();
        assertThat(trip.getEndedAt()).isNotNull();
    }

    @Test
    void createdTripShouldBecomeActive() {

        Trip trip = createTrip();

        trip.markStarted();

        assertThat(trip.getTripStatus()).isEqualTo(TripStatus.ACTIVE);

        assertThat(trip.getStartedAt()).isNotNull();
    }


    @Test
    void activeTripShouldBecomeCompleted() {

        Trip trip = createTrip();

        trip.markStarted();
        trip.markFinished();

        assertThat(trip.getTripStatus()).isEqualTo(TripStatus.COMPLETED);

        assertThat(trip.getEndedAt()).isNotNull();
    }


    @Test
    void createdTripShouldNotBeFinished() {

        Trip trip = createTrip();

        assertThatThrownBy(trip::markFinished).isInstanceOf(IllegalStateException.class);
    }


    @Test
    void completedTripShouldNotBeStartedAgain() {

        Trip trip = createTrip();

        trip.markStarted();
        trip.markFinished();

        assertThatThrownBy(trip::markStarted).isInstanceOf(IllegalStateException.class);
    }


    @Test
    void createdTripShouldBeCancellable() {

        Trip trip = createTrip();

        trip.markCancelled();

        assertThat(trip.getTripStatus()).isEqualTo(TripStatus.CANCELLED);

        assertThat(trip.getEndedAt()).isNotNull();
    }


    @Test
    void activeTripShouldBeCancellable() {

        Trip trip = createTrip();

        trip.markStarted();
        trip.markCancelled();

        assertThat(trip.getTripStatus()).isEqualTo(TripStatus.CANCELLED);
    }


    @Test
    void completedTripShouldNotBeCancellable() {

        Trip trip = createTrip();

        trip.markStarted();
        trip.markFinished();

        assertThatThrownBy(trip::markCancelled).isInstanceOf(IllegalStateException.class);
    }


    @Test
    void addedStationsShouldPreserveOrder() {

        Trip trip = createTrip();

        Station first = new Station();

        Station second = new Station();

        trip.addStation(first);
        trip.addStation(second);

        assertThat(trip.getStations()).hasSize(2);

        assertThat(trip.getStations()
                        .get(0)
                        .getPosition()
        ).isEqualTo(0);

        assertThat(trip.getStations()
                        .get(1)
                        .getPosition()
        ).isEqualTo(1);

        assertThat(trip.getStations()
                        .get(0)
                        .getStation()
        ).isSameAs(first);

        assertThat(trip.getStations()
                        .get(1)
                        .getStation()
        ).isSameAs(second);
    }

    private Trip createTrip() {
        User user = new User("test-user", "password");
        return new Trip(user, NotificationType.STANDARD);
    }
}
