package org.example.stationtracker.service;

import lombok.extern.slf4j.Slf4j;
import org.example.stationtracker.DTO.TripRequest;
import org.example.stationtracker.DTO.TripResponse;
import org.example.stationtracker.DTO.TripStartedEvent;
import org.example.stationtracker.entity.Station;
import org.example.stationtracker.entity.Trip;
import org.example.stationtracker.entity.TripStation;
import org.example.stationtracker.entity.User;
import org.example.stationtracker.enums.TripStatus;
import org.example.stationtracker.repository.StationRepository;
import org.example.stationtracker.repository.TripRepository;
import org.example.stationtracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TripService {
    private final TripRepository tripRepository;
    private final UserRepository userRepository;
    private final StationRepository stationRepository;
    private final ApplicationEventPublisher publisher;

    @Autowired
    public TripService(TripRepository tripRepository, StationRepository stationRepository, UserRepository userRepository, ApplicationEventPublisher publisher) {
        this.tripRepository = tripRepository;
        this.stationRepository = stationRepository;
        this.userRepository = userRepository;
        this.publisher = publisher;
    }

    @Transactional
    public Trip createTrip(TripRequest tripRequest, Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        List<Long> stationIds = tripRequest.stationIds();
        List<Station> foundStations = stationRepository.findAllById(stationIds);

        if (tripRepository.existsByUserIdAndTripStatusIn(id, List.of(TripStatus.CREATED, TripStatus.ACTIVE))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Trip for user already exists");
        }

        if (foundStations.size() != stationIds.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "One or more stations do not exist");
        }

        Map<Long, Station> stationsById = foundStations
                .stream()
                .collect(Collectors.toMap(
                        station -> station.getId(),
                        station -> station
                ));

        Trip trip = new Trip(user, tripRequest.notificationType());

        for (Long stationId : stationIds) {
            Station station = stationsById.get(stationId);
            trip.addStation(station);
        }

        Trip savedTrip = tripRepository.save(trip);

        log.info(
                "Trip created: tripId = {}, userId = {}, stationCount = {}",
                savedTrip.getId(),
                user.getId(),
                stationIds.size()
        );

        return savedTrip;
    }

    @Transactional(readOnly = true)
    public Optional<TripResponse> getCurrentTrip(Long userId) {
        Optional<Trip> activeTrip = tripRepository.findFirstByUserIdAndTripStatusOrderByCreatedAtDesc(userId,TripStatus.ACTIVE);

        if (activeTrip.isPresent()) {
            return activeTrip.map(TripResponse::from);
        }

        return tripRepository.findFirstByUserIdAndTripStatusOrderByCreatedAtDesc(userId,TripStatus.CREATED)
                .map(TripResponse::from);
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public Page<TripResponse> getTripHistory(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Trip> trips = tripRepository.findAllByUserIdAndTripStatusIn(userId, List.of(TripStatus.COMPLETED, TripStatus.CANCELLED), pageable);

        log.info(
                "Returned trip history to userId = {}",
                userId
        );

        return trips.map(trip -> TripResponse.from(trip));
    }

    @Transactional(readOnly = true)
    public TripResponse getTrip(Long tripId, Long userId) {
        Trip trip = tripRepository.findByIdAndUserId(tripId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Trip not found"));
        return TripResponse.from(trip);
    }

    @Transactional
    public void deleteTrip(Long userId, Long tripId) {
        tripRepository.deleteByIdAndUserIdAndTripStatusIn(tripId, userId, List.of(TripStatus.COMPLETED, TripStatus.CANCELLED));
        log.info(
                "Trip deleted from history: tripId={}, userId={}",
                tripId,
                userId
        );
    }

    @Transactional
    public void deleteAllTrips(Long userId) {
        tripRepository.deleteAllByUserIdAndTripStatusIn(userId, List.of(TripStatus.CANCELLED, TripStatus.COMPLETED));
        log.info(
                "All history cleared: userId={}",
                userId
        );
    }

    @Transactional
    public TripResponse startTrip(Long tripId, Long userId) {
        Trip trip = tripRepository.findByIdAndUserId(tripId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Trip not found"));
        trip.markStarted();

        publisher.publishEvent(new TripStartedEvent(tripId, userId));

        log.info(
                "Trip started: tripId={}, userId={}",
                tripId,
                userId
        );
        return TripResponse.from(trip);
    }

    @Transactional
    public TripResponse finishTrip(Long tripId, Long userId) {
        Trip trip = tripRepository.findByIdAndUserId(tripId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Trip not found"));
        trip.markFinished();
        log.info(
                "Trip finished: tripId={}, userId={}",
                tripId,
                userId
        );
        return TripResponse.from(trip);
    }

    @Transactional
    public TripResponse cancelTrip(Long tripId, Long userId) {
        //логика оповещения устройства об отмене
        Trip trip = tripRepository.findByIdAndUserId(tripId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Trip not found"));
        trip.markCancelled();
        log.info(
                "Trip cancelled: tripId={}, userId={}",
                tripId,
                userId
        );
        return TripResponse.from(trip);
    }

    @Transactional
    public void markStationNotified(Long tripId, Long tripStationId, Long userId) {
        Trip trip = tripRepository.findByIdAndUserId(tripId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Trip not found"));

        if (!trip.getTripStatus().equals(TripStatus.ACTIVE)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Trip status must be ACTIVE");
        }

        TripStation tripStation = trip.getStations()
                .stream()
                .filter(station -> station.getId().equals(tripStationId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Trip station not found"));

        if (tripStation.isNotified()) {
            return;
        }

        tripStation.markNotified();

        log.info(
                "Trip station marked as notified: tripId={}, tripStationId={}, userId={}",
                tripId,
                tripStationId,
                userId
        );
    }
}
