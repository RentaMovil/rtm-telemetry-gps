package com.rentamovil.telemetry.infrastructure.persistence.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import com.rentamovil.telemetry.infrastructure.persistence.entity.LocationEntity;

/** telemetry.location: findLatest and findRoute bound the search to a rental's own window. */
@DataJpaTest
class LocationJpaRepositoryTest {

    private static final LocalDateTime RENTAL_START = LocalDateTime.of(2026, 10, 15, 9, 0);

    @Autowired
    private LocationJpaRepository repository;

    private static LocationEntity point(Integer gpsId, LocalDateTime timestamp) {
        var entity = new LocationEntity();
        entity.setGpsId(gpsId);
        entity.setLatitude(new BigDecimal("4.60000000"));
        entity.setLongitude(new BigDecimal("-74.00000000"));
        entity.setTimestamp(timestamp);
        return entity;
    }

    @Test
    void findLatestReturnsTheMostRecentPointSinceTheGivenInstant() {
        repository.save(point(2, RENTAL_START.plusMinutes(5)));
        repository.save(point(2, RENTAL_START.plusMinutes(10)));

        Optional<LocationEntity> latest = repository.findLatest(2, RENTAL_START);

        assertThat(latest).isPresent();
        assertThat(latest.get().getTimestamp()).isEqualTo(RENTAL_START.plusMinutes(10));
    }

    @Test
    void findLatestIgnoresPointsFromBeforeTheRentalStarted() {
        // Same gpsId, reused by an earlier, already finished rental
        repository.save(point(2, RENTAL_START.minusDays(1)));

        assertThat(repository.findLatest(2, RENTAL_START)).isEmpty();
    }

    @Test
    void findLatestIgnoresOtherGpsDevices() {
        repository.save(point(3, RENTAL_START.plusMinutes(5)));

        assertThat(repository.findLatest(2, RENTAL_START)).isEmpty();
    }

    @Test
    void findRouteReturnsThePointsWithinTheWindowInChronologicalOrder() {
        LocalDateTime end = RENTAL_START.plusHours(1);
        repository.save(point(2, RENTAL_START.plusMinutes(30)));
        repository.save(point(2, RENTAL_START.plusMinutes(10)));
        repository.save(point(2, end.plusMinutes(1))); // after the window: excluded

        var route = repository.findRoute(2, RENTAL_START, end);

        assertThat(route).extracting(LocationEntity::getTimestamp)
                .containsExactly(RENTAL_START.plusMinutes(10), RENTAL_START.plusMinutes(30));
    }
}
