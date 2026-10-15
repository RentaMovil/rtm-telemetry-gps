package com.rentamovil.telemetry.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.rentamovil.telemetry.application.usecase.LocationUseCaseImpl;
import com.rentamovil.telemetry.application.usecase.RentalTrackingUseCaseImpl;
import com.rentamovil.telemetry.domain.port.in.LocationUseCases;
import com.rentamovil.telemetry.domain.port.in.RentalTrackingUseCases;
import com.rentamovil.telemetry.domain.port.out.ActiveTrackingPort;
import com.rentamovil.telemetry.domain.port.out.GpsRepositoryPort;
import com.rentamovil.telemetry.domain.port.out.LocationRepositoryPort;
import com.rentamovil.telemetry.domain.port.out.RentalClientPort;

/**
 * Conecta los casos de uso con sus adaptadores.
 * Los casos de uso no llevan @Service para que el dominio no dependa de Spring.
 */
@Configuration
public class BeanConfig {

    @Bean
    public LocationUseCases locationUseCases(LocationRepositoryPort locationRepository,
                                             GpsRepositoryPort gpsRepository,
                                             ActiveTrackingPort activeTracking,
                                             RentalClientPort rentalClient) {
        return new LocationUseCaseImpl(locationRepository, gpsRepository, activeTracking, rentalClient);
    }

    @Bean
    public RentalTrackingUseCases rentalTrackingUseCases(ActiveTrackingPort activeTracking) {
        return new RentalTrackingUseCaseImpl(activeTracking);
    }
}
