package com.rentamovil.telemetry.infrastructure.web.dto;

import java.util.Map;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Evento que booking-reservation le envía a este servicio por POST /internal/events.
 *
 * <pre>
 * {
 *   "eventId": "7f1c…",               (opcional: solo para trazabilidad en los logs)
 *   "eventType": "RentalStarted",
 *   "payload": { "rentalId": 12, "reservationId": 7, "vehicleId": 3, "gpsId": 2 }
 * }
 * </pre>
 *
 * Mismo sobre que usan todos los servicios (fleet, booking, payment incluidos).
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InternalEventRequest {

    private String eventId;

    @NotBlank(message = "obligatorio")
    private String eventType;

    @NotNull(message = "obligatorio")
    private Map<String, Object> payload;
}
