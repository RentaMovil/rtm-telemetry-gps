package com.rentamovil.telemetry.infrastructure.web.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rentamovil.telemetry.domain.port.in.RentalTrackingUseCases;
import com.rentamovil.telemetry.infrastructure.web.dto.InternalEventPayload;
import com.rentamovil.telemetry.infrastructure.web.dto.InternalEventRequest;
import com.rentamovil.telemetry.infrastructure.web.dto.InternalEventResponse;
import com.rentamovil.telemetry.infrastructure.web.exception.InvalidEventException;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Adaptador de entrada para los eventos de booking-reservation (events.md -> "Eventos
 * consumidos"). Solo traduce el mensaje y delega en RentalTrackingUseCases.
 *
 * <p><b>Sin JWT a propósito</b>, igual que /internal/reservations de booking: lo llama otro
 * servicio, no una persona. El api-gateway no enruta /internal/**, así que no es alcanzable
 * desde afuera.
 */
@Slf4j
@RestController
@RequestMapping("/internal/events")
@RequiredArgsConstructor
public class InternalEventController {

    private final RentalTrackingUseCases rentalTracking;

    @PostMapping
    public InternalEventResponse receive(@Valid @RequestBody InternalEventRequest request) {
        InternalEventPayload payload = new InternalEventPayload(request.getPayload());

        switch (request.getEventType()) {
            case "RentalStarted" -> rentalTracking.onRentalStarted(payload.requiredInt("gpsId"));
            case "RentalCompleted" -> rentalTracking.onRentalCompleted(payload.requiredInt("gpsId"));
            default -> throw new InvalidEventException("UNKNOWN_EVENT_TYPE", "eventType",
                    "Tipo de evento desconocido: " + request.getEventType());
        }

        log.info("Evento {} (eventId={}): {}", request.getEventType(), request.getEventId(),
                InternalEventResponse.PROCESSED);
        return new InternalEventResponse(request.getEventType(), request.getEventId(),
                InternalEventResponse.PROCESSED);
    }
}
