package com.g4vrk.schedula.api;

import com.g4vrk.schedula.task.scheduler.TaskScheduler;
import com.g4vrk.schedula.task.scheduler.factory.SchedulerFactory;
import org.jetbrains.annotations.NotNull;

/**
 * Platform-independent entry point for creating {@link TaskScheduler} instances.
 *
 * @param <E> the entity type accepted by created schedulers
 */
public interface SchedulaAPI<E> {

    @NotNull TaskScheduler<E> createScheduler();

    static <E> @NotNull Builder<E> builder() {

        return new BuilderImpl<>();

    }

    interface Builder<E> {

        @NotNull Builder<E> factory(
                final @NotNull SchedulerFactory<E> factory
        );

        @NotNull SchedulaAPI<E> build();

    }

}
