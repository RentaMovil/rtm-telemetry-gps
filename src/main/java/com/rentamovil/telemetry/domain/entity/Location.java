package com.rentamovil.telemetry.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.rentamovil.telemetry.domain.exception.BusinessRuleException;

import lombok.Getter;

/**
 * Un punto de ubicación reportado por un dispositivo GPS. El conjunto de puntos de una renta
 * forma su ruta (entities-and-rules.md, Entity: Location).
 *
 * <p>Entidad de dominio pura — no usa Spring ni JPA. Las invariantes de rango (Location.INV-001
 * en el sentido de "coordenadas válidas") están duplicadas a propósito con el CHECK de la base:
 * cada capa frena por su lado.
 */
@Getter
public class Location {

    private final Integer id;
    private final Integer gpsId;
    private final BigDecimal latitude;
    private final BigDecimal longitude;
    private final LocalDateTime timestamp;

    private Location(Integer id, Integer gpsId, BigDecimal latitude, BigDecimal longitude,
                     LocalDateTime timestamp) {
        this.id = id;
        this.gpsId = gpsId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
    }

    public static Location create(Integer gpsId, BigDecimal latitude, BigDecimal longitude,
                                  LocalDateTime timestamp) {
        validateRange("latitude", latitude, -90, 90);
        validateRange("longitude", longitude, -180, 180);
        // (0, 0) es "Null Island": dentro del rango válido pero nunca una posición real de la
        // flota. Varios módulos GPS la mandan como placeholder mientras todavía no tienen fix
        // (ver README -> "Sin fix todavía"), y sin este rechazo quedaba guardada como un punto
        // más de la ruta, dibujando un salto enorme en el mapa hasta el golfo de Guinea.
        if (BigDecimal.ZERO.compareTo(latitude) == 0 && BigDecimal.ZERO.compareTo(longitude) == 0) {
            throw new BusinessRuleException("GPS_NO_FIX", "El GPS todavía no tiene una posición válida");
        }
        return new Location(null, gpsId, latitude, longitude, timestamp);
    }

    public static Location restore(Integer id, Integer gpsId, BigDecimal latitude, BigDecimal longitude,
                                   LocalDateTime timestamp) {
        return new Location(id, gpsId, latitude, longitude, timestamp);
    }

    private static void validateRange(String field, BigDecimal value, int min, int max) {
        if (value == null || value.compareTo(BigDecimal.valueOf(min)) < 0
                || value.compareTo(BigDecimal.valueOf(max)) > 0) {
            throw new BusinessRuleException("INVALID_COORDINATES",
                    field + " debe estar entre " + min + " y " + max);
        }
    }
}
