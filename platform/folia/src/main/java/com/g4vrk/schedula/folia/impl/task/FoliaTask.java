package com.g4vrk.schedula.folia.impl.task;

import com.g4vrk.schedula.task.Task;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.jetbrains.annotations.NotNull;

public final class FoliaTask implements Task {

    private final ScheduledTask task;

    private final boolean sync;

    public FoliaTask(
            @NotNull ScheduledTask task,
            boolean sync
    ) {
        this.task = task;
        this.sync = sync;
    }

    @Override
    public void cancel() {
        task.cancel();
    }

    @Override
    public boolean isCancelled() {
        return task.isCancelled();
    }

    @Override
    public boolean sync() {
        return sync;
    }

}