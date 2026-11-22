package com.g4vrk.schedula.minestom.impl.task.runner.factory;

import com.g4vrk.schedula.minestom.impl.task.runner.MinestomScheduler;
import com.g4vrk.schedula.task.scheduler.factory.SchedulerFactory;
import net.minestom.server.entity.Entity;
import org.jetbrains.annotations.NotNull;

/**
 * Creates Schedula schedulers backed by Minestom.
 */
public final class MinestomSchedulerFactory implements SchedulerFactory<Entity> {

    @Override
    public @NotNull MinestomScheduler create() {

        return new MinestomScheduler();

    }

}
