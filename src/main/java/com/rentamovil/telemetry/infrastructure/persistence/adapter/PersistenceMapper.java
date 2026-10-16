package com.rentamovil.telemetry.infrastructure.persistence.adapter;

import com.rentamovil.telemetry.domain.entity.GpsDevice;
import com.rentamovil.telemetry.domain.entity.Location;
import com.rentamovil.telemetry.infrastructure.persistence.entity.GpsEntity;
import com.rentamovil.telemetry.infrastructure.persistence.entity.LocationEntity;

/** Traduce entre entidades JPA y entidades de dominio. Sin Spring: lo usan los adaptadores. */
public final class PersistenceMapper {

    private PersistenceMapper() {
    }

    public static LocationEntity toEntity(Location location) {
        LocationEntity entity = new LocationEntity();
        entity.setId(location.getId());
        entity.setGpsId(location.getGpsId());
        entity.setLatitude(location.getLatitude());
        entity.setLongitude(location.getLongitude());
        entity.setTimestamp(location.getTimestamp());
        return entity;
    }

    public static Location toDomain(LocationEntity entity) {
        return Location.restore(entity.getId(), entity.getGpsId(), entity.getLatitude(),
                entity.getLongitude(), entity.getTimestamp());
    }

    public static GpsDevice toDomain(GpsEntity entity) {
        return GpsDevice.restore(entity.getId(), entity.getSerial(), entity.getModel(), entity.isActive());
    }
}
