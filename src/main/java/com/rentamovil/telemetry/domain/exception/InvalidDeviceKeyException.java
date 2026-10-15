package com.rentamovil.telemetry.domain.exception;

/** La API key enviada por el dispositivo GPS no coincide con la configurada. 401. */
public class InvalidDeviceKeyException extends RuntimeException {

    public InvalidDeviceKeyException(String message) {
        super(message);
    }
}
