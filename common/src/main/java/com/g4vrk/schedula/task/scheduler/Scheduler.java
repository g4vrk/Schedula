package com.g4vrk.schedula.task.scheduler;

import com.g4vrk.schedula.task.Task;
import com.g4vrk.schedula.task.TickSchedule;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public interface Scheduler {

    @NotNull Task schedule(
            @NotNull Runnable runnable,
            @NotNull TickSchedule tickSchedule
    );

    @NotNull Task schedule(
            @NotNull Consumer<Task> consumer,
            @NotNull TickSchedule tickSchedule
    );

    @NotNull Task scheduleAsync(
            @NotNull Runnable runnable,
            @NotNull TickSchedule tickSchedule
    );

    @NotNull Task scheduleAsync(
            @NotNull Consumer<Task> consumer,
            @NotNull TickSchedule tickSchedule
    );

    @NotNull Task scheduleEntity(
            @NotNull Entity entity,
            @NotNull Runnable runnable,
            @NotNull TickSchedule tickSchedule
    );

    @NotNull Task scheduleEntity(
            @NotNull Entity entity,
            @NotNull Consumer<Task> consumer,
            @NotNull TickSchedule tickSchedule
    );

    @NotNull Task scheduleLocation(
            @NotNull Location location,
            @NotNull Runnable runnable,
            @NotNull TickSchedule tickSchedule
    );

    @NotNull Task scheduleLocation(
            @NotNull Location location,
            @NotNull Consumer<Task> consumer,
            @NotNull TickSchedule tickSchedule
    );

}