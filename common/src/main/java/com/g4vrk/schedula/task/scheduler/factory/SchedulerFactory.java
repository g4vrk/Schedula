package com.g4vrk.schedula.task.scheduler.factory;

import com.g4vrk.schedula.Factory;
import com.g4vrk.schedula.task.scheduler.TaskScheduler;
import org.jetbrains.annotations.NotNull;

/**
 * Factory creating platform-specific {@link TaskScheduler} instances.
 *
 * @param <E> the entity type accepted by created schedulers
 */
public interface SchedulerFactory<E> extends Factory<@NotNull TaskScheduler<E>> {

}
