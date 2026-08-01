package com.g4vrk.schedula.folia.impl.task.runner.factory;

import com.g4vrk.schedula.folia.impl.task.runner.FoliaTaskRunner;
import com.g4vrk.schedula.task.runner.factory.AbstractTaskRunnerFactory;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class FoliaTaskRunnerFactory extends AbstractTaskRunnerFactory<FoliaTaskRunner> {

    public FoliaTaskRunnerFactory(@NotNull Plugin plugin) {
        super(plugin);
    }

    @Override
    public @NotNull FoliaTaskRunner create() {
        return new FoliaTaskRunner(getPlugin());
    }

}
