package com.rentamovil.telemetry.infrastructure.persistence.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Mapeo de telemetry.location. La tabla la crea rtm-telemetry-gps-db; Hibernate solo valida. */
@Entity
@Table(name = "location")
@Getter
@Setter
@NoArgsConstructor
public class LocationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "location_id")
    private Integer id;

    @Column(name = "gps_id", nullable = false)
    private Integer gpsId;

    @Column(name = "latitude", nullable = false, precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(name = "longitude", nullable = false, precision = 11, scale = 8)
    private BigDecimal longitude;

    // "timestamp" es palabra reservada en SQL Server (001_create_table_location.sql la crea como
    // [timestamp]). Las comillas invertidas le dicen a Hibernate que la cite en cada consulta
    // (SELECT/INSERT), no solo al validar el esquema al arrancar -- sin esto fallaría en runtime.
    @Column(name = "`timestamp`", nullable = false)
    private LocalDateTime timestamp;
}
