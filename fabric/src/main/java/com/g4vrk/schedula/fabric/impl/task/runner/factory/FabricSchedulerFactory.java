package com.g4vrk.schedula.fabric.impl.task.runner.factory;

import com.g4vrk.schedula.fabric.impl.task.runner.FabricScheduler;
import com.g4vrk.schedula.task.scheduler.factory.SchedulerFactory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Creates Schedula schedulers backed by Fabric.
 */
public final class FabricSchedulerFactory implements SchedulerFactory<Entity> {

    private final MinecraftServer server;

    public FabricSchedulerFactory(
            final @NotNull MinecraftServer server
    ) {

        this.server = Objects.requireNonNull(server, "server");

    }

    @Override
    public @NotNull FabricScheduler create() {

        return new FabricScheduler(server);

    }

}
