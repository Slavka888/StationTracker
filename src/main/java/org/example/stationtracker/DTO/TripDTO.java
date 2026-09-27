package org.example.stationtracker.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripDTO {
    private String userName;
    private String station;
    private Instant endedAt;
    private Instant notifiedAt;
}
