package com.rentamovil.telemetry.application.usecase;

import com.rentamovil.telemetry.domain.port.in.RentalTrackingUseCases;
import com.rentamovil.telemetry.domain.port.out.ActiveTrackingPort;

/** Reacciones a RentalStarted/RentalCompleted de booking-reservation (events.md). */
public class RentalTrackingUseCaseImpl implements RentalTrackingUseCases {

    private final ActiveTrackingPort activeTracking;

    public RentalTrackingUseCaseImpl(ActiveTrackingPort activeTracking) {
        this.activeTracking = activeTracking;
    }

    @Override
    public void onRentalStarted(Integer gpsId) {
        activeTracking.start(gpsId);
    }

    @Override
    public void onRentalCompleted(Integer gpsId) {
        activeTracking.stop(gpsId);
    }
}
