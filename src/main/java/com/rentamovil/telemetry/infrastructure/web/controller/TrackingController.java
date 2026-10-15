package com.rentamovil.telemetry.infrastructure.web.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rentamovil.telemetry.domain.entity.AuthenticatedUser;
import com.rentamovil.telemetry.domain.port.in.LocationUseCases;
import com.rentamovil.telemetry.infrastructure.web.auth.CurrentUser;
import com.rentamovil.telemetry.infrastructure.web.dto.LocationResponse;

import lombok.RequiredArgsConstructor;

/** HU-GPS-001 (última ubicación) y HU-GPS-002 (historial). Exclusivo tracking:read. */
@RestController
@RequestMapping("/tracking")
@RequiredArgsConstructor
public class TrackingController {

    private final LocationUseCases locations;

    @GetMapping("/{rentalId}/live")
    public LocationResponse getLive(@CurrentUser AuthenticatedUser currentUser, @PathVariable Integer rentalId) {
        return LocationResponse.from(locations.getLive(currentUser, rentalId));
    }

    @GetMapping("/{rentalId}/history")
    public List<LocationResponse> getHistory(@CurrentUser AuthenticatedUser currentUser,
                                             @PathVariable Integer rentalId) {
        return locations.getHistory(currentUser, rentalId).stream().map(LocationResponse::from).toList();
    }
}
