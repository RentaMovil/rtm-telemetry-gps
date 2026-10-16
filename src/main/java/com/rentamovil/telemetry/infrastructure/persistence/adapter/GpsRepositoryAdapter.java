package com.rentamovil.telemetry.infrastructure.persistence.adapter;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.rentamovil.telemetry.domain.entity.GpsDevice;
import com.rentamovil.telemetry.domain.port.out.GpsRepositoryPort;
import com.rentamovil.telemetry.infrastructure.persistence.repository.GpsJpaRepository;

@Component
public class GpsRepositoryAdapter implements GpsRepositoryPort {

    private final GpsJpaRepository jpaRepository;

    public GpsRepositoryAdapter(GpsJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Integer gpsId) {
        return jpaRepository.existsById(gpsId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GpsDevice> findAllActive() {
        return jpaRepository.findByActiveTrue().stream().map(PersistenceMapper::toDomain).toList();
    }
}
