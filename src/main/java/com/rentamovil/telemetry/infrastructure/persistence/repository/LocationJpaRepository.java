package com.rentamovil.telemetry.infrastructure.persistence.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.rentamovil.telemetry.infrastructure.persistence.entity.LocationEntity;

/** Acceso Spring Data a telemetry.location. */
public interface LocationJpaRepository extends JpaRepository<LocationEntity, Integer> {

    /**
     * HU-GPS-001: el último punto reportado para ese gps, desde que arrancó la renta (since). Sin
     * el límite inferior, un reporte de una renta anterior con el mismo gpsId reusado podría
     * colarse como si fuera "la última ubicación" de la renta actual.
     */
    @Query("SELECT l FROM LocationEntity l "
            + "WHERE l.gpsId = :gpsId AND l.timestamp >= :since "
            + "ORDER BY l.timestamp DESC LIMIT 1")
    Optional<LocationEntity> findLatest(@Param("gpsId") Integer gpsId, @Param("since") LocalDateTime since);

    /** HU-GPS-002: la ruta completa de una renta, acotada a su ventana [from, to]. */
    @Query("SELECT l FROM LocationEntity l "
            + "WHERE l.gpsId = :gpsId AND l.timestamp BETWEEN :from AND :to "
            + "ORDER BY l.timestamp ASC")
    List<LocationEntity> findRoute(@Param("gpsId") Integer gpsId, @Param("from") LocalDateTime from,
                                   @Param("to") LocalDateTime to);
}
