package com.g4vrk.schedula.task.runner;

import com.g4vrk.schedula.task.Task;
import com.g4vrk.schedula.task.schedule.TickSchedule;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public interface TaskRunner {

    @NotNull Task schedule(
            @NotNull Runnable runnable,
            @NotNull TickSchedule tickSchedule
    );

    @NotNull Task scheduleAsync(
            @NotNull Runnable runnable,
            @NotNull TickSchedule tickSchedule
    );

    @NotNull Task scheduleEntity(
            @NotNull Entity entity,
            @NotNull Runnable runnable,
            @NotNull TickSchedule tickSchedule
    );

    @NotNull Task scheduleLocation(
            @NotNull Location location,
            @NotNull Runnable runnable,
            @NotNull TickSchedule tickSchedule
    );

}
