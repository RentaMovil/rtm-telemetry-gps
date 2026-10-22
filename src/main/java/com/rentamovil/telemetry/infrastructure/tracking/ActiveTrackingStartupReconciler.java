package com.rentamovil.telemetry.infrastructure.tracking;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.rentamovil.telemetry.domain.port.in.RentalTrackingUseCases;

/**
 * Reconstruye ActiveTrackingPort al arrancar (ver RentalTrackingUseCases.reconcileActiveRentals):
 * sin esto, cada reinicio de este servicio deja todos los GPS con una renta en curso rechazando
 * /locations con NO_ACTIVE_RENTAL hasta que alguien registre un pickup nuevo.
 */
@Component
public class ActiveTrackingStartupReconciler implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(ActiveTrackingStartupReconciler.class);

    private final RentalTrackingUseCases rentalTracking;

    public ActiveTrackingStartupReconciler(RentalTrackingUseCases rentalTracking) {
        this.rentalTracking = rentalTracking;
    }

    @Override
    public void run(ApplicationArguments args) {
        rentalTracking.reconcileActiveRentals();
        log.info("Tracking activo reconstruido al arrancar desde booking-reservation");
    }
}
