package com.rentamovil.telemetry.domain.entity;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** Quién hace la petición a /tracking/*, según los claims del access token. */
@Getter
@AllArgsConstructor
public class AuthenticatedUser {

    private Integer userId;
    private Integer personId;
    private String role;
    private List<String> permissions;

    public boolean hasPermission(String permission) {
        return permissions != null && permissions.contains(permission);
    }
}
