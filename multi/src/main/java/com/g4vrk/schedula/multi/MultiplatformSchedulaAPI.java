package com.g4vrk.schedula.multi;

import com.g4vrk.schedula.api.SchedulaAPI;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public interface MultiplatformSchedulaAPI extends SchedulaAPI {

    static @NotNull Builder builder() {
        return new BuilderImpl();
    }

    interface Builder extends SchedulaAPI.Builder {

        @NotNull Builder autoDetectFactoryFor(@NotNull Plugin plugin);

        @Override
        @NotNull MultiplatformSchedulaAPI build();

    }

}
