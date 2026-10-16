package com.rentamovil.telemetry.domain.port.out;

import java.util.List;

import com.rentamovil.telemetry.domain.entity.GpsDevice;

public interface GpsRepositoryPort {

    /** Para rechazar con 404 un reporte de un gpsId que no corresponde a ningún dispositivo. */
    boolean existsById(Integer gpsId);

    /** Catálogo de dispositivos activos, para que el admin elija uno al registrar un pickup. */
    List<GpsDevice> findAllActive();
}
