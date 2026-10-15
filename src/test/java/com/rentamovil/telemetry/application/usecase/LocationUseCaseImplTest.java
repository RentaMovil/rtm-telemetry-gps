package com.rentamovil.telemetry.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.rentamovil.telemetry.domain.entity.AuthenticatedUser;
import com.rentamovil.telemetry.domain.entity.Location;
import com.rentamovil.telemetry.domain.exception.BusinessRuleException;
import com.rentamovil.telemetry.domain.exception.ForbiddenException;
import com.rentamovil.telemetry.domain.exception.ResourceNotFoundException;
import com.rentamovil.telemetry.domain.port.out.ActiveTrackingPort;
import com.rentamovil.telemetry.domain.port.out.GpsRepositoryPort;
import com.rentamovil.telemetry.domain.port.out.LocationRepositoryPort;
import com.rentamovil.telemetry.domain.port.out.RentalClientPort;
import com.rentamovil.telemetry.domain.port.out.RentalClientPort.RentalSnapshot;

/** HU-GPS-001/002 (consultas) y Location.INV-001/INV-002 (ingesta y acceso). */
@ExtendWith(MockitoExtension.class)
class LocationUseCaseImplTest {

    private static final Integer GPS_ID = 2;
    private static final Integer RENTAL_ID = 12;
    private static final LocalDateTime START = LocalDateTime.of(2026, 10, 15, 9, 0);

    @Mock
    private LocationRepositoryPort locationRepository;
    @Mock
    private GpsRepositoryPort gpsRepository;
    @Mock
    private ActiveTrackingPort activeTracking;
    @Mock
    private RentalClientPort rentalClient;

    private LocationUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new LocationUseCaseImpl(locationRepository, gpsRepository, activeTracking, rentalClient);
    }

    private AuthenticatedUser admin() {
        return new AuthenticatedUser(1, 1, "ADMIN", List.of("tracking:read"));
    }

    private AuthenticatedUser client() {
        return new AuthenticatedUser(2, 2, "CLIENT", List.of("reservations:create"));
    }

    @Test
    @DisplayName("Reportar con gps inexistente responde NOT_FOUND")
    void gpsInexistenteFalla() {
        when(gpsRepository.existsById(GPS_ID)).thenReturn(false);

        assertThatThrownBy(() -> useCase.report(GPS_ID, new BigDecimal("4.6"), new BigDecimal("-74.0"), START))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(locationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Location.INV-001: sin renta activa para ese gps, responde NO_ACTIVE_RENTAL")
    void sinRentaActivaFalla() {
        when(gpsRepository.existsById(GPS_ID)).thenReturn(true);
        when(activeTracking.isActive(GPS_ID)).thenReturn(false);

        assertThatThrownBy(() -> useCase.report(GPS_ID, new BigDecimal("4.6"), new BigDecimal("-74.0"), START))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(e -> assertThat(((BusinessRuleException) e).getCode()).isEqualTo("NO_ACTIVE_RENTAL"));
        verify(locationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Con renta activa, el reporte se guarda")
    void conRentaActivaSeGuarda() {
        when(gpsRepository.existsById(GPS_ID)).thenReturn(true);
        when(activeTracking.isActive(GPS_ID)).thenReturn(true);
        when(locationRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Location saved = useCase.report(GPS_ID, new BigDecimal("4.6"), new BigDecimal("-74.0"), START);

        assertThat(saved.getGpsId()).isEqualTo(GPS_ID);
    }

    @Test
    @DisplayName("Location.INV-002: un CLIENT no puede consultar la ubicación en vivo")
    void clienteNoPuedeVerUbicacionEnVivo() {
        assertThatThrownBy(() -> useCase.getLive(client(), RENTAL_ID)).isInstanceOf(ForbiddenException.class);
    }

    @Test
    @DisplayName("Location.INV-002: un CLIENT no puede consultar el historial, ni de su propia reserva")
    void clienteNoPuedeVerHistorial() {
        assertThatThrownBy(() -> useCase.getHistory(client(), RENTAL_ID)).isInstanceOf(ForbiddenException.class);
    }

    @Test
    @DisplayName("getLive acota la busqueda desde el inicio de la renta, no solo por gpsId")
    void getLiveAcotaPorInicioDeRenta() {
        when(rentalClient.getRental(RENTAL_ID)).thenReturn(new RentalSnapshot(RENTAL_ID, GPS_ID, START, null));
        Location latest = Location.restore(99, GPS_ID, new BigDecimal("4.6"), new BigDecimal("-74.0"), START.plusMinutes(5));
        when(locationRepository.findLatest(GPS_ID, START)).thenReturn(Optional.of(latest));

        Location result = useCase.getLive(admin(), RENTAL_ID);

        assertThat(result.getId()).isEqualTo(99);
        verify(locationRepository).findLatest(GPS_ID, START);
    }

    @Test
    @DisplayName("getHistory usa el fin real de la renta como limite superior")
    void getHistoryUsaFinDeLaRenta() {
        LocalDateTime end = START.plusDays(2);
        when(rentalClient.getRental(RENTAL_ID)).thenReturn(new RentalSnapshot(RENTAL_ID, GPS_ID, START, end));
        when(locationRepository.findRoute(GPS_ID, START, end)).thenReturn(List.of());

        useCase.getHistory(admin(), RENTAL_ID);

        verify(locationRepository).findRoute(eq(GPS_ID), eq(START), eq(end));
    }
}
