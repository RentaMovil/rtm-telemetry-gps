package com.rentamovil.telemetry.domain.port.out;

import java.time.LocalDateTime;

/**
 * Resuelve rentalId -> gpsId y la ventana de fechas de esa renta, llamando a
 * booking-reservation (GET /internal/rentals/{id}, sin auth — red interna).
 *
 * <p>Solo lo usan las consultas de /tracking/* (HU-GPS-001/002), nunca la ingesta de
 * /locations: un mismo dispositivo físico se reutiliza en rentas distintas con el tiempo, así que
 * "todas las ubicaciones de ese gpsId" no es lo mismo que "las de esta renta" — hace falta acotar
 * también por actualStartDate/actualEndDate.
 */
public interface RentalClientPort {

    RentalSnapshot getRental(Integer rentalId);

    record RentalSnapshot(Integer id, Integer gpsId, LocalDateTime actualStartDate,
                          LocalDateTime actualEndDate) {
    }
}
