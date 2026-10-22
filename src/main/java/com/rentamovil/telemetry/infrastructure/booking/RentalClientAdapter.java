package com.rentamovil.telemetry.infrastructure.booking;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.rentamovil.telemetry.domain.exception.BusinessRuleException;
import com.rentamovil.telemetry.domain.exception.ResourceNotFoundException;
import com.rentamovil.telemetry.domain.port.out.RentalClientPort;

/**
 * Cliente REST de booking-reservation: resuelve rentalId -> gpsId + ventana de fechas.
 *
 * <p>GET /internal/rentals/{id} es público (sin auth) por diseño: no hay una persona detrás de
 * esta llamada, ya se verificó tracking:read en este mismo servicio antes de llegar acá (mismo
 * patrón que /internal/reservations/vehicle-occupancy de fleet-maintenance). Va directo por la
 * red interna, no por el gateway.
 */
@Component
public class RentalClientAdapter implements RentalClientPort {

    private final RestClient restClient;

    public RentalClientAdapter(@Value("${app.telemetry-gps.booking-url}") String bookingUrl,
                              @Value("${app.telemetry-gps.booking-timeout}") Duration timeout) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(timeout);
        requestFactory.setReadTimeout(timeout);
        this.restClient = RestClient.builder().baseUrl(bookingUrl).requestFactory(requestFactory).build();
    }

    @Override
    public RentalSnapshot getRental(Integer rentalId) {
        try {
            RentalApiResponse response = restClient.get()
                    .uri("/internal/rentals/{id}", rentalId)
                    .retrieve()
                    .body(RentalApiResponse.class);
            if (response == null) {
                throw unavailable();
            }
            return new RentalSnapshot(response.id(), response.gpsId(), response.actualStartDate(),
                    response.actualEndDate());
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ResourceNotFoundException("La renta " + rentalId + " no existe");
            }
            throw unavailable();
        } catch (RestClientException e) {
            throw unavailable();
        }
    }

    @Override
    public List<RentalSnapshot> getActiveRentals() {
        try {
            List<RentalApiResponse> response = restClient.get()
                    .uri("/internal/rentals/active")
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<RentalApiResponse>>() { });
            if (response == null) {
                return List.of();
            }
            return response.stream()
                    .map(r -> new RentalSnapshot(r.id(), r.gpsId(), r.actualStartDate(), r.actualEndDate()))
                    .toList();
        } catch (RestClientException e) {
            // Al arrancar: si booking-reservation no responde todavía, se arranca igual sin
            // tracking activo reconstruido -- mejor que tumbar telemetry-gps por una dependencia
            // que puede no estar lista aún (mismo nivel de tolerancia que el resto del sistema).
            return List.of();
        }
    }

    private BusinessRuleException unavailable() {
        return new BusinessRuleException("BOOKING_SERVICE_UNAVAILABLE",
                "El servicio de reservas no está disponible en este momento");
    }

    record RentalApiResponse(Integer id, Integer gpsId, LocalDateTime actualStartDate,
                             LocalDateTime actualEndDate) {
    }
}
