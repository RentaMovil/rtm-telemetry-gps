package com.rentamovil.telemetry.domain.entity;

import lombok.Getter;

/** Un dispositivo físico de la tabla telemetry.gps. Catálogo de solo lectura: se carga por seed. */
@Getter
public class GpsDevice {

    private final Integer id;
    private final String serial;
    private final String model;
    private final boolean active;

    private GpsDevice(Integer id, String serial, String model, boolean active) {
        this.id = id;
        this.serial = serial;
        this.model = model;
        this.active = active;
    }

    public static GpsDevice restore(Integer id, String serial, String model, boolean active) {
        return new GpsDevice(id, serial, model, active);
    }
}
