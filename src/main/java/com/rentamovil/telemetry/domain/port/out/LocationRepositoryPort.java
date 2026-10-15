package com.rentamovil.telemetry.domain.port.out;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.rentamovil.telemetry.domain.entity.Location;

public interface LocationRepositoryPort {

    Location save(Location location);

    /** HU-GPS-001: el último punto reportado para ese gps, desde que arrancó la renta. */
    Optional<Location> findLatest(Integer gpsId, LocalDateTime since);

    /** HU-GPS-002: la ruta completa, en orden cronológico, acotada a la ventana de la renta. */
    List<Location> findRoute(Integer gpsId, LocalDateTime from, LocalDateTime to);
}
