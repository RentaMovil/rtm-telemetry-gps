package com.rentamovil.telemetry.infrastructure.web.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.rentamovil.telemetry.domain.entity.AuthenticatedUser;
import com.rentamovil.telemetry.domain.entity.GpsDevice;
import com.rentamovil.telemetry.domain.port.in.GpsCatalogUseCases;

/** GET /gps: catalog of GPS devices for the admin's pickup selector. */
class GpsControllerTest {

    private final GpsCatalogUseCases gpsCatalog = mock(GpsCatalogUseCases.class);
    private final GpsController controller = new GpsController(gpsCatalog);

    private static final AuthenticatedUser ADMIN = new AuthenticatedUser(1, 1, "ADMIN", List.of("tracking:read"));

    @Test
    void listActiveIsMappedFromTheUseCase() {
        GpsDevice device = GpsDevice.restore(1, "SN-001", "Queclink GV300", true);
        when(gpsCatalog.listActive(ADMIN)).thenReturn(List.of(device));

        var response = controller.listActive(ADMIN);

        assertThat(response).hasSize(1);
        assertThat(response.get(0).getSerial()).isEqualTo("SN-001");
    }

    @Test
    void emptyWhenThereAreNoActiveDevices() {
        when(gpsCatalog.listActive(ADMIN)).thenReturn(List.of());

        assertThat(controller.listActive(ADMIN)).isEmpty();
    }
}
