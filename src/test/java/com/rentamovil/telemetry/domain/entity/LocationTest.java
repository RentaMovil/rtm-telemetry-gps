package com.rentamovil.telemetry.domain.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.rentamovil.telemetry.domain.exception.BusinessRuleException;

/**
 * Tests del dominio: sin Spring, sin base de datos (hexagonal-architecture.md, sección 8).
 * Verifican las invariantes de 02-domain/entities-and-rules.md (Entity: Location).
 */
class LocationTest {

    private static final LocalDateTime NOW = LocalDateTime.of(2026, 10, 15, 10, 0);

    @Test
    @DisplayName("Coordenadas válidas se aceptan")
    void coordenadasValidasSeAceptan() {
        Location location = Location.create(1, new BigDecimal("4.6097"), new BigDecimal("-74.0817"), NOW);

        assertThat(location.getGpsId()).isEqualTo(1);
        assertThat(location.getLatitude()).isEqualByComparingTo("4.6097");
    }

    @Test
    @DisplayName("Latitud fuera de -90..90 se rechaza")
    void latitudFueraDeRangoSeRechaza() {
        assertThatThrownBy(() -> Location.create(1, new BigDecimal("91"), new BigDecimal("0"), NOW))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    @DisplayName("Longitud fuera de -180..180 se rechaza")
    void longitudFueraDeRangoSeRechaza() {
        assertThatThrownBy(() -> Location.create(1, new BigDecimal("0"), new BigDecimal("181"), NOW))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    @DisplayName("Los límites exactos (-90, 90, -180, 180) son válidos")
    void limitesExactosSonValidos() {
        assertThat(Location.create(1, new BigDecimal("90"), new BigDecimal("180"), NOW)).isNotNull();
        assertThat(Location.create(1, new BigDecimal("-90"), new BigDecimal("-180"), NOW)).isNotNull();
    }
}
