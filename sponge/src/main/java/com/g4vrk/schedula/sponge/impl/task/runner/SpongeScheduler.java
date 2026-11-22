package com.g4vrk.schedula.sponge.impl.task.runner;

import com.g4vrk.schedula.task.Task;
import com.g4vrk.schedula.task.TickSchedule;
import com.g4vrk.schedula.task.scheduler.TaskScheduler;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.scheduler.ScheduledTask;
import org.spongepowered.api.scheduler.Scheduler;
import org.spongepowered.api.util.Ticks;
import org.spongepowered.plugin.PluginContainer;

import java.util.function.Consumer;

/**
 * Sponge implementation of Schedula's global, asynchronous, and entity task scheduling.
 */
public final class SpongeScheduler implements TaskScheduler<ServerPlayer> {

    private final PluginContainer plugin;
    private final Scheduler syncScheduler;
    private final Scheduler asyncScheduler;

    public SpongeScheduler(
            final @NotNull PluginContainer plugin
    ) {

        this.plugin = plugin;
        this.syncScheduler = Sponge.server().scheduler();
        this.asyncScheduler = Sponge.asyncScheduler();

    }

    @Override
    public @NotNull Task schedule(
            final @NotNull Runnable runnable,
            final @NotNull TickSchedule tickSchedule
    ) {

        return schedule(syncScheduler, runnable, tickSchedule, true);

    }

    @Override
    public @NotNull Task schedule(
            final @NotNull Consumer<Task> consumer,
            final @NotNull TickSchedule tickSchedule
    ) {

        final SpongeTask task = new SpongeTask(true);
        schedule(syncScheduler, () -> consumer.accept(task), tickSchedule, task);
        return task;

    }

    @Override
    public @NotNull Task scheduleAsync(
            final @NotNull Runnable runnable,
            final @NotNull TickSchedule tickSchedule
    ) {

        return schedule(asyncScheduler, runnable, tickSchedule, false);

    }

    @Override
    public @NotNull Task scheduleAsync(
            final @NotNull Consumer<Task> consumer,
            final @NotNull TickSchedule tickSchedule
    ) {

        final SpongeTask task = new SpongeTask(false);
        schedule(asyncScheduler, () -> consumer.accept(task), tickSchedule, task);
        return task;

    }

    @Override
    public @NotNull Task scheduleEntity(
            final @NotNull ServerPlayer entity,
            final @NotNull Runnable runnable,
            final @NotNull TickSchedule tickSchedule
    ) {

        return schedule(syncScheduler, runnable, tickSchedule, true);

    }

    @Override
    public @NotNull Task scheduleEntity(
            final @NotNull ServerPlayer entity,
            final @NotNull Consumer<Task> consumer,
            final @NotNull TickSchedule tickSchedule
    ) {

        final SpongeTask task = new SpongeTask(true);
        schedule(syncScheduler, () -> consumer.accept(task), tickSchedule, task);
        return task;

    }

    private @NotNull Task schedule(
            final @NotNull Scheduler scheduler,
            final @NotNull Runnable runnable,
            final @NotNull TickSchedule tickSchedule,
            final boolean sync
    ) {

        final SpongeTask task = new SpongeTask(sync);
        schedule(scheduler, runnable, tickSchedule, task);
        return task;

    }

    private void schedule(
            final @NotNull Scheduler scheduler,
            final @NotNull Runnable runnable,
            final @NotNull TickSchedule tickSchedule,
            final @NotNull SpongeTask task
    ) {

        final org.spongepowered.api.scheduler.Task.Builder builder = org.spongepowered.api.scheduler.Task.builder()
                .plugin(plugin)
                .execute(scheduledTask -> runnable.run());
        if (tickSchedule.isDelayed()) {
            builder.delay(Ticks.of(tickSchedule.getDelay()));
        }
        if (tickSchedule.isRepeating()) {
            builder.interval(Ticks.of(tickSchedule.getPeriod()));
        }
        task.delegate(scheduler.submit(builder.build()));

    }

    /**
     * Wraps a Sponge scheduler task and retains cancellation requests made before submission ends.
     */
    private static final class SpongeTask implements Task {

        private final boolean sync;
        private volatile ScheduledTask delegate;
        private volatile boolean cancelled;

        private SpongeTask(
                final boolean sync
        ) {

            this.sync = sync;

        }

        private void delegate(
                final @NotNull ScheduledTask delegate
        ) {

            this.delegate = delegate;
            if (cancelled) {
                delegate.cancel();
            }

        }

        @Override
        public void cancel() {

            cancelled = true;
            final ScheduledTask delegate = this.delegate;
            if (delegate != null) {
                delegate.cancel();
            }

        }

        @Override
        public boolean isCancelled() {

            final ScheduledTask delegate = this.delegate;
            return cancelled || delegate != null && delegate.isCancelled();

        }

        @Override
        public boolean sync() {

            return sync;

        }

    }

}
