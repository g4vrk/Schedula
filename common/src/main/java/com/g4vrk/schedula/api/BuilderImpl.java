package com.g4vrk.schedula.api;

import com.g4vrk.schedula.task.scheduler.factory.SchedulerFactory;
import org.jetbrains.annotations.NotNull;

class BuilderImpl<E> implements SchedulaAPI.Builder<E> {

    private SchedulerFactory<E> schedulerFactory;

    BuilderImpl() {

    }

    @Override
    public SchedulaAPI.@NotNull Builder<E> factory(
            final @NotNull SchedulerFactory<E> factory
    ) {

        this.schedulerFactory = factory;
        return this;

    }

    @Override
    public @NotNull SchedulaAPI<E> build() {

        if (schedulerFactory == null) {
            throw new NullPointerException("factory not initialized!");
        }
        return schedulerFactory::create;

    }

}
