package com.rentamovil.telemetry.domain.port.in;

import java.util.List;

import com.rentamovil.telemetry.domain.entity.AuthenticatedUser;
import com.rentamovil.telemetry.domain.entity.GpsDevice;

public interface GpsCatalogUseCases {

    /** Dispositivos activos, para que el admin elija uno al registrar el pickup de una renta. */
    List<GpsDevice> listActive(AuthenticatedUser currentUser);
}
