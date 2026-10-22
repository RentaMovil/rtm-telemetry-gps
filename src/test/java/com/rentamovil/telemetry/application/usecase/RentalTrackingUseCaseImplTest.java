package com.rentamovil.telemetry.application.usecase;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.rentamovil.telemetry.domain.port.out.ActiveTrackingPort;
import com.rentamovil.telemetry.domain.port.out.RentalClientPort;
import com.rentamovil.telemetry.domain.port.out.RentalClientPort.RentalSnapshot;

@ExtendWith(MockitoExtension.class)
class RentalTrackingUseCaseImplTest {

    private static final Integer GPS_ID = 2;

    @Mock
    private ActiveTrackingPort activeTracking;
    @Mock
    private RentalClientPort rentalClient;

    private RentalTrackingUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new RentalTrackingUseCaseImpl(activeTracking, rentalClient);
    }

    @Test
    void rentalStartedBeginsTrackingThatGpsDevice() {
        useCase.onRentalStarted(GPS_ID);

        verify(activeTracking).start(GPS_ID);
    }

    @Test
    void rentalCompletedStopsTrackingThatGpsDevice() {
        useCase.onRentalCompleted(GPS_ID);

        verify(activeTracking).stop(GPS_ID);
    }

    @Test
    void reconcileStartsTrackingForEveryActiveRental() {
        RentalSnapshot rental = new RentalSnapshot(1, GPS_ID, LocalDateTime.now(), null);
        when(rentalClient.getActiveRentals()).thenReturn(List.of(rental));

        useCase.reconcileActiveRentals();

        verify(activeTracking).start(GPS_ID);
    }
}
