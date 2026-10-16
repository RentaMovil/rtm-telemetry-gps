package com.rentamovil.telemetry.infrastructure.web.dto;

import com.rentamovil.telemetry.domain.entity.GpsDevice;

import lombok.Getter;

/** Respuesta de GET /gps: catálogo de dispositivos para el selector del admin. */
@Getter
public class GpsDeviceResponse {

    private final Integer id;
    private final String serial;
    private final String model;
    private final boolean active;

    private GpsDeviceResponse(Integer id, String serial, String model, boolean active) {
        this.id = id;
        this.serial = serial;
        this.model = model;
        this.active = active;
    }

    public static GpsDeviceResponse from(GpsDevice device) {
        return new GpsDeviceResponse(device.getId(), device.getSerial(), device.getModel(), device.isActive());
    }
}
