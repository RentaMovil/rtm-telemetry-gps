package com.rentamovil.telemetry.infrastructure.tracking;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/** In-memory ActiveTrackingPort, rebuilt from RentalStarted/RentalCompleted events. */
class InMemoryActiveTrackingAdapterTest {

    private final InMemoryActiveTrackingAdapter tracking = new InMemoryActiveTrackingAdapter();

    @Test
    void notActiveBeforeAnyRentalStarted() {
        assertThat(tracking.isActive(2)).isFalse();
    }

    @Test
    void activeAfterStart() {
        tracking.start(2);

        assertThat(tracking.isActive(2)).isTrue();
    }

    @Test
    void notActiveAfterStop() {
        tracking.start(2);
        tracking.stop(2);

        assertThat(tracking.isActive(2)).isFalse();
    }

    @Test
    void stoppingAGpsThatNeverStartedIsSafe() {
        tracking.stop(2);

        assertThat(tracking.isActive(2)).isFalse();
    }

    @Test
    void eachGpsIsTrackedIndependently() {
        tracking.start(2);

        assertThat(tracking.isActive(3)).isFalse();
    }
}
