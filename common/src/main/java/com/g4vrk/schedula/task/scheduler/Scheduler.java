package com.g4vrk.schedula.task.scheduler;

import com.g4vrk.schedula.task.Task;
import com.g4vrk.schedula.task.TickSchedule;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * Schedules tasks using Bukkit entity and location types.
 */
public interface Scheduler extends TaskScheduler<Entity> {

    @Override
    @NotNull Task schedule(
            final @NotNull Runnable runnable,
            final @NotNull TickSchedule tickSchedule
    );

    @Override
    @NotNull Task schedule(
            final @NotNull Consumer<Task> consumer,
            final @NotNull TickSchedule tickSchedule
    );

    @Override
    @NotNull Task scheduleAsync(
            final @NotNull Runnable runnable,
            final @NotNull TickSchedule tickSchedule
    );

    @Override
    @NotNull Task scheduleAsync(
            final @NotNull Consumer<Task> consumer,
            final @NotNull TickSchedule tickSchedule
    );

    @Override
    @NotNull Task scheduleEntity(
            final @NotNull Entity entity,
            final @NotNull Runnable runnable,
            final @NotNull TickSchedule tickSchedule
    );

    @Override
    @NotNull Task scheduleEntity(
            final @NotNull Entity entity,
            final @NotNull Consumer<Task> consumer,
            final @NotNull TickSchedule tickSchedule
    );

    /**
     * Schedules a task in the execution context of a Bukkit location.
     */
    @NotNull Task scheduleLocation(
            final @NotNull Location location,
            final @NotNull Runnable runnable,
            final @NotNull TickSchedule tickSchedule
    );

    /**
     * Schedules a location task and provides its handle to the callback.
     */
    @NotNull Task scheduleLocation(
            final @NotNull Location location,
            final @NotNull Consumer<Task> consumer,
            final @NotNull TickSchedule tickSchedule
    );

}
