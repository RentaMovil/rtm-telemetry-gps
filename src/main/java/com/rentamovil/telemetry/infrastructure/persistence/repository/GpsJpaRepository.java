package com.rentamovil.telemetry.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rentamovil.telemetry.infrastructure.persistence.entity.GpsEntity;

/** Acceso Spring Data a telemetry.gps. */
public interface GpsJpaRepository extends JpaRepository<GpsEntity, Integer> {
}
