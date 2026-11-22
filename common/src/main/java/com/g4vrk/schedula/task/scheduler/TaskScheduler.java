package com.g4vrk.schedula.task.scheduler;

import com.g4vrk.schedula.task.Task;
import com.g4vrk.schedula.task.TickSchedule;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Schedules global, asynchronous, and entity tasks through the underlying platform.
 *
 * @param <E> the entity type accepted by this scheduler
 */
public interface TaskScheduler<E> {

    /**
     * Schedules a task in the platform's global execution context.
     *
     * @param runnable the task callback
     * @param tickSchedule the task timing
     * @return the task handle
     */
    @NotNull Task schedule(
            final @NotNull Runnable runnable,
            final @NotNull TickSchedule tickSchedule
    );

    /**
     * Schedules a task globally and provides its handle to the callback.
     *
     * @param consumer the task callback
     * @param tickSchedule the task timing
     * @return the task handle
     */
    @NotNull Task schedule(
            final @NotNull Consumer<Task> consumer,
            final @NotNull TickSchedule tickSchedule
    );

    /**
     * Schedules a task in the platform's asynchronous execution context.
     *
     * @param runnable the task callback
     * @param tickSchedule the task timing
     * @return the task handle
     */
    @NotNull Task scheduleAsync(
            final @NotNull Runnable runnable,
            final @NotNull TickSchedule tickSchedule
    );

    /**
     * Schedules a task asynchronously and provides its handle to the callback.
     *
     * @param consumer the task callback
     * @param tickSchedule the task timing
     * @return the task handle
     */
    @NotNull Task scheduleAsync(
            final @NotNull Consumer<Task> consumer,
            final @NotNull TickSchedule tickSchedule
    );

    /**
     * Schedules a task in the execution context of the given entity.
     *
     * @param entity the entity that determines the task's execution context
     * @param runnable the task callback
     * @param tickSchedule the task timing
     * @return the task handle
     */
    @NotNull Task scheduleEntity(
            final @NotNull E entity,
            final @NotNull Runnable runnable,
            final @NotNull TickSchedule tickSchedule
    );

    /**
     * Schedules an entity task and provides its handle to the callback.
     *
     * @param entity the entity that determines the task's execution context
     * @param consumer the task callback
     * @param tickSchedule the task timing
     * @return the task handle
     */
    @NotNull Task scheduleEntity(
            final @NotNull E entity,
            final @NotNull Consumer<Task> consumer,
            final @NotNull TickSchedule tickSchedule
    );

    /**
     * Returns a scheduler that accepts another entity type and delegates scheduling here.
     * The mapper runs on the calling thread when an entity task is submitted.
     *
     * @param entityMapper a function that converts an entity to a non-null delegate entity
     * @param <T> the entity type accepted by the returned scheduler
     * @return a scheduler backed by this scheduler
     */
    default <T> @NotNull TaskScheduler<T> mapEntity(final @NotNull Function<? super T, ? extends E> entityMapper) {

        Objects.requireNonNull(entityMapper, "entityMapper");

        return new TaskScheduler<>() {

            @Override
            public @NotNull Task schedule(
                    final @NotNull Runnable runnable,
                    final @NotNull TickSchedule tickSchedule
            ) {

                return TaskScheduler.this.schedule(runnable, tickSchedule);

            }

            @Override
            public @NotNull Task schedule(
                    final @NotNull Consumer<Task> consumer,
                    final @NotNull TickSchedule tickSchedule
            ) {

                return TaskScheduler.this.schedule(consumer, tickSchedule);

            }

            @Override
            public @NotNull Task scheduleAsync(
                    final @NotNull Runnable runnable,
                    final @NotNull TickSchedule tickSchedule
            ) {

                return TaskScheduler.this.scheduleAsync(runnable, tickSchedule);

            }

            @Override
            public @NotNull Task scheduleAsync(
                    final @NotNull Consumer<Task> consumer,
                    final @NotNull TickSchedule tickSchedule
            ) {

                return TaskScheduler.this.scheduleAsync(consumer, tickSchedule);

            }

            @Override
            public @NotNull Task scheduleEntity(
                    final @NotNull T entity,
                    final @NotNull Runnable runnable,
                    final @NotNull TickSchedule tickSchedule
            ) {

                return TaskScheduler.this.scheduleEntity(entityMapper.apply(entity), runnable, tickSchedule);

            }

            @Override
            public @NotNull Task scheduleEntity(
                    final @NotNull T entity,
                    final @NotNull Consumer<Task> consumer,
                    final @NotNull TickSchedule tickSchedule
            ) {

                return TaskScheduler.this.scheduleEntity(entityMapper.apply(entity), consumer, tickSchedule);

            }

        };

    }

}
