package com.g4vrk.schedula.fabric.impl.task.runner;

import com.g4vrk.schedula.task.Task;
import com.g4vrk.schedula.task.TickSchedule;
import com.g4vrk.schedula.task.scheduler.TaskScheduler;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Implements Schedula task scheduling on a Fabric Minecraft server.
 */
public final class FabricScheduler implements TaskScheduler<Entity> {

    private static final long MILLIS_PER_TICK = 50L;

    private static final Map<MinecraftServer, ServerState> SERVERS = Collections.synchronizedMap(new WeakHashMap<>());

    static {

        ServerTickEvents.END_SERVER_TICK.register(FabricScheduler::tick);
        ServerLifecycleEvents.SERVER_STOPPING.register(FabricScheduler::stop);

    }

    private final MinecraftServer server;
    private final ServerState state;

    /**
     * Creates a scheduler for the supplied server. Schedulers for the same server share its tick and async task
     * state, and all scheduled work is cancelled when that server begins stopping.
     *
     * @param server the server whose main thread and lifecycle back this scheduler
     */
    public FabricScheduler(
            final @NotNull MinecraftServer server
    ) {

        this.server = Objects.requireNonNull(server, "server");
        this.state = state(server);

    }

    @Override
    public @NotNull Task schedule(
            final @NotNull Runnable runnable,
            final @NotNull TickSchedule tickSchedule
    ) {

        Objects.requireNonNull(runnable, "runnable");
        final FabricTask task = new FabricTask(
                state,
                runnable,
                tickSchedule,
                null,
                true
        );
        state.schedule(task);
        return task;

    }

    @Override
    public @NotNull Task schedule(
            final @NotNull Consumer<Task> consumer,
            final @NotNull TickSchedule tickSchedule
    ) {

        Objects.requireNonNull(consumer, "consumer");
        final FabricTask task = new FabricTask(
                state,
                null,
                tickSchedule,
                null,
                true
        );
        task.runnable = () -> consumer.accept(task);
        state.schedule(task);
        return task;

    }

    @Override
    public @NotNull Task scheduleAsync(
            final @NotNull Runnable runnable,
            final @NotNull TickSchedule tickSchedule
    ) {

        Objects.requireNonNull(runnable, "runnable");
        final FabricTask task = new FabricTask(
                state,
                runnable,
                tickSchedule,
                null,
                false
        );
        state.scheduleAsync(task);
        return task;

    }

    @Override
    public @NotNull Task scheduleAsync(
            final @NotNull Consumer<Task> consumer,
            final @NotNull TickSchedule tickSchedule
    ) {

        Objects.requireNonNull(consumer, "consumer");
        final FabricTask task = new FabricTask(
                state,
                null,
                tickSchedule,
                null,
                false
        );
        task.runnable = () -> consumer.accept(task);
        state.scheduleAsync(task);
        return task;

    }

    @Override
    public @NotNull Task scheduleEntity(
            final @NotNull Entity entity,
            final @NotNull Runnable runnable,
            final @NotNull TickSchedule tickSchedule
    ) {

        requireServerEntity(entity);
        Objects.requireNonNull(runnable, "runnable");
        final FabricTask task = new FabricTask(
                state,
                runnable,
                tickSchedule,
                entity,
                true
        );
        state.schedule(task);
        return task;

    }

    @Override
    public @NotNull Task scheduleEntity(
            final @NotNull Entity entity,
            final @NotNull Consumer<Task> consumer,
            final @NotNull TickSchedule tickSchedule
    ) {

        requireServerEntity(entity);
        Objects.requireNonNull(consumer, "consumer");
        final FabricTask task = new FabricTask(
                state,
                null,
                tickSchedule,
                entity,
                true
        );
        task.runnable = () -> consumer.accept(task);
        state.schedule(task);
        return task;

    }

    private void requireServerEntity(
            final @NotNull Entity entity
    ) {

        Objects.requireNonNull(entity, "entity");
        if (entity.level().getServer() != server) {
            throw new IllegalArgumentException("Entity does not belong to this server");
        }

    }

    private static @NotNull ServerState state(
            final @NotNull MinecraftServer server
    ) {

        synchronized (SERVERS) {
            return SERVERS.computeIfAbsent(server, ignored -> new ServerState());
        }

    }

    private static void tick(
            final @NotNull MinecraftServer server
    ) {

        final ServerState state;
        synchronized (SERVERS) {
            state = SERVERS.get(server);
        }
        if (state != null) {
            state.tick();
        }

    }

    private static void stop(
            final @NotNull MinecraftServer server
    ) {

        final ServerState state;
        synchronized (SERVERS) {
            state = SERVERS.get(server);
        }
        if (state != null) {
            state.stop();
        }

    }

    /**
     * Holds scheduled work and the async executor for one server lifecycle.
     */
    private static final class ServerState {

        private final Set<FabricTask> tasks = new HashSet<>();

        private ScheduledThreadPoolExecutor asyncExecutor;
        private boolean stopped;

        private synchronized void schedule(
                final @NotNull FabricTask task
        ) {

            ensureRunning();
            tasks.add(task);

        }

        private synchronized void scheduleAsync(
                final @NotNull FabricTask task
        ) {

            ensureRunning();
            final long initialDelay = ticksToMillis(task.tickSchedule.getDelay());
            final long period = task.tickSchedule.isRepeating()
                    ? ticksToMillis(task.tickSchedule.getPeriod())
                    : 0L;
            final ScheduledThreadPoolExecutor executor = asyncExecutor();
            tasks.add(task);
            try {
                final ScheduledFuture<?> future = task.tickSchedule.isRepeating()
                        ? executor.scheduleAtFixedRate(
                                task::runAsync,
                                initialDelay,
                                period,
                                TimeUnit.MILLISECONDS
                        )
                        : executor.schedule(task::runAsync, initialDelay, TimeUnit.MILLISECONDS);
                task.future(future);
            } catch (final RuntimeException exception) {
                tasks.remove(task);
                task.cancelled = true;
                throw exception;
            }

        }

        private void tick() {

            final FabricTask[] snapshot;
            synchronized (this) {
                if (stopped) {
                    return;
                }
                snapshot = tasks.toArray(new FabricTask[0]);
            }

            for (final FabricTask task : snapshot) {
                task.tick();
            }

        }

        private void remove(
                final @NotNull FabricTask task
        ) {

            synchronized (this) {
                tasks.remove(task);
            }

        }

        private void stop() {

            final ArrayList<FabricTask> pending;
            final ScheduledThreadPoolExecutor executor;
            synchronized (this) {
                if (stopped) {
                    return;
                }
                stopped = true;
                pending = new ArrayList<>(tasks);
                tasks.clear();
                executor = asyncExecutor;
            }

            for (final FabricTask task : pending) {
                task.cancel();
            }
            if (executor != null) {
                executor.shutdownNow();
            }

        }

        private ScheduledThreadPoolExecutor asyncExecutor() {

            if (asyncExecutor == null) {
                final ThreadFactory threadFactory = runnable -> {
                    final Thread thread = new Thread(runnable, "Schedula-Fabric-Async");
                    thread.setDaemon(true);
                    return thread;
                };
                asyncExecutor = new ScheduledThreadPoolExecutor(1, threadFactory);
                asyncExecutor.setRemoveOnCancelPolicy(true);
            }
            return asyncExecutor;

        }

        private void ensureRunning() {

            if (stopped) {
                throw new IllegalStateException("The Fabric server is stopping");
            }

        }

    }

    /**
     * Adapts a Schedula task to the Fabric server tick or async executor.
     */
    private static final class FabricTask implements Task {

        private final ServerState state;
        private final TickSchedule tickSchedule;
        private final Entity entity;
        private final boolean sync;

        private volatile Runnable runnable;
        private volatile ScheduledFuture<?> future;
        private volatile boolean cancelled;
        private long ticksUntilRun;

        private FabricTask(
                final @NotNull ServerState state,
                final Runnable runnable,
                final @NotNull TickSchedule tickSchedule,
                final Entity entity,
                final boolean sync
        ) {

            this.state = state;
            this.runnable = runnable;
            this.tickSchedule = Objects.requireNonNull(tickSchedule, "tickSchedule");
            this.entity = entity;
            this.sync = sync;
            this.ticksUntilRun = Math.max(1L, tickSchedule.getDelay());

        }

        private void tick() {

            if (!sync || cancelled) {
                return;
            }
            if (entity != null && entity.isRemoved()) {
                cancel();
                return;
            }
            if (ticksUntilRun > 1L) {
                ticksUntilRun--;
                return;
            }

            try {
                runnable.run();
            } finally {
                if (tickSchedule.isRepeating() && !cancelled) {
                    ticksUntilRun = tickSchedule.getPeriod();
                } else {
                    state.remove(this);
                }
            }

        }

        private void runAsync() {

            if (cancelled) {
                return;
            }
            try {
                runnable.run();
            } catch (final RuntimeException | Error exception) {
                state.remove(this);
                throw exception;
            } finally {
                if (!tickSchedule.isRepeating()) {
                    state.remove(this);
                }
            }

        }

        private void future(
                final @NotNull ScheduledFuture<?> future
        ) {

            this.future = future;
            if (cancelled) {
                future.cancel(false);
            }

        }

        @Override
        public void cancel() {

            cancelled = true;
            final ScheduledFuture<?> future = this.future;
            if (future != null) {
                future.cancel(false);
            }
            state.remove(this);

        }

        @Override
        public boolean isCancelled() {

            final ScheduledFuture<?> future = this.future;
            return cancelled || future != null && future.isCancelled();

        }

        @Override
        public boolean sync() {

            return sync;

        }

    }

    private static long ticksToMillis(
            final long ticks
    ) {

        return Math.multiplyExact(ticks, MILLIS_PER_TICK);

    }

}
