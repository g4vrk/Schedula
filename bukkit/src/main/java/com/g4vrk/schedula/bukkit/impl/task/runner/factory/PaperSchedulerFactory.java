package com.g4vrk.schedula.bukkit.impl.task.runner.factory;

import com.g4vrk.schedula.bukkit.impl.task.runner.PaperTaskRunner;
import com.g4vrk.schedula.task.runner.factory.AbstractTaskRunnerFactory;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class PaperTaskRunnerFactory extends AbstractTaskRunnerFactory<PaperTaskRunner> {

    public PaperTaskRunnerFactory(@NotNull Plugin plugin) {
        super(plugin);
    }

    @Override
    public @NotNull PaperTaskRunner create() {
        return new PaperTaskRunner(getPlugin());
    }

}
