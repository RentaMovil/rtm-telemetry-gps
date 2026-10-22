package com.rentamovil.telemetry.application.usecase;

import com.rentamovil.telemetry.domain.port.in.RentalTrackingUseCases;
import com.rentamovil.telemetry.domain.port.out.ActiveTrackingPort;
import com.rentamovil.telemetry.domain.port.out.RentalClientPort;

/** Reacciones a RentalStarted/RentalCompleted de booking-reservation (events.md). */
public class RentalTrackingUseCaseImpl implements RentalTrackingUseCases {

    private final ActiveTrackingPort activeTracking;
    private final RentalClientPort rentalClient;

    public RentalTrackingUseCaseImpl(ActiveTrackingPort activeTracking, RentalClientPort rentalClient) {
        this.activeTracking = activeTracking;
        this.rentalClient = rentalClient;
    }

    @Override
    public void onRentalStarted(Integer gpsId) {
        activeTracking.start(gpsId);
    }

    @Override
    public void onRentalCompleted(Integer gpsId) {
        activeTracking.stop(gpsId);
    }

    @Override
    public void reconcileActiveRentals() {
        rentalClient.getActiveRentals().forEach(rental -> activeTracking.start(rental.gpsId()));
    }
}
