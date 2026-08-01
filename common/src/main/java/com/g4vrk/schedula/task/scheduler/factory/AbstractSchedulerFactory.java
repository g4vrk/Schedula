package com.g4vrk.schedula.task.scheduler.factory;

import com.g4vrk.schedula.task.scheduler.Scheduler;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractSchedulerFactory<R extends Scheduler> implements SchedulerFactory {

    @Getter(AccessLevel.PROTECTED)
    private final Plugin plugin;

    protected AbstractSchedulerFactory(
            @NotNull Plugin plugin
    ) {
        this.plugin = plugin;
    }

    @Override
    public abstract @NotNull R create();

}
