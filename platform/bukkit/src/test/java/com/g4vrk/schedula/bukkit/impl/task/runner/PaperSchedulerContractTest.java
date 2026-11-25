package com.g4vrk.schedula.bukkit.impl.task.runner;

import com.g4vrk.schedula.task.Task;
import com.g4vrk.schedula.task.TickSchedule;
import org.bukkit.Server;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies {@link PaperScheduler} synchronization metadata and consumer task delegation.
 */
final class PaperSchedulerContractTest {

    @Test
    void consumerTasksPreserveSyncVsAsyncFlag() {

        final List<Runnable> queued = new ArrayList<>();
        final AtomicBoolean nativeSyncFlag = new AtomicBoolean(true);
        final BukkitScheduler bukkitScheduler = proxyScheduler(queued, nativeSyncFlag);
        final Plugin plugin = proxyPlugin(bukkitScheduler);
        final PaperScheduler scheduler = new PaperScheduler(plugin);

        final AtomicReference<Task> syncCallbackTask = new AtomicReference<>();
        final Task syncTask = scheduler.schedule(syncCallbackTask::set, TickSchedule.instant());
        assertTrue(syncTask.sync(), "Sync consumer task must report sync() == true");
        queued.get(0).run();
        assertNotNull(syncCallbackTask.get());
        assertTrue(syncCallbackTask.get().sync(), "Delivered sync consumer task must report sync() == true");

        nativeSyncFlag.set(false);
        final AtomicReference<Task> asyncCallbackTask = new AtomicReference<>();
        final Task asyncTask = scheduler.scheduleAsync(asyncCallbackTask::set, TickSchedule.instant());
        assertFalse(asyncTask.sync(), "Async consumer task must report sync() == false");
        queued.get(1).run();
        assertNotNull(asyncCallbackTask.get());
        assertFalse(asyncCallbackTask.get().sync(), "Delivered async consumer task must report sync() == false");

        final Entity dummyEntity = (Entity) Proxy.newProxyInstance(
                Entity.class.getClassLoader(),
                new Class<?>[] {Entity.class},
                (proxy, method, args) -> null
        );
        final Task entityTask = scheduler.scheduleEntity(dummyEntity, task -> {

        }, TickSchedule.instant());
        assertTrue(entityTask.sync(), "Entity consumer task must report sync() == true");

    }

    @Test
    void consumerTaskPropagatesPreDelegateCancellation() {

        final AtomicBoolean nativeCancelled = new AtomicBoolean(false);
        final AtomicReference<Task> capturedInCallback = new AtomicReference<>();
        final BukkitScheduler bukkitScheduler = (BukkitScheduler) Proxy.newProxyInstance(
                BukkitScheduler.class.getClassLoader(),
                new Class<?>[] {BukkitScheduler.class},
                (proxy, method, args) -> {
                    if (method.getName().startsWith("runTask") && args != null && args.length >= 2
                            && args[1] instanceof Runnable runnable) {
                        runnable.run();
                        return proxyCancellableNativeTask(true, nativeCancelled);
                    }
                    return null;
                }
        );
        final PaperScheduler scheduler = new PaperScheduler(proxyPlugin(bukkitScheduler));
        final Task task = scheduler.schedule(current -> {
            capturedInCallback.set(current);
            current.cancel();
        }, TickSchedule.delayed(2));

        assertNotNull(capturedInCallback.get());
        assertTrue(task.isCancelled());
        assertTrue(nativeCancelled.get(), "Cancelling during synchronous invocation must cancel native delegate once attached");

    }

    private static BukkitScheduler proxyScheduler(
            final List<Runnable> queued,
            final AtomicBoolean syncFlag
    ) {

        return (BukkitScheduler) Proxy.newProxyInstance(
                BukkitScheduler.class.getClassLoader(),
                new Class<?>[] {BukkitScheduler.class},
                (proxy, method, args) -> {
                    if (method.getName().startsWith("runTask") && args != null && args.length >= 2) {
                        for (final Object arg : args) {
                            if (arg instanceof Runnable runnable) {
                                queued.add(runnable);
                            }
                        }
                        return proxyNativeTask(syncFlag.get());
                    }
                    return null;
                }
        );

    }

    private static Plugin proxyPlugin(
            final BukkitScheduler scheduler
    ) {

        final Server server = (Server) Proxy.newProxyInstance(
                Server.class.getClassLoader(),
                new Class<?>[] {Server.class},
                (proxy, method, args) -> method.getName().equals("getScheduler") ? scheduler : null
        );
        return (Plugin) Proxy.newProxyInstance(
                Plugin.class.getClassLoader(),
                new Class<?>[] {Plugin.class},
                (proxy, method, args) -> method.getName().equals("getServer") ? server : null
        );

    }

    private static BukkitTask proxyNativeTask(
            final boolean sync
    ) {

        return proxyCancellableNativeTask(sync, new AtomicBoolean());

    }

    private static BukkitTask proxyCancellableNativeTask(
            final boolean sync,
            final AtomicBoolean cancelled
    ) {

        return (BukkitTask) Proxy.newProxyInstance(
                BukkitTask.class.getClassLoader(),
                new Class<?>[] {BukkitTask.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "isSync" -> sync;
                    case "isCancelled" -> cancelled.get();
                    case "cancel" -> {
                        cancelled.set(true);
                        yield null;
                    }
                    default -> null;
                }
        );

    }

}
