package com.rentamovil.telemetry.infrastructure.persistence.adapter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.rentamovil.telemetry.domain.entity.Location;
import com.rentamovil.telemetry.domain.port.out.LocationRepositoryPort;
import com.rentamovil.telemetry.infrastructure.persistence.repository.LocationJpaRepository;

@Component
public class LocationRepositoryAdapter implements LocationRepositoryPort {

    private final LocationJpaRepository jpaRepository;

    public LocationRepositoryAdapter(LocationJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    @Transactional
    public Location save(Location location) {
        return PersistenceMapper.toDomain(jpaRepository.save(PersistenceMapper.toEntity(location)));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Location> findLatest(Integer gpsId, LocalDateTime since) {
        return jpaRepository.findLatest(gpsId, since).map(PersistenceMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Location> findRoute(Integer gpsId, LocalDateTime from, LocalDateTime to) {
        return jpaRepository.findRoute(gpsId, from, to).stream().map(PersistenceMapper::toDomain).toList();
    }
}
