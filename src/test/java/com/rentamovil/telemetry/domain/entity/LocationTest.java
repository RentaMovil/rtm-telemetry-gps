package com.rentamovil.telemetry.domain.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.rentamovil.telemetry.domain.exception.BusinessRuleException;

/**
 * Domain tests: no Spring, no database (hexagonal-architecture.md, section 8).
 * Verify the invariants from 02-domain/entities-and-rules.md (Entity: Location).
 */
class LocationTest {

    private static final LocalDateTime NOW = LocalDateTime.of(2026, 10, 15, 10, 0);

    @Test
    @DisplayName("Valid coordinates are accepted")
    void validCoordinatesAreAccepted() {
        Location location = Location.create(1, new BigDecimal("4.6097"), new BigDecimal("-74.0817"), NOW);

        assertThat(location.getGpsId()).isEqualTo(1);
        assertThat(location.getLatitude()).isEqualByComparingTo("4.6097");
    }

    @Test
    @DisplayName("A latitude outside -90..90 is rejected")
    void latitudeOutOfRangeIsRejected() {
        assertThatThrownBy(() -> Location.create(1, new BigDecimal("91"), new BigDecimal("0"), NOW))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    @DisplayName("A longitude outside -180..180 is rejected")
    void longitudeOutOfRangeIsRejected() {
        assertThatThrownBy(() -> Location.create(1, new BigDecimal("0"), new BigDecimal("181"), NOW))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    @DisplayName("The exact boundaries (-90, 90, -180, 180) are valid")
    void exactBoundariesAreValid() {
        assertThat(Location.create(1, new BigDecimal("90"), new BigDecimal("180"), NOW)).isNotNull();
        assertThat(Location.create(1, new BigDecimal("-90"), new BigDecimal("-180"), NOW)).isNotNull();
    }
}
