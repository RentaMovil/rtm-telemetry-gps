package com.rentamovil.telemetry.infrastructure.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.rentamovil.telemetry.domain.exception.InvalidDeviceKeyException;

/**
 * Valida el header X-Device-Api-Key de POST /locations contra la clave fija configurada
 * (decisions.md: "un esquema de API key por dispositivo", pero sin columna propia por
 * dispositivo en el modelo de datos documentado — una sola clave compartida para todos).
 *
 * <p>No es el JWT/RBAC de persona logueada: la pregunta acá es "¿es este un dispositivo que
 * nosotros instalamos?", no "qué persona, con qué rol" (decisions.md).
 */
@Component
public class DeviceApiKeyValidator {

    private final String configuredKey;

    public DeviceApiKeyValidator(@Value("${app.telemetry-gps.device-api-key}") String configuredKey) {
        if (configuredKey == null || configuredKey.isBlank()) {
            throw new IllegalStateException("Falta APP_TELEMETRY_GPS_DEVICE_API_KEY en el .env");
        }
        this.configuredKey = configuredKey;
    }

    public void validate(String providedKey) {
        if (providedKey == null || !configuredKey.equals(providedKey)) {
            throw new InvalidDeviceKeyException("API key de dispositivo inválida");
        }
    }
}
