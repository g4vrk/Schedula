package com.g4vrk.schedula.task.scheduler;

import com.g4vrk.schedula.task.Task;
import com.g4vrk.schedula.task.TickSchedule;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * Extends {@link TaskScheduler} with location-scoped scheduling for regionized platforms.
 *
 * @param <E> the entity type accepted by this scheduler
 * @param <L> the location type accepted by this scheduler
 */
public interface RegionTaskScheduler<E, L> extends TaskScheduler<E> {

    /**
     * Schedules a task in the execution context of a platform location.
     *
     * @param location     the target location
     * @param runnable     the task callback
     * @param tickSchedule the task timing
     * @return the task handle
     */
    @NotNull Task scheduleLocation(
            final @NotNull L location,
            final @NotNull Runnable runnable,
            final @NotNull TickSchedule tickSchedule
    );

    /**
     * Schedules a location task and provides its handle to the callback.
     *
     * @param location     the target location
     * @param consumer     the task callback
     * @param tickSchedule the task timing
     * @return the task handle
     */
    @NotNull Task scheduleLocation(
            final @NotNull L location,
            final @NotNull Consumer<Task> consumer,
            final @NotNull TickSchedule tickSchedule
    );

}
