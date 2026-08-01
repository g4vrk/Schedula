package com.g4vrk.schedula.folia.impl.task.runner;

import com.g4vrk.schedula.folia.impl.task.FoliaTask;
import com.g4vrk.schedula.task.DummyTask;
import com.g4vrk.schedula.task.Task;
import com.g4vrk.schedula.task.scheduler.AbstractScheduler;
import com.g4vrk.schedula.task.TickSchedule;
import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler;
import io.papermc.paper.threadedregions.scheduler.RegionScheduler;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public final class FoliaScheduler extends AbstractScheduler {

    private final GlobalRegionScheduler globalRegionScheduler;

    private final RegionScheduler regionScheduler;

    private final AsyncScheduler asyncScheduler;

    public FoliaScheduler(
            @NotNull Plugin plugin
    ) {
        super(plugin);

        this.regionScheduler = super.getServer().getRegionScheduler();
        this.globalRegionScheduler = super.getServer().getGlobalRegionScheduler();
        this.asyncScheduler = super.getServer().getAsyncScheduler();
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

        return adapt(task, false);
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

        return adapt(task, false);
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
