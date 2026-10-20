package com.rentamovil.telemetry.infrastructure.web.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.rentamovil.telemetry.domain.entity.AuthenticatedUser;
import com.rentamovil.telemetry.domain.entity.Location;
import com.rentamovil.telemetry.domain.port.in.LocationUseCases;

/** HU-GPS-001 (live) and HU-GPS-002 (history): exclusive to tracking:read (Location.INV-002). */
class TrackingControllerTest {

    private static final AuthenticatedUser ADMIN = new AuthenticatedUser(1, 1, "ADMIN", List.of("tracking:read"));
    private static final Integer RENTAL_ID = 12;

    private final LocationUseCases locations = mock(LocationUseCases.class);
    private final TrackingController controller = new TrackingController(locations);

    @Test
    void getLiveIsMappedFromTheUseCase() {
        Location latest = Location.restore(99, 2, new BigDecimal("4.6"), new BigDecimal("-74.0"),
                LocalDateTime.of(2026, 10, 15, 9, 5));
        when(locations.getLive(ADMIN, RENTAL_ID)).thenReturn(latest);

        assertThat(controller.getLive(ADMIN, RENTAL_ID).getId()).isEqualTo(99);
    }

    @Test
    void getHistoryIsMappedFromTheUseCase() {
        Location point = Location.restore(1, 2, new BigDecimal("4.6"), new BigDecimal("-74.0"),
                LocalDateTime.of(2026, 10, 15, 9, 0));
        when(locations.getHistory(ADMIN, RENTAL_ID)).thenReturn(List.of(point));

        assertThat(controller.getHistory(ADMIN, RENTAL_ID)).extracting(r -> r.getId()).containsExactly(1);
    }

    @Test
    void emptyHistoryWhenThereAreNoPointsYet() {
        when(locations.getHistory(ADMIN, RENTAL_ID)).thenReturn(List.of());

        assertThat(controller.getHistory(ADMIN, RENTAL_ID)).isEmpty();
    }
}
