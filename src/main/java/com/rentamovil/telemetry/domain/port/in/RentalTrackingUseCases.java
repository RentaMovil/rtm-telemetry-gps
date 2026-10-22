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

    /**
     * Reconstruye el tracking activo preguntándole a booking-reservation qué rentas siguen
     * IN_PROGRESS -- ActiveTrackingPort vive en memoria y no sobrevive un reinicio de este
     * servicio, así que sin esto cada reinicio deja todos los GPS en curso rechazando /locations
     * hasta el próximo RentalStarted real (ver InMemoryActiveTrackingAdapter).
     */
    void reconcileActiveRentals();
}
