package com.rentamovil.telemetry.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Resultado de procesar un evento. Siempre 200, incluso si fue ignorado (DUPLICATE_IGNORED no es
 * un error: quien reenvía no debe reintentar).
 */
@Getter
@AllArgsConstructor
public class InternalEventResponse {

    public static final String PROCESSED = "PROCESSED";

    private String type;
    private String eventId;
    private String result;
}
