package com.rentamovil.telemetry.infrastructure.web.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rentamovil.telemetry.domain.entity.AuthenticatedUser;
import com.rentamovil.telemetry.domain.port.in.GpsCatalogUseCases;
import com.rentamovil.telemetry.infrastructure.web.auth.CurrentUser;
import com.rentamovil.telemetry.infrastructure.web.dto.GpsDeviceResponse;

import lombok.RequiredArgsConstructor;

/** Catálogo de dispositivos GPS, para elegir uno al registrar el pickup de una renta. */
@RestController
@RequestMapping("/gps")
@RequiredArgsConstructor
public class GpsController {

    private final GpsCatalogUseCases gpsCatalog;

    @GetMapping
    public List<GpsDeviceResponse> listActive(@CurrentUser AuthenticatedUser currentUser) {
        return gpsCatalog.listActive(currentUser).stream().map(GpsDeviceResponse::from).toList();
    }
}
