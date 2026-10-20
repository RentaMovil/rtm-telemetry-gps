package com.rentamovil.telemetry.infrastructure.security;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import com.rentamovil.telemetry.domain.exception.InvalidDeviceKeyException;

/** Validates POST /locations' X-Device-Api-Key against the single configured shared key. */
class DeviceApiKeyValidatorTest {

    private final DeviceApiKeyValidator validator = new DeviceApiKeyValidator("the-configured-key");

    @Test
    void acceptsTheConfiguredKey() {
        assertThatCode(() -> validator.validate("the-configured-key")).doesNotThrowAnyException();
    }

    @Test
    void rejectsAWrongKey() {
        assertThatThrownBy(() -> validator.validate("wrong-key")).isInstanceOf(InvalidDeviceKeyException.class);
    }

    @Test
    void rejectsANullKey() {
        assertThatThrownBy(() -> validator.validate(null)).isInstanceOf(InvalidDeviceKeyException.class);
    }

    @Test
    void failsFastWhenNoKeyIsConfigured() {
        assertThatThrownBy(() -> new DeviceApiKeyValidator(" ")).isInstanceOf(IllegalStateException.class);
    }
}
