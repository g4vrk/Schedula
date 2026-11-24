package com.g4vrk.schedula.bukkit.impl.task.runner.factory;

import com.g4vrk.schedula.bukkit.impl.task.runner.PaperScheduler;
import com.g4vrk.schedula.task.scheduler.factory.SchedulerFactory;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class PaperSchedulerFactory implements SchedulerFactory<Entity> {

    private final Plugin plugin;

    public PaperSchedulerFactory(
            final @NotNull Plugin plugin
    ) {

        this.plugin = plugin;

    }

    @Override
    public @NotNull PaperScheduler create() {

        return new PaperScheduler(plugin);

    }

}
