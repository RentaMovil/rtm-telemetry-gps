package com.rentamovil.telemetry.domain.port.out;

public interface GpsRepositoryPort {

    /** Para rechazar con 404 un reporte de un gpsId que no corresponde a ningún dispositivo. */
    boolean existsById(Integer gpsId);
}
