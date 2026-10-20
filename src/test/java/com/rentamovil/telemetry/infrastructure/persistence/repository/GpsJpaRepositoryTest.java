package com.rentamovil.telemetry.infrastructure.persistence.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import com.rentamovil.telemetry.infrastructure.persistence.entity.GpsEntity;

/** telemetry.gps: a read-only catalog loaded by seed, filtered by the active flag. */
@DataJpaTest
class GpsJpaRepositoryTest {

    @Autowired
    private GpsJpaRepository repository;

    private static GpsEntity device(Integer id, String serial, boolean active) {
        var entity = new GpsEntity();
        entity.setId(id);
        entity.setSerial(serial);
        entity.setModel("Queclink GV300");
        entity.setActive(active);
        return entity;
    }

    @Test
    void findByActiveTrueExcludesInactiveDevices() {
        repository.save(device(1, "SN-001", true));
        repository.save(device(2, "SN-002", false));

        assertThat(repository.findByActiveTrue()).extracting(GpsEntity::getSerial).containsExactly("SN-001");
    }

    @Test
    void emptyWhenNoDeviceIsActive() {
        repository.save(device(1, "SN-001", false));

        assertThat(repository.findByActiveTrue()).isEmpty();
    }
}
