package com.rentamovil.telemetry.domain.port.out;

/**
 * Registra una acción en shared.audit (cross-cutting, la crea rtm-iam-db). Cada servicio añade su
 * propio &lt;schema&gt;_app con permiso de INSERT; ver rtm-telemetry-gps-db/03_dcl.
 */
public interface AuditPort {

    /** Sin actor humano: los reportes de ubicación los manda hardware, no una persona logueada. */
    void recordSystem(String action, String affectedTable, Integer affectedRecordId, String detailJson);
}
