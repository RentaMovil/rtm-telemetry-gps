package com.rentamovil.telemetry.infrastructure.tracking;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.rentamovil.telemetry.domain.port.out.ActiveTrackingPort;

/**
 * Qué gpsId tiene una renta IN_PROGRESS ahora mismo, en memoria (ActiveTrackingPort). Se alimenta
 * de RentalStarted/RentalCompleted vía InternalEventController -- no hay tabla propia para esto
 * (data-model.md solo documenta gps y location), así que este estado se pierde al reiniciar el
 * servicio. Aceptable para un MVP de un solo nodo: el próximo RentalStarted de cada renta en
 * curso la reconstruiría si booking reenviara sus eventos, pero hoy no hay ese replay (mismo
 * nivel que InProcessEventDispatcher del resto de los servicios).
 */
@Component
public class InMemoryActiveTrackingAdapter implements ActiveTrackingPort {

    private final Set<Integer> activeGpsIds = ConcurrentHashMap.newKeySet();

    @Override
    public void start(Integer gpsId) {
        activeGpsIds.add(gpsId);
    }

    @Override
    public void stop(Integer gpsId) {
        activeGpsIds.remove(gpsId);
    }

    @Override
    public boolean isActive(Integer gpsId) {
        return activeGpsIds.contains(gpsId);
    }
}
