package com.rentamovil.telemetry.domain.port.in;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.rentamovil.telemetry.domain.entity.AuthenticatedUser;
import com.rentamovil.telemetry.domain.entity.Location;

public interface LocationUseCases {

    /**
     * Ingesta de un dispositivo GPS. Sin AuthenticatedUser: se autentica con API key de
     * dispositivo, no con JWT (CurrentUser no aplica acá).
     */
    Location report(Integer gpsId, BigDecimal latitude, BigDecimal longitude, LocalDateTime timestamp);

    /** HU-GPS-001. Exclusivo tracking:read (Location.INV-002, sin excepción para el propio cliente). */
    Location getLive(AuthenticatedUser currentUser, Integer rentalId);

    /** HU-GPS-002. Lista vacía (no error) si el GPS falló y no quedó ningún punto registrado. */
    List<Location> getHistory(AuthenticatedUser currentUser, Integer rentalId);
}
