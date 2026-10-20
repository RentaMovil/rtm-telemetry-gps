package com.rentamovil.telemetry.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.rentamovil.telemetry.domain.entity.AuthenticatedUser;
import com.rentamovil.telemetry.domain.entity.GpsDevice;
import com.rentamovil.telemetry.domain.exception.ForbiddenException;
import com.rentamovil.telemetry.domain.port.out.GpsRepositoryPort;

@ExtendWith(MockitoExtension.class)
class GpsCatalogUseCaseImplTest {

    @Mock
    private GpsRepositoryPort gpsRepository;

    private GpsCatalogUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new GpsCatalogUseCaseImpl(gpsRepository);
    }

    private AuthenticatedUser admin() {
        return new AuthenticatedUser(1, 1, "ADMIN", List.of("tracking:read"));
    }

    private AuthenticatedUser client() {
        return new AuthenticatedUser(2, 2, "CLIENT", List.of("reservations:create"));
    }

    @Test
    void returnsTheActiveDevicesWhenTheCallerHasTrackingRead() {
        GpsDevice device = GpsDevice.restore(1, "SN-001", "Queclink GV300", true);
        when(gpsRepository.findAllActive()).thenReturn(List.of(device));

        assertThat(useCase.listActive(admin())).containsExactly(device);
    }

    @Test
    void clientWithoutTrackingReadIsForbidden() {
        assertThatThrownBy(() -> useCase.listActive(client())).isInstanceOf(ForbiddenException.class);
        verify(gpsRepository, never()).findAllActive();
    }
}
