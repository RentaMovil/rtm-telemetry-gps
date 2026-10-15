package com.rentamovil.telemetry.domain.exception;

/** El usuario autenticado no tiene el permiso requerido. 403. */
public class ForbiddenException extends RuntimeException {

    public ForbiddenException(String message) {
        super(message);
    }
}
