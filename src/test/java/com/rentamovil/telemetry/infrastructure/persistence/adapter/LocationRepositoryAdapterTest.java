package com.rentamovil.telemetry.infrastructure.persistence.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.rentamovil.telemetry.domain.entity.Location;
import com.rentamovil.telemetry.infrastructure.persistence.entity.LocationEntity;
import com.rentamovil.telemetry.infrastructure.persistence.repository.LocationJpaRepository;

class LocationRepositoryAdapterTest {

    private static final LocalDateTime TIMESTAMP = LocalDateTime.of(2026, 10, 15, 9, 0);

    private final LocationJpaRepository jpaRepository = mock(LocationJpaRepository.class);
    private final LocationRepositoryAdapter adapter = new LocationRepositoryAdapter(jpaRepository);

    private static LocationEntity entity(Integer id) {
        var entity = new LocationEntity();
        entity.setId(id);
        entity.setGpsId(2);
        entity.setLatitude(new BigDecimal("4.6"));
        entity.setLongitude(new BigDecimal("-74.0"));
        entity.setTimestamp(TIMESTAMP);
        return entity;
    }

    @Test
    void saveMapsTheDomainEntityToJpaAndBack() {
        when(jpaRepository.save(any())).thenReturn(entity(1));

        Location saved = adapter.save(Location.create(2, new BigDecimal("4.6"), new BigDecimal("-74.0"), TIMESTAMP));

        assertThat(saved.getId()).isEqualTo(1);
    }

    @Test
    void findLatestMapsTheOptionalResult() {
        when(jpaRepository.findLatest(2, TIMESTAMP)).thenReturn(Optional.of(entity(1)));

        assertThat(adapter.findLatest(2, TIMESTAMP)).isPresent();
    }

    @Test
    void findLatestIsEmptyWhenTheJpaRepositoryFindsNothing() {
        when(jpaRepository.findLatest(2, TIMESTAMP)).thenReturn(Optional.empty());

        assertThat(adapter.findLatest(2, TIMESTAMP)).isEmpty();
    }

    @Test
    void findRouteMapsEveryPoint() {
        LocalDateTime to = TIMESTAMP.plusHours(1);
        when(jpaRepository.findRoute(2, TIMESTAMP, to)).thenReturn(List.of(entity(1), entity(2)));

        assertThat(adapter.findRoute(2, TIMESTAMP, to)).hasSize(2);
    }
}
