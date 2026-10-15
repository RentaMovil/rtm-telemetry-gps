package com.rentamovil.telemetry.infrastructure.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rentamovil.telemetry.domain.port.in.LocationUseCases;
import com.rentamovil.telemetry.infrastructure.security.DeviceApiKeyValidator;
import com.rentamovil.telemetry.infrastructure.web.dto.LocationResponse;
import com.rentamovil.telemetry.infrastructure.web.dto.ReportLocationRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Ingesta de ubicación desde los dispositivos GPS físicos. No corresponde a ninguna HU de
 * usuario — el endpoint de mayor volumen de todo el sistema.
 */
@RestController
@RequestMapping("/locations")
@RequiredArgsConstructor
public class LocationController {

    private final LocationUseCases locations;
    private final DeviceApiKeyValidator deviceApiKeyValidator;

    @PostMapping
    public ResponseEntity<LocationResponse> report(@RequestHeader("X-Device-Api-Key") String deviceApiKey,
                                                   @Valid @RequestBody ReportLocationRequest request) {
        deviceApiKeyValidator.validate(deviceApiKey);
        var location = locations.report(request.getGpsId(), request.getLatitude(), request.getLongitude(),
                request.getTimestamp());
        return ResponseEntity.status(HttpStatus.CREATED).body(LocationResponse.from(location));
    }
}
