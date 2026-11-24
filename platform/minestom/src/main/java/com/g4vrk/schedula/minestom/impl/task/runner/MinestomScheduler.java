package com.g4vrk.schedula.minestom.impl.task.runner;

import com.g4vrk.schedula.task.Task;
import com.g4vrk.schedula.task.TickSchedule;
import com.g4vrk.schedula.task.scheduler.TaskScheduler;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Entity;
import net.minestom.server.timer.ExecutionType;
import net.minestom.server.timer.Scheduler;
import net.minestom.server.timer.SchedulerManager;
import net.minestom.server.timer.TaskSchedule;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Minestom implementation of Schedula's global, asynchronous, and entity task scheduling.
 */
public final class MinestomScheduler implements TaskScheduler<Entity> {

    private static final long MILLIS_PER_TICK = 50L;

    private final SchedulerManager scheduler;
    private final ScheduledExecutorService asyncExecutor;

    public MinestomScheduler() {

        this.scheduler = MinecraftServer.getSchedulerManager();
        this.asyncExecutor = Executors.newScheduledThreadPool(2, runnable -> {
            final Thread thread = new Thread(runnable, "Schedula-Minestom-Async");
            thread.setDaemon(true);
            return thread;
        });
        scheduler.buildShutdownTask(asyncExecutor::shutdownNow);

    }

    @Override
    public @NotNull Task schedule(
            final @NotNull Runnable runnable,
            final @NotNull TickSchedule tickSchedule
    ) {

        return schedule(scheduler, runnable, tickSchedule, true);

    }

    @Override
    public @NotNull Task schedule(
            final @NotNull Consumer<Task> consumer,
            final @NotNull TickSchedule tickSchedule
    ) {

        final MinestomTask task = new MinestomTask(true);
        final Task nativeTask = schedule(scheduler, () -> consumer.accept(task), tickSchedule, true);
        task.delegate(nativeTask);
        return task;

    }

    @Override
    public @NotNull Task scheduleAsync(
            final @NotNull Runnable runnable,
            final @NotNull TickSchedule tickSchedule
    ) {

        return scheduleAsyncInternal(runnable, tickSchedule);

    }

    @Override
    public @NotNull Task scheduleAsync(
            final @NotNull Consumer<Task> consumer,
            final @NotNull TickSchedule tickSchedule
    ) {

        final AsyncTask task = new AsyncTask();
        scheduleAsyncInternal(() -> consumer.accept(task), tickSchedule, task);
        return task;

    }

    @Override
    public @NotNull Task scheduleEntity(
            final @NotNull Entity entity,
            final @NotNull Runnable runnable,
            final @NotNull TickSchedule tickSchedule
    ) {

        return schedule(entity.scheduler(), runnable, tickSchedule, true);

    }

    @Override
    public @NotNull Task scheduleEntity(
            final @NotNull Entity entity,
            final @NotNull Consumer<Task> consumer,
            final @NotNull TickSchedule tickSchedule
    ) {

        final MinestomTask task = new MinestomTask(true);
        final Task nativeTask = schedule(entity.scheduler(), () -> consumer.accept(task), tickSchedule, true);
        task.delegate(nativeTask);
        return task;

    }

    private @NotNull Task schedule(
            final @NotNull Scheduler scheduler,
            final @NotNull Runnable runnable,
            final @NotNull TickSchedule tickSchedule,
            final boolean sync
    ) {

        final net.minestom.server.timer.Task.Builder builder = scheduler.buildTask(runnable)
                .executionType(ExecutionType.TICK_START);
        if (tickSchedule.isDelayed()) {
            builder.delay(ticks(tickSchedule.getDelay()));
        }
        if (tickSchedule.isRepeating()) {
            builder.repeat(ticks(tickSchedule.getPeriod()));
        }
        return new MinestomTask(builder.schedule(), sync);

    }

    private @NotNull Task scheduleAsyncInternal(
            final @NotNull Runnable runnable,
            final @NotNull TickSchedule tickSchedule
    ) {

        final AsyncTask task = new AsyncTask();
        scheduleAsyncInternal(runnable, tickSchedule, task);
        return task;

    }

    private void scheduleAsyncInternal(
            final @NotNull Runnable runnable,
            final @NotNull TickSchedule tickSchedule,
            final @NotNull AsyncTask task
    ) {

        final long initialDelay = ticksToMillis(tickSchedule.getDelay());
        final ScheduledFuture<?> future;
        if (tickSchedule.isRepeating()) {
            future = asyncExecutor.scheduleAtFixedRate(
                    runnable,
                    initialDelay,
                    ticksToMillis(tickSchedule.getPeriod()),
                    TimeUnit.MILLISECONDS
            );
        } else {
            future = asyncExecutor.schedule(runnable, initialDelay, TimeUnit.MILLISECONDS);
        }
        task.delegate(future);

    }

    private @NotNull TaskSchedule ticks(
            final long ticks
    ) {

        return TaskSchedule.duration(Duration.ofMillis(ticksToMillis(ticks)));

    }

    private long ticksToMillis(
            final long ticks
    ) {

        return Math.multiplyExact(ticks, MILLIS_PER_TICK);

    }

    /**
     * Wraps a Minestom or scheduled-executor task with Schedula's cancellation contract.
     */
    private static final class MinestomTask implements Task {

        private final boolean sync;
        private volatile Task delegate;
        private volatile boolean cancelled;

        private MinestomTask(
                final boolean sync
        ) {

            this.sync = sync;

        }

        private MinestomTask(
                final @NotNull net.minestom.server.timer.Task task,
                final boolean sync
        ) {

            this.sync = sync;
            this.delegate = new NativeTask(task, sync);

        }

        private void delegate(
                final @NotNull Task delegate
        ) {

            this.delegate = delegate;
            if (cancelled) {
                delegate.cancel();
            }

        }

        @Override
        public void cancel() {

            cancelled = true;
            final Task delegate = this.delegate;
            if (delegate != null) {
                delegate.cancel();
            }

        }

        @Override
        public boolean isCancelled() {

            final Task delegate = this.delegate;
            return cancelled || delegate != null && delegate.isCancelled();

        }

        @Override
        public boolean sync() {

            return sync;

        }

    }

    /**
     * Adapts a native Minestom task handle.
     */
    private static final class NativeTask implements Task {

        private final net.minestom.server.timer.Task task;
        private final boolean sync;
        private volatile boolean cancelled;

        private NativeTask(
                final @NotNull net.minestom.server.timer.Task task,
                final boolean sync
        ) {

            this.task = task;
            this.sync = sync;

        }

        @Override
        public void cancel() {

            cancelled = true;
            task.cancel();

        }

        @Override
        public boolean isCancelled() {

            return cancelled;

        }

        @Override
        public boolean sync() {

            return sync;

        }

    }

    /**
     * Adapts an asynchronous task handle and handles cancellation before scheduling completes.
     */
    private static final class AsyncTask implements Task {

        private volatile ScheduledFuture<?> delegate;
        private volatile boolean cancelled;

        private void delegate(
                final @NotNull ScheduledFuture<?> delegate
        ) {

            this.delegate = delegate;
            if (cancelled) {
                delegate.cancel(false);
            }

        }

        @Override
        public void cancel() {

            cancelled = true;
            final ScheduledFuture<?> delegate = this.delegate;
            if (delegate != null) {
                delegate.cancel(false);
            }

        }

        @Override
        public boolean isCancelled() {

            final ScheduledFuture<?> delegate = this.delegate;
            return cancelled || delegate != null && delegate.isCancelled();

        }

        @Override
        public boolean sync() {

            return false;

        }

    }

}
