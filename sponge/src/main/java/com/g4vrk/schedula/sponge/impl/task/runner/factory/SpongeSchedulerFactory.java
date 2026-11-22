package com.g4vrk.schedula.sponge.impl.task.runner.factory;

import com.g4vrk.schedula.sponge.impl.task.runner.SpongeScheduler;
import com.g4vrk.schedula.task.scheduler.factory.SchedulerFactory;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.plugin.PluginContainer;

/**
 * Creates Schedula schedulers backed by Sponge.
 */
public final class SpongeSchedulerFactory implements SchedulerFactory<ServerPlayer> {

    private final PluginContainer plugin;

    public SpongeSchedulerFactory(
            final @NotNull PluginContainer plugin
    ) {

        this.plugin = plugin;

    }

    @Override
    public @NotNull SpongeScheduler create() {

        return new SpongeScheduler(plugin);

    }

}
