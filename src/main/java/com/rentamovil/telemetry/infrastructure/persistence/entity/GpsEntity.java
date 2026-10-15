package com.rentamovil.telemetry.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Mapeo de telemetry.gps. La tabla la crea rtm-telemetry-gps-db; Hibernate solo valida. */
@Entity
@Table(name = "gps")
@Getter
@Setter
@NoArgsConstructor
public class GpsEntity {

    @Id
    @Column(name = "gps_id")
    private Integer id;

    @Column(name = "serial", nullable = false, length = 100)
    private String serial;

    @Column(name = "model", length = 50)
    private String model;

    @Column(name = "is_active", nullable = false)
    private boolean active;
}
