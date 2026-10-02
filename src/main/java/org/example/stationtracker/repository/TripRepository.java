package org.example.stationtracker.repository;

import org.example.stationtracker.entity.Trip;
import org.example.stationtracker.enums.TripStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {
    Page<Trip> findAllByUserIdAndTripStatusIn(Long userId, Collection<TripStatus> status, Pageable pageable);
    void deleteAllByUserIdAndTripStatusIn(Long user_id, Collection<TripStatus> tripStatus);
    void deleteByIdAndUserId(Long tripId, Long userId);
}
