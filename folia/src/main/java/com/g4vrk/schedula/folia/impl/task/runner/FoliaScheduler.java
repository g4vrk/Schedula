package com.g4vrk.schedula.folia.impl.task.runner;

import com.g4vrk.schedula.folia.impl.task.FoliaTask;
import com.g4vrk.schedula.task.DummyTask;
import com.g4vrk.schedula.task.Task;
import com.g4vrk.schedula.task.TickSchedule;
import com.g4vrk.schedula.task.scheduler.RegionTaskScheduler;
import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler;
import io.papermc.paper.threadedregions.scheduler.RegionScheduler;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public final class FoliaScheduler implements RegionTaskScheduler<Entity, Location> {

    private final Plugin plugin;

    private final GlobalRegionScheduler globalRegionScheduler;

    private final RegionScheduler regionScheduler;

    private final AsyncScheduler asyncScheduler;

    public FoliaScheduler(
            final @NotNull Plugin plugin
    ) {

        this.plugin = plugin;
        this.regionScheduler = plugin.getServer().getRegionScheduler();
        this.globalRegionScheduler = plugin.getServer().getGlobalRegionScheduler();
        this.asyncScheduler = plugin.getServer().getAsyncScheduler();

    }

    private @NotNull Plugin getPlugin() {

        return plugin;

    }

    @Override
    public @NotNull Task schedule(
            @NotNull Runnable runnable,
            @NotNull TickSchedule tickSchedule
    ) {
        return runGlobally(
                runnable,
                tickSchedule
        );
    }

    @Override
    public @NotNull Task schedule(
            @NotNull Consumer<Task> consumer,
            @NotNull TickSchedule tickSchedule
    ) {
        return runGlobally(
                consumer,
                tickSchedule
        );
    }

    @Override
    public @NotNull Task scheduleAsync(
            @NotNull Runnable runnable,
            @NotNull TickSchedule tickSchedule
    ) {

        final ScheduledTask task;

        if (tickSchedule.isRepeating()) {

            task = asyncScheduler.runAtFixedRate(
                    getPlugin(),
                    scheduledTask -> runnable.run(),
                    positiveMillis(tickSchedule.getDelay()),
                    positiveMillis(tickSchedule.getPeriod()),
                    TimeUnit.MILLISECONDS
            );

        } else if (tickSchedule.isDelayed()) {

            task = asyncScheduler.runDelayed(
                    getPlugin(),
                    scheduledTask -> runnable.run(),
                    positiveMillis(tickSchedule.getDelay()),
                    TimeUnit.MILLISECONDS
            );

        } else {

            task = asyncScheduler.runNow(
                    getPlugin(),
                    scheduledTask -> runnable.run()
            );
        }

        return adapt(task, false);
    }

    @Override
    public @NotNull Task scheduleAsync(
            @NotNull Consumer<Task> consumer,
            @NotNull TickSchedule tickSchedule
    ) {
        final ScheduledTask task;

        if (tickSchedule.isRepeating()) {

            task = asyncScheduler.runAtFixedRate(
                    getPlugin(),
                    scheduledTask -> consumer.accept(
                            adapt(scheduledTask, false)
                    ),
                    positiveMillis(tickSchedule.getDelay()),
                    positiveMillis(tickSchedule.getPeriod()),
                    TimeUnit.MILLISECONDS
            );

        } else if (tickSchedule.isDelayed()) {

            task = asyncScheduler.runDelayed(
                    getPlugin(),
                    scheduledTask -> consumer.accept(
                            adapt(scheduledTask, false)
                    ),
                    positiveMillis(tickSchedule.getDelay()),
                    TimeUnit.MILLISECONDS
            );

        } else {

            task = asyncScheduler.runNow(
                    getPlugin(),
                    scheduledTask -> consumer.accept(
                            adapt(scheduledTask, false)
                    )
            );
        }

        return adapt(task, false);
    }

    public @NotNull Task runGlobally(
            @NotNull Runnable runnable,
            @NotNull TickSchedule tickSchedule
    ) {

        final ScheduledTask task;

        if (tickSchedule.isRepeating()) {

            task = globalRegionScheduler.runAtFixedRate(
                    getPlugin(),
                    scheduledTask -> runnable.run(),
                    positiveTicks(tickSchedule.getDelay()),
                    positiveTicks(tickSchedule.getPeriod())
            );

        } else if (tickSchedule.isDelayed()) {

            task = globalRegionScheduler.runDelayed(
                    getPlugin(),
                    scheduledTask -> runnable.run(),
                    positiveTicks(tickSchedule.getDelay())
            );

        } else {

            task = globalRegionScheduler.run(
                    getPlugin(),
                    scheduledTask -> runnable.run()
            );
        }

        return adapt(task, true);
    }

    public @NotNull Task runGlobally(
            @NotNull Consumer<Task> consumer,
            @NotNull TickSchedule tickSchedule
    ) {
        final ScheduledTask task;

        if (tickSchedule.isRepeating()) {

            task = globalRegionScheduler.runAtFixedRate(
                    getPlugin(),
                    scheduledTask -> consumer.accept(
                            adapt(scheduledTask, true)
                    ),
                    positiveTicks(tickSchedule.getDelay()),
                    positiveTicks(tickSchedule.getPeriod())
            );

        } else if (tickSchedule.isDelayed()) {

            task = globalRegionScheduler.runDelayed(
                    getPlugin(),
                    scheduledTask -> consumer.accept(
                            adapt(scheduledTask, true)
                    ),
                    positiveTicks(tickSchedule.getDelay())
            );

        } else {

            task = globalRegionScheduler.run(
                    getPlugin(),
                    scheduledTask -> consumer.accept(
                            adapt(scheduledTask, true)
                    )
            );
        }

        return adapt(task, true);
    }

    @Override
    public @NotNull Task scheduleEntity(
            @NotNull Entity entity,
            @NotNull Runnable runnable,
            @NotNull TickSchedule tickSchedule
    ) {

        final ScheduledTask task;

        if (tickSchedule.isRepeating()) {

            task = entity.getScheduler().runAtFixedRate(
                    getPlugin(),
                    scheduledTask -> runnable.run(),
                    null,
                    positiveTicks(tickSchedule.getDelay()),
                    positiveTicks(tickSchedule.getPeriod())
            );

        } else if (tickSchedule.isDelayed()) {

            task = entity.getScheduler().runDelayed(
                    getPlugin(),
                    scheduledTask -> runnable.run(),
                    null,
                    positiveTicks(tickSchedule.getDelay())
            );

        } else {

            task = entity.getScheduler().run(
                    getPlugin(),
                    scheduledTask -> runnable.run(),
                    null
            );
        }

        if (task == null) {
            return DummyTask.get();
        }

        return adapt(task, true);
    }

    @Override
    public @NotNull Task scheduleEntity(
            @NotNull Entity entity,
            @NotNull Consumer<Task> consumer,
            @NotNull TickSchedule tickSchedule
    ) {
        final ScheduledTask task;

        if (tickSchedule.isRepeating()) {

            task = entity.getScheduler().runAtFixedRate(
                    getPlugin(),
                    scheduledTask -> consumer.accept(
                            adapt(scheduledTask, true)
                    ),
                    null,
                    positiveTicks(tickSchedule.getDelay()),
                    positiveTicks(tickSchedule.getPeriod())
            );

        } else if (tickSchedule.isDelayed()) {

            task = entity.getScheduler().runDelayed(
                    getPlugin(),
                    scheduledTask -> consumer.accept(
                            adapt(scheduledTask, true)
                    ),
                    null,
                    positiveTicks(tickSchedule.getDelay())
            );

        } else {

            task = entity.getScheduler().run(
                    getPlugin(),
                    scheduledTask -> consumer.accept(
                            adapt(scheduledTask, true)
                    ),
                    null
            );
        }

        if (task == null) {
            return DummyTask.get();
        }

        return adapt(task, true);
    }

    @Override
    public @NotNull Task scheduleLocation(
            @NotNull Location location,
            @NotNull Runnable runnable,
            @NotNull TickSchedule tickSchedule
    ) {

        final ScheduledTask task;

        if (tickSchedule.isRepeating()) {

            task = regionScheduler.runAtFixedRate(
                    getPlugin(),
                    location,
                    scheduledTask -> runnable.run(),
                    positiveTicks(tickSchedule.getDelay()),
                    positiveTicks(tickSchedule.getPeriod())
            );

        } else if (tickSchedule.isDelayed()) {

            task = regionScheduler.runDelayed(
                    getPlugin(),
                    location,
                    scheduledTask -> runnable.run(),
                    positiveTicks(tickSchedule.getDelay())
            );

        } else {

            task = regionScheduler.run(
                    getPlugin(),
                    location,
                    scheduledTask -> runnable.run()
            );
        }

        return adapt(task, true);
    }

    @Override
    public @NotNull Task scheduleLocation(
            @NotNull Location location,
            @NotNull Consumer<Task> consumer,
            @NotNull TickSchedule tickSchedule
    ) {
        final ScheduledTask task;

        if (tickSchedule.isRepeating()) {

            task = regionScheduler.runAtFixedRate(
                    getPlugin(),
                    location,
                    scheduledTask -> consumer.accept(
                            adapt(scheduledTask, true)
                    ),
                    positiveTicks(tickSchedule.getDelay()),
                    positiveTicks(tickSchedule.getPeriod())
            );

        } else if (tickSchedule.isDelayed()) {

            task = regionScheduler.runDelayed(
                    getPlugin(),
                    location,
                    scheduledTask -> consumer.accept(
                            adapt(scheduledTask, true)
                    ),
                    positiveTicks(tickSchedule.getDelay())
            );

        } else {

            task = regionScheduler.run(
                    getPlugin(),
                    location,
                    scheduledTask -> consumer.accept(
                            adapt(scheduledTask, true)
                    )
            );
        }

        return adapt(task, true);
    }

    private @NotNull FoliaTask adapt(
            final @NotNull ScheduledTask task,
            final boolean sync
    ) {
        return new FoliaTask(task, sync);
    }

    private long positiveTicks(
            final long ticks
    ) {
        return Math.max(1L, ticks);
    }

    private long positiveMillis(
            final long ticks
    ) {
        return Math.max(1L, Math.multiplyExact(ticks, 50L));
    }
}