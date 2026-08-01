package com.g4vrk.schedula.multi;

import com.g4vrk.schedula.api.SchedulaAPI;
import com.g4vrk.schedula.bukkit.impl.task.runner.factory.PaperSchedulerFactory;
import com.g4vrk.schedula.folia.impl.task.runner.factory.FoliaSchedulerFactory;
import com.g4vrk.schedula.task.scheduler.factory.SchedulerFactory;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

class BuilderImpl implements MultiplatformSchedulaAPI.Builder {

    private SchedulerFactory schedulerFactory;

    public BuilderImpl() {
    }

    @Override
    public MultiplatformSchedulaAPI.@NotNull Builder autoDetectFactoryFor(@NotNull Plugin plugin) {
        this.schedulerFactory = FoliaUtility.foliaServer()
                ? new FoliaSchedulerFactory(plugin)
                : new PaperSchedulerFactory(plugin);

        return this;
    }

    @Override
    public SchedulaAPI.@NotNull Builder factory(@NotNull SchedulerFactory factory) {
        this.schedulerFactory = factory;
        return this;
    }

    @Override
    public @NotNull MultiplatformSchedulaAPI build() {
        if (schedulerFactory == null) throw new NullPointerException("factory not initialized!");
        return () -> schedulerFactory.create();
    }
}
