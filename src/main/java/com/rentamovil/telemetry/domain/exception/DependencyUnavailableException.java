package com.rentamovil.telemetry.domain.exception;

import lombok.Getter;

/** Otro servicio del que depende la operación no responde (hoy: iam, al descargar sus claves
 * para verificar el token). No es culpa de quien llama: se traduce a 503. */
@Getter
public class DependencyUnavailableException extends RuntimeException {

    // IAM_UNAVAILABLE
    private final String code;

    public DependencyUnavailableException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }
}
