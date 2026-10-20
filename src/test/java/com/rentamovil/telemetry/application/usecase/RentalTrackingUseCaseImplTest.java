package com.rentamovil.telemetry.application.usecase;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.rentamovil.telemetry.domain.port.out.ActiveTrackingPort;

@ExtendWith(MockitoExtension.class)
class RentalTrackingUseCaseImplTest {

    private static final Integer GPS_ID = 2;

    @Mock
    private ActiveTrackingPort activeTracking;

    private RentalTrackingUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new RentalTrackingUseCaseImpl(activeTracking);
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
}
