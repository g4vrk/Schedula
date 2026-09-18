package com.g4vrk.schedula.bukkit.impl.task.runner;

import com.g4vrk.schedula.bukkit.impl.task.PaperTask;
import com.g4vrk.schedula.task.Task;
import com.g4vrk.schedula.task.TickSchedule;
import com.g4vrk.schedula.task.scheduler.AbstractScheduler;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class PaperScheduler extends AbstractScheduler {

    private final BukkitScheduler scheduler;

    public PaperScheduler(@NotNull Plugin plugin) {
        super(plugin);
        this.scheduler = plugin.getServer().getScheduler();
    }

    private @NotNull Task schedule(
            @NotNull Runnable runnable,
            @NotNull TickSchedule tickSchedule,
            boolean async
    ) {

        final long delay = tickSchedule.getDelay();
        final long period = tickSchedule.getPeriod();

        if (tickSchedule.isRepeating()) {

            return new PaperTask(
                    async
                            ? scheduler.runTaskTimerAsynchronously(
                            getPlugin(),
                            runnable,
                            delay,
                            period
                    )
                            : scheduler.runTaskTimer(
                            getPlugin(),
                            runnable,
                            delay,
                            period
                    )
            );
        }

        if (tickSchedule.isDelayed()) {

            return new PaperTask(
                    async
                            ? scheduler.runTaskLaterAsynchronously(
                            getPlugin(),
                            runnable,
                            delay
                    )
                            : scheduler.runTaskLater(
                            getPlugin(),
                            runnable,
                            delay
                    )
            );
        }

        return new PaperTask(
                async
                        ? scheduler.runTaskAsynchronously(
                        getPlugin(),
                        runnable
                )
                        : scheduler.runTask(
                        getPlugin(),
                        runnable
                )
        );
    }

    private @NotNull Task schedule(
            @NotNull Consumer<Task> consumer,
            @NotNull TickSchedule tickSchedule,
            boolean async
    ) {
        final ConsumerTask task = new ConsumerTask(async);

        final Runnable runnable = () -> consumer.accept(task);

        final Task delegate = schedule(
                runnable,
                tickSchedule,
                async
        );

        task.setDelegate(delegate);

        return task;
    }

    @Override
    public @NotNull Task schedule(
            @NotNull Runnable runnable,
            @NotNull TickSchedule tickSchedule
    ) {
        return schedule(
                runnable,
                tickSchedule,
                false
        );
    }

    @Override
    public @NotNull Task schedule(
            @NotNull Consumer<Task> consumer,
            @NotNull TickSchedule tickSchedule
    ) {
        return schedule(
                consumer,
                tickSchedule,
                false
        );
    }

    @Override
    public @NotNull Task scheduleAsync(
            @NotNull Runnable runnable,
            @NotNull TickSchedule tickSchedule
    ) {
        return schedule(
                runnable,
                tickSchedule,
                true
        );
    }

    @Override
    public @NotNull Task scheduleAsync(
            @NotNull Consumer<Task> consumer,
            @NotNull TickSchedule tickSchedule
    ) {
        return schedule(
                consumer,
                tickSchedule,
                true
        );
    }

    @Override
    public @NotNull Task scheduleEntity(
            @NotNull Entity entity,
            @NotNull Runnable runnable,
            @NotNull TickSchedule tickSchedule
    ) {
        return schedule(
                runnable,
                tickSchedule,
                false
        );
    }

    @Override
    public @NotNull Task scheduleEntity(
            @NotNull Entity entity,
            @NotNull Consumer<Task> consumer,
            @NotNull TickSchedule tickSchedule
    ) {
        return schedule(
                consumer,
                tickSchedule,
                false
        );
    }

    @Override
    public @NotNull Task scheduleLocation(
            @NotNull Location location,
            @NotNull Runnable runnable,
            @NotNull TickSchedule tickSchedule
    ) {
        return schedule(
                runnable,
                tickSchedule,
                false
        );
    }

    @Override
    public @NotNull Task scheduleLocation(
            @NotNull Location location,
            @NotNull Consumer<Task> consumer,
            @NotNull TickSchedule tickSchedule
    ) {
        return schedule(
                consumer,
                tickSchedule,
                false
        );
    }

    private static final class ConsumerTask implements Task {

        private final boolean sync;

        private volatile Task delegate;

        private volatile boolean cancelled;

        private ConsumerTask(
                final boolean sync
        ) {
            this.sync = sync;
        }

        private void setDelegate(
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
            if (cancelled) {
                return true;
            }

            final Task delegate = this.delegate;

            return delegate != null && delegate.isCancelled();
        }

        @Override
        public boolean sync() {
            return sync;
        }
    }
}