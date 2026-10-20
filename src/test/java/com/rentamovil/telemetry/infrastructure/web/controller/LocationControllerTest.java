package com.rentamovil.telemetry.infrastructure.web.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import com.rentamovil.telemetry.domain.entity.Location;
import com.rentamovil.telemetry.domain.exception.InvalidDeviceKeyException;
import com.rentamovil.telemetry.domain.port.in.LocationUseCases;
import com.rentamovil.telemetry.infrastructure.security.DeviceApiKeyValidator;
import com.rentamovil.telemetry.infrastructure.web.dto.ReportLocationRequest;

/** POST /locations: the system's highest-volume endpoint, ingesting points from GPS hardware. */
class LocationControllerTest {

    private static final String VALID_KEY = "device-key-123";

    private final LocationUseCases locations = mock(LocationUseCases.class);
    private final DeviceApiKeyValidator deviceApiKeyValidator = mock(DeviceApiKeyValidator.class);
    private final LocationController controller = new LocationController(locations, deviceApiKeyValidator);

    private static ReportLocationRequest request() {
        var request = new ReportLocationRequest();
        request.setGpsId(2);
        request.setLatitude(new BigDecimal("4.6"));
        request.setLongitude(new BigDecimal("-74.0"));
        request.setTimestamp(LocalDateTime.of(2026, 10, 15, 9, 0));
        return request;
    }

    @Test
    void reportsTheLocationWhenTheApiKeyIsValid() {
        Location saved = Location.restore(1, 2, new BigDecimal("4.6"), new BigDecimal("-74.0"),
                LocalDateTime.of(2026, 10, 15, 9, 0));
        when(locations.report(2, new BigDecimal("4.6"), new BigDecimal("-74.0"),
                LocalDateTime.of(2026, 10, 15, 9, 0))).thenReturn(saved);

        var response = controller.report(VALID_KEY, request());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getId()).isEqualTo(1);
    }

    @Test
    void anInvalidApiKeyNeverReachesTheUseCase() {
        doThrow(new InvalidDeviceKeyException("invalid device api key"))
                .when(deviceApiKeyValidator).validate("wrong-key");

        assertThatThrownBy(() -> controller.report("wrong-key", request()))
                .isInstanceOf(InvalidDeviceKeyException.class);

        verifyNoInteractions(locations);
    }
}
