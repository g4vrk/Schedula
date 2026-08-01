package com.g4vrk.schedula;

@FunctionalInterface
public interface Factory<T> {

    T create();

}
