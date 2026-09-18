package com.g4vrk.schedula.task;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

@Getter @ToString
@EqualsAndHashCode
public final class TickSchedule {

    private static final long TICK_MILLIS = 50L;

    private static final TickSchedule INSTANT = new TickSchedule(0, 0);

    private final long delay;
    private final long period;

    private TickSchedule(
            long delay,
            long period
    ) {
        this.delay = delay;
        this.period = period;
    }

    public static @NotNull TickSchedule instant() {
        return INSTANT;
    }

    public static @NotNull TickSchedule delayed(
            final long ticks
    ) {
        return new TickSchedule(
                Math.max(0, ticks),
                0
        );
    }

    public static @NotNull TickSchedule delayed(
            final @NotNull Duration duration
    ) {
        return delayed(toTicks(duration));
    }

    public static @NotNull TickSchedule repeating(
            final long delay,
            final long period
    ) {
        return new TickSchedule(
                Math.max(0, delay),
                Math.max(0, period)
        );
    }

    public static @NotNull TickSchedule repeating(
            final @NotNull Duration delay,
            final @NotNull Duration period
    ) {
        return repeating(
                toTicks(delay),
                toTicks(period)
        );
    }

    public static @NotNull TickSchedule fixedRate(
            final long period
    ) {
        return new TickSchedule(
                0,
                Math.max(0, period)
        );
    }

    public static @NotNull TickSchedule fixedRate(
            final @NotNull Duration period
    ) {
        return fixedRate(toTicks(period));
    }

    public boolean isDelayed() {
        return delay > 0;
    }

    public boolean isRepeating() {
        return period > 0;
    }

    private static long toTicks(
            final @NotNull Duration duration
    ) {
        if (duration.isNegative() || duration.isZero()) {
            return 0;
        }

        final long millis = duration.toMillis();

        if (millis <= 0) {
            return 1;
        }

        return Math.max(
                1L,
                (millis + TICK_MILLIS - 1L) / TICK_MILLIS
        );
    }
}