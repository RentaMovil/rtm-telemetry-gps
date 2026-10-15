package com.rentamovil.telemetry.infrastructure.web.dto;

import java.math.BigDecimal;
import java.util.Map;

import com.rentamovil.telemetry.infrastructure.web.exception.InvalidEventException;

/**
 * Lee los datos de un evento con validación. Cada error dice el campo exacto
 * ("payload.gpsId") para que el servicio que envía sepa qué corregir.
 */
public final class InternalEventPayload {

    private final Map<String, Object> values;

    public InternalEventPayload(Map<String, Object> values) {
        this.values = values == null ? Map.of() : values;
    }

    public Integer requiredInt(String name) {
        Object raw = values.get(name);
        if (raw == null) {
            throw missing(name);
        }
        if (raw instanceof Integer number) {
            return number;
        }
        String text = raw instanceof Number || raw instanceof String ? raw.toString().trim() : null;
        if (text == null) {
            throw invalid(name, "debe ser un número entero");
        }
        try {
            return new BigDecimal(text).intValueExact();
        } catch (NumberFormatException | ArithmeticException e) {
            throw invalid(name, "debe ser un número entero");
        }
    }

    private static InvalidEventException missing(String name) {
        return new InvalidEventException("INVALID_EVENT", "payload." + name, "obligatorio");
    }

    private static InvalidEventException invalid(String name, String message) {
        return new InvalidEventException("INVALID_EVENT", "payload." + name, message);
    }
}
