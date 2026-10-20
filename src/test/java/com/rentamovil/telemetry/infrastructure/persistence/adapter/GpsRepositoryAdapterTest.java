package com.rentamovil.telemetry.infrastructure.persistence.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.rentamovil.telemetry.infrastructure.persistence.entity.GpsEntity;
import com.rentamovil.telemetry.infrastructure.persistence.repository.GpsJpaRepository;

class GpsRepositoryAdapterTest {

    private final GpsJpaRepository jpaRepository = mock(GpsJpaRepository.class);
    private final GpsRepositoryAdapter adapter = new GpsRepositoryAdapter(jpaRepository);

    @Test
    void existsByIdDelegatesToTheJpaRepository() {
        when(jpaRepository.existsById(2)).thenReturn(true);

        assertThat(adapter.existsById(2)).isTrue();
    }

    @Test
    void findAllActiveMapsEachEntityToTheDomain() {
        var entity = new GpsEntity();
        entity.setId(1);
        entity.setSerial("SN-001");
        entity.setModel("Queclink GV300");
        entity.setActive(true);
        when(jpaRepository.findByActiveTrue()).thenReturn(List.of(entity));

        var devices = adapter.findAllActive();

        assertThat(devices).hasSize(1);
        assertThat(devices.get(0).getSerial()).isEqualTo("SN-001");
    }
}
