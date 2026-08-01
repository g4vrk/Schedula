package com.g4vrk.schedula.task.runner;

import com.g4vrk.schedula.task.Task;
import com.g4vrk.schedula.task.schedule.TickSchedule;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractTaskRunner implements TaskRunner {

    @Getter(AccessLevel.PROTECTED)
    private final Plugin plugin;

    protected AbstractTaskRunner(
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
    public abstract @NotNull Task scheduleAsync(@NotNull Runnable runnable, @NotNull TickSchedule tickSchedule);

    @Override
    public abstract @NotNull Task runGlobally(@NotNull Runnable runnable, @NotNull TickSchedule tickSchedule);

    @Override
    public abstract @NotNull Task scheduleEntity(@NotNull Entity entity, @NotNull Runnable runnable, @NotNull TickSchedule tickSchedule);

    @Override
    public abstract @NotNull Task scheduleLocation(@NotNull Location location, @NotNull Runnable runnable, @NotNull TickSchedule tickSchedule);

}
