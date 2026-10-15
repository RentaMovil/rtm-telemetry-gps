package com.rentamovil.telemetry.domain.port.out;

/**
 * Qué gpsId tiene una renta IN_PROGRESS ahora mismo (Location.INV-001). Se alimenta de
 * RentalStarted/RentalCompleted, no de una tabla — events.md: "en proceso", sin outbox, mismo
 * nivel que el resto de los forwarders del sistema. Se pierde al reiniciar el servicio (aceptable
 * para un MVP de un solo nodo); no está en el alcance de datos de data-model.md (solo gps y
 * location), así que no se persiste como tabla nueva.
 */
public interface ActiveTrackingPort {

    void start(Integer gpsId);

    void stop(Integer gpsId);

    boolean isActive(Integer gpsId);
}
