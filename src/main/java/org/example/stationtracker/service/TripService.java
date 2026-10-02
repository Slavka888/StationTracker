package org.example.stationtracker.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.example.stationtracker.DTO.TripRequest;
import org.example.stationtracker.entity.Station;
import org.example.stationtracker.entity.Trip;
import org.example.stationtracker.entity.User;
import org.example.stationtracker.repository.StationRepository;
import org.example.stationtracker.repository.TripRepository;
import org.example.stationtracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TripService {
    private final TripRepository tripRepository;
    private final UserRepository userRepository;
    private final StationRepository stationRepository;

    @Autowired
    public TripService(TripRepository tripRepository, StationRepository stationRepository, UserRepository userRepository) {
        this.tripRepository = tripRepository;
        this.stationRepository =  stationRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Trip createTrip(TripRequest tripRequest, Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        List<Long> stationIds = tripRequest.stationIds();
        List<Station> foundStations = stationRepository.findAllById(stationIds);


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

        Trip savedTrip =  tripRepository.save(trip);

        log.info(
                "Trip created: tripId = {}, userId = {}, stationCount = {}",
                savedTrip.getId(),
                user.getId(),
                stationIds.size()
        );

        return savedTrip;
    }
}
