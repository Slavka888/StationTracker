package org.example.stationtracker.repository;

import org.example.stationtracker.entity.Trip;
import org.example.stationtracker.entity.User;
import org.example.stationtracker.enums.TripStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {
    Page<Trip> findAllByUserIdAndTripStatusIn(Long userId, Collection<TripStatus> status, Pageable pageable);
    void deleteAllByUserIdAndTripStatusIn(Long userId, Collection<TripStatus> tripStatus);
    void deleteByIdAndUserIdAndTripStatusIn(Long id, Long userId, Collection<TripStatus> tripStatus);
    void deleteAllByUserId(Long userId);
    Optional<Trip> findByIdAndUserId(Long tripId, Long userId);
    boolean existsByUserIdAndTripStatusIn(Long userId, Collection<TripStatus> tripStatus);
    Optional<Trip> findFirstByUserIdAndTripStatusOrderByCreatedAtDesc(Long userId, TripStatus tripStatus);
}
