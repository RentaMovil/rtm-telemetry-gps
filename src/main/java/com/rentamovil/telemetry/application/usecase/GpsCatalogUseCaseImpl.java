package com.rentamovil.telemetry.application.usecase;

import java.util.List;

import com.rentamovil.telemetry.domain.entity.AuthenticatedUser;
import com.rentamovil.telemetry.domain.entity.GpsDevice;
import com.rentamovil.telemetry.domain.exception.ForbiddenException;
import com.rentamovil.telemetry.domain.port.in.GpsCatalogUseCases;
import com.rentamovil.telemetry.domain.port.out.GpsRepositoryPort;

/** Mismo permiso que el resto del módulo: quien puede ver tracking puede elegir un dispositivo. */
public class GpsCatalogUseCaseImpl implements GpsCatalogUseCases {

    private static final String TRACKING_PERMISSION = "tracking:read";

    private final GpsRepositoryPort gpsRepository;

    public GpsCatalogUseCaseImpl(GpsRepositoryPort gpsRepository) {
        this.gpsRepository = gpsRepository;
    }

    @Override
    public List<GpsDevice> listActive(AuthenticatedUser currentUser) {
        if (!currentUser.hasPermission(TRACKING_PERMISSION)) {
            throw new ForbiddenException("Necesitas permiso para ver el catálogo de dispositivos GPS");
        }
        return gpsRepository.findAllActive();
    }
}
