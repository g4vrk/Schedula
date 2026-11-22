package com.g4vrk.schedula.multi;

import com.g4vrk.schedula.api.SchedulaAPI;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public interface MultiplatformSchedulaAPI extends SchedulaAPI<Entity> {

    static @NotNull Builder builder() {

        return new BuilderImpl();

    }

    interface Builder extends SchedulaAPI.Builder<Entity> {

        @NotNull Builder autoDetectFactoryFor(
                final @NotNull Plugin plugin
        );

        @Override
        @NotNull MultiplatformSchedulaAPI build();

    }

}
