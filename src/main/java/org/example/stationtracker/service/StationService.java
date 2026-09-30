package org.example.stationtracker.service;

import jakarta.transaction.Transactional;
import org.example.stationtracker.entity.Station;
import org.example.stationtracker.repository.StationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StationService {
    private final StationRepository stationRepository;

    @Autowired
    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    @Transactional
    public Station findStationById(Long id) {
        return stationRepository.findStationById(id)
                .orElseThrow(() -> new IllegalArgumentException("Station not found"));
    }
}
