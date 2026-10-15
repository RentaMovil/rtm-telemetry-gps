package com.rentamovil.telemetry.infrastructure.web.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.rentamovil.telemetry.domain.entity.Location;

import lombok.Getter;

/** Respuesta de un punto de ubicación (07-api/contracts/openapi/telemetry-gps.yaml -> Location). */
@Getter
public class LocationResponse {

    private final Integer id;
    private final Integer gpsId;
    private final BigDecimal latitude;
    private final BigDecimal longitude;
    private final LocalDateTime timestamp;

    private LocationResponse(Integer id, Integer gpsId, BigDecimal latitude, BigDecimal longitude,
                             LocalDateTime timestamp) {
        this.id = id;
        this.gpsId = gpsId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
    }

    public static LocationResponse from(Location location) {
        return new LocationResponse(location.getId(), location.getGpsId(), location.getLatitude(),
                location.getLongitude(), location.getTimestamp());
    }
}
