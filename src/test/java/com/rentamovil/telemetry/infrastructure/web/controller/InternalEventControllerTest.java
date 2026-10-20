package com.rentamovil.telemetry.infrastructure.web.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import java.util.Map;

import org.junit.jupiter.api.Test;

import com.rentamovil.telemetry.domain.port.in.RentalTrackingUseCases;
import com.rentamovil.telemetry.infrastructure.web.dto.InternalEventRequest;
import com.rentamovil.telemetry.infrastructure.web.dto.InternalEventResponse;
import com.rentamovil.telemetry.infrastructure.web.exception.InvalidEventException;

/** POST /internal/events: dispatches booking-reservation's rental events by eventType. */
class InternalEventControllerTest {

    private final RentalTrackingUseCases rentalTracking = mock(RentalTrackingUseCases.class);
    private final InternalEventController controller = new InternalEventController(rentalTracking);

    private static InternalEventRequest event(String eventType, Map<String, Object> payload) {
        return new InternalEventRequest("evt-1", eventType, payload);
    }

    @Test
    void rentalStartedDelegatesToTheUseCase() {
        InternalEventResponse response = controller.receive(event("RentalStarted", Map.of("gpsId", 2)));

        verify(rentalTracking).onRentalStarted(2);
        assertThat(response.getResult()).isEqualTo(InternalEventResponse.PROCESSED);
    }

    @Test
    void rentalCompletedDelegatesToTheUseCase() {
        controller.receive(event("RentalCompleted", Map.of("gpsId", 2)));

        verify(rentalTracking).onRentalCompleted(2);
    }

    @Test
    void gpsIdAsNumericTextIsAlsoAccepted() {
        controller.receive(event("RentalStarted", Map.of("gpsId", "2")));

        verify(rentalTracking).onRentalStarted(2);
    }

    @Test
    void missingGpsIdIsRejectedAndNeverReachesTheUseCase() {
        assertThatThrownBy(() -> controller.receive(event("RentalStarted", Map.of())))
                .isInstanceOf(InvalidEventException.class)
                .hasFieldOrPropertyWithValue("field", "payload.gpsId");

        verifyNoInteractions(rentalTracking);
    }

    @Test
    void unknownEventTypeIsRejected() {
        assertThatThrownBy(() -> controller.receive(event("RentalCancelled", Map.of("gpsId", 2))))
                .isInstanceOf(InvalidEventException.class)
                .hasFieldOrPropertyWithValue("code", "UNKNOWN_EVENT_TYPE");

        verifyNoInteractions(rentalTracking);
    }
}
