package com.g4vrk.schedula.task;

import org.jetbrains.annotations.NotNull;

public class DummyTask implements Task {

    private static final DummyTask INSTANCE = new DummyTask();

    private DummyTask() {
    }

    public static @NotNull DummyTask get() {
        return INSTANCE;
    }

    @Override
    public void cancel() {
        // nothing
    }

    @Override
    public boolean isCancelled() {
        return true;
    }

    @Override
    public boolean sync() {
        return true;
    }
}
