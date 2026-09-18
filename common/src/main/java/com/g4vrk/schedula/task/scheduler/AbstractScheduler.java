package com.g4vrk.schedula.task.scheduler;

import com.g4vrk.schedula.task.Task;
import com.g4vrk.schedula.task.TickSchedule;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public abstract class AbstractScheduler implements Scheduler {

    @Getter(AccessLevel.PROTECTED)
    private final Plugin plugin;

    protected AbstractScheduler(
            @NotNull Plugin plugin
    ) {
        this.plugin = plugin;
    }

    protected @NotNull Server getServer() {
        return getPlugin().getServer();
    }

    @Override
    public abstract @NotNull Task schedule(@NotNull Runnable runnable, @NotNull TickSchedule tickSchedule);

    @Override
    public abstract @NotNull Task schedule(@NotNull Consumer<Task> consumer, @NotNull TickSchedule tickSchedule);

    @Override
    public abstract @NotNull Task scheduleAsync(@NotNull Runnable runnable, @NotNull TickSchedule tickSchedule);

    @Override
    public abstract @NotNull Task scheduleAsync(@NotNull Consumer<Task> consumer, @NotNull TickSchedule tickSchedule);

    @Override
    public abstract @NotNull Task scheduleEntity(@NotNull Entity entity, @NotNull Runnable runnable, @NotNull TickSchedule tickSchedule);

    @Override
    public abstract @NotNull Task scheduleEntity(@NotNull Entity entity, @NotNull Consumer<Task> consumer, @NotNull TickSchedule tickSchedule);

    @Override
    public abstract @NotNull Task scheduleLocation(@NotNull Location location, @NotNull Runnable runnable, @NotNull TickSchedule tickSchedule);

    @Override
    public abstract @NotNull Task scheduleLocation(@NotNull Location location, @NotNull Consumer<Task> consumer, @NotNull TickSchedule tickSchedule);
}
