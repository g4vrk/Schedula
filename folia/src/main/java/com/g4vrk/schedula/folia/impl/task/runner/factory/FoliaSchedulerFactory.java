package com.g4vrk.schedula.folia.impl.task.runner.factory;

import com.g4vrk.schedula.folia.impl.task.runner.FoliaScheduler;
import com.g4vrk.schedula.task.scheduler.factory.SchedulerFactory;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class FoliaSchedulerFactory implements SchedulerFactory<Entity> {

    private final Plugin plugin;

    public FoliaSchedulerFactory(
            final @NotNull Plugin plugin
    ) {

        this.plugin = plugin;

    }

    @Override
    public @NotNull FoliaScheduler create() {

        return new FoliaScheduler(plugin);

    }

}
