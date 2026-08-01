package com.g4vrk.schedula.folia.impl.task.runner.factory;

import com.g4vrk.schedula.folia.impl.task.runner.FoliaScheduler;
import com.g4vrk.schedula.task.scheduler.factory.AbstractSchedulerFactory;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class FoliaSchedulerFactory extends AbstractSchedulerFactory<FoliaScheduler> {

    public FoliaSchedulerFactory(@NotNull Plugin plugin) {
        super(plugin);
    }

    @Override
    public @NotNull FoliaScheduler create() {
        return new FoliaScheduler(getPlugin());
    }

}
