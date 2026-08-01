package com.g4vrk.schedula.api;

import com.g4vrk.schedula.task.scheduler.Scheduler;
import com.g4vrk.schedula.task.scheduler.factory.SchedulerFactory;
import org.jetbrains.annotations.NotNull;

public interface SchedulaAPI {

    @NotNull Scheduler createScheduler();

    static @NotNull Builder builder() {
        return new BuilderImpl();
    }

    interface Builder {

        @NotNull Builder factory(@NotNull SchedulerFactory factory);

        @NotNull SchedulaAPI build();

    }

}
