package com.rentamovil.telemetry.application.usecase;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.rentamovil.telemetry.domain.entity.AuthenticatedUser;
import com.rentamovil.telemetry.domain.entity.Location;
import com.rentamovil.telemetry.domain.exception.BusinessRuleException;
import com.rentamovil.telemetry.domain.exception.ForbiddenException;
import com.rentamovil.telemetry.domain.exception.ResourceNotFoundException;
import com.rentamovil.telemetry.domain.port.in.LocationUseCases;
import com.rentamovil.telemetry.domain.port.out.ActiveTrackingPort;
import com.rentamovil.telemetry.domain.port.out.GpsRepositoryPort;
import com.rentamovil.telemetry.domain.port.out.LocationRepositoryPort;
import com.rentamovil.telemetry.domain.port.out.RentalClientPort;
import com.rentamovil.telemetry.domain.port.out.RentalClientPort.RentalSnapshot;

/**
 * HU-GPS-001/002 (consultas de tracking) y la ingesta de /locations (Location.INV-001/INV-002).
 */
public class LocationUseCaseImpl implements LocationUseCases {

    private static final String TRACKING_PERMISSION = "tracking:read";

    private final LocationRepositoryPort locationRepository;
    private final GpsRepositoryPort gpsRepository;
    private final ActiveTrackingPort activeTracking;
    private final RentalClientPort rentalClient;

    public LocationUseCaseImpl(LocationRepositoryPort locationRepository, GpsRepositoryPort gpsRepository,
                              ActiveTrackingPort activeTracking, RentalClientPort rentalClient) {
        this.locationRepository = locationRepository;
        this.gpsRepository = gpsRepository;
        this.activeTracking = activeTracking;
        this.rentalClient = rentalClient;
    }

    @Override
    public Location report(Integer gpsId, BigDecimal latitude, BigDecimal longitude, LocalDateTime timestamp) {
        if (!gpsRepository.existsById(gpsId)) {
            throw new ResourceNotFoundException("El gps " + gpsId + " no corresponde a ningún dispositivo registrado");
        }
        // Location.INV-001: solo se graba si hay una renta IN_PROGRESS asociada a este gpsId ahora.
        if (!activeTracking.isActive(gpsId)) {
            throw new BusinessRuleException("NO_ACTIVE_RENTAL",
                    "Este GPS no tiene ninguna renta en curso asociada");
        }
        return locationRepository.save(Location.create(gpsId, latitude, longitude, timestamp));
    }

    @Override
    public Location getLive(AuthenticatedUser currentUser, Integer rentalId) {
        requireTrackingPermission(currentUser);
        RentalSnapshot rental = rentalClient.getRental(rentalId);
        return locationRepository.findLatest(rental.gpsId(), rental.actualStartDate())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Todavía no hay ninguna ubicación registrada para esta renta"));
    }

    @Override
    public List<Location> getHistory(AuthenticatedUser currentUser, Integer rentalId) {
        requireTrackingPermission(currentUser);
        RentalSnapshot rental = rentalClient.getRental(rentalId);
        // Si la renta sigue IN_PROGRESS (actualEndDate null), se acota hasta ahora: igual que
        // getLive, nunca se devuelven puntos de una renta anterior que reusó el mismo gpsId.
        LocalDateTime to = rental.actualEndDate() != null ? rental.actualEndDate() : LocalDateTime.now();
        return locationRepository.findRoute(rental.gpsId(), rental.actualStartDate(), to);
    }

    private void requireTrackingPermission(AuthenticatedUser currentUser) {
        if (!currentUser.hasPermission(TRACKING_PERMISSION)) {
            // Location.INV-002: un CLIENT nunca puede consultar esto, ni para su propia reserva.
            throw new ForbiddenException("Necesitas permiso para consultar la ubicación de un vehículo");
        }
    }
}
