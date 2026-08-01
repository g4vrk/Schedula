package com.g4vrk.schedula.bukkit.impl.task.runner.factory;

import com.g4vrk.schedula.bukkit.impl.task.runner.PaperScheduler;
import com.g4vrk.schedula.task.scheduler.factory.AbstractSchedulerFactory;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class PaperSchedulerFactory extends AbstractSchedulerFactory<PaperScheduler> {

    public PaperSchedulerFactory(@NotNull Plugin plugin) {
        super(plugin);
    }

    @Override
    public @NotNull PaperScheduler create() {
        return new PaperScheduler(getPlugin());
    }

}
