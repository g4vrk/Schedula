package com.g4vrk.schedula.api;

import com.g4vrk.schedula.task.scheduler.factory.SchedulerFactory;
import org.jetbrains.annotations.NotNull;

class BuilderImpl implements SchedulaAPI.Builder {

    private SchedulerFactory schedulerFactory;

    BuilderImpl() {
    }

    @Override
    public SchedulaAPI.@NotNull Builder factory(@NotNull SchedulerFactory factory) {
        this.schedulerFactory = factory;

        return this;
    }

    @Override
    public @NotNull SchedulaAPI build() {
        if (schedulerFactory == null) throw new NullPointerException("factory not initialized!");

        return () -> schedulerFactory.create();
    }
}
