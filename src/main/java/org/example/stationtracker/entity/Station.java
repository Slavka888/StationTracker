package org.example.stationtracker.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@Entity
@Getter
@Table(name = "stations")
public class Station {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "line", nullable = false)
    private String line;
    @Column(name = "latitude", precision = 11, scale = 8, nullable = false)
    private BigDecimal latitude;
    @Column(name = "longitude", precision = 11, scale = 8, nullable = false)
    private BigDecimal longitude;
}
