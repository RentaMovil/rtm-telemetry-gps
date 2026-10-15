package com.rentamovil.telemetry.infrastructure.web.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/** Cuerpo de POST /locations. Lo manda el hardware GPS, no un formulario humano. */
@Getter
@Setter
public class ReportLocationRequest {

    @NotNull(message = "obligatorio")
    private Integer gpsId;

    @NotNull(message = "obligatorio")
    private BigDecimal latitude;

    @NotNull(message = "obligatorio")
    private BigDecimal longitude;

    @NotNull(message = "obligatorio")
    private LocalDateTime timestamp;
}
