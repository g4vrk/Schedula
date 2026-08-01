package com.g4vrk.schedula.bukkit.impl.task;

import com.g4vrk.schedula.task.Task;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

public final class PaperTask implements Task {

    private final BukkitTask task;

    public PaperTask(
            @NotNull BukkitTask task
    ) {
        this.task = task;
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
        return task.isSync();
    }

}