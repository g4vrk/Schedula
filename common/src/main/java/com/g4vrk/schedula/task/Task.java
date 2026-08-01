package com.g4vrk.schedula.task;

public interface Task {

    void cancel();

    boolean isCancelled();

    boolean sync();

}
