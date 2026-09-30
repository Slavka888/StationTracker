package org.example.stationtracker.service;

import jakarta.transaction.Transactional;
import org.example.stationtracker.entity.Trip;
import org.example.stationtracker.repository.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TripService {
    private final TripRepository tripRepository;

    @Autowired
    public TripService(TripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }
}
