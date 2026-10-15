package com.rentamovil.telemetry.domain.port.in;

/**
 * Reacciones a los eventos de booking-reservation (events.md -> "Eventos consumidos").
 * At-least-once: aplicar el mismo evento dos veces es naturalmente idempotente (mismo estado
 * final), así que no hace falta deduplicar por eventId.
 */
public interface RentalTrackingUseCases {

    /** RentalStarted: empieza a esperar reportes de ese gpsId (Location.INV-001). */
    void onRentalStarted(Integer gpsId);

    /** RentalCompleted: deja de esperarlos. */
    void onRentalCompleted(Integer gpsId);
}
