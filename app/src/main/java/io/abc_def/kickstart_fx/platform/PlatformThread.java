package io.abc_def.kickstart_fx.platform;

import io.abc_def.kickstart_fx.core.mode.AppOperationMode;
import io.abc_def.kickstart_fx.issue.ErrorEventFactory;

import javafx.application.Platform;

import java.util.*;
import java.util.concurrent.CountDownLatch;

public class PlatformThread {

    private static boolean canRunPlatform() {
        if (PlatformState.getCurrent() != PlatformState.RUNNING) {
            return false;
        }

        return !AppOperationMode.isInShutdown();
    }

    public static void enterNestedEventLoop(Object key) {
        if (!Platform.canStartNestedEventLoop()) {
            return;
        }

        try {
            Platform.enterNestedEventLoop(key);
        } catch (IllegalStateException ex) {
            // We might be in an animation or layout call
            ErrorEventFactory.fromThrowable(ex).omit().expected().handle();
        }
    }

    public static void exitNestedEventLoop(Object key) {
        try {
            Platform.exitNestedEventLoop(key, null);
        } catch (IllegalArgumentException ex) {
            // The event loop might have died somehow
            // Or we passed an invalid key
            ErrorEventFactory.fromThrowable(ex).omit().expected().handle();
        }
    }

    public static void runNestedLoopIteration() {
        if (!Platform.canStartNestedEventLoop()) {
            return;
        }

        var key = new Object();
        Platform.runLater(() -> {
            exitNestedEventLoop(key);
        });
        enterNestedEventLoop(key);
    }

    public static void runLaterIfNeeded(Runnable r) {
        if (!canRunPlatform()) {
            return;
        }

        Runnable catcher = () -> {
            try {
                r.run();
            } catch (Throwable t) {
                ErrorEventFactory.fromThrowable(t).handle();
            }
        };

        if (Platform.isFxApplicationThread()) {
            catcher.run();
        } else {
            Platform.runLater(catcher);
        }
    }

    public static void runLaterIfNeededBlocking(Runnable r) {
        if (!canRunPlatform()) {
            return;
        }

        Runnable catcher = () -> {
            try {
                r.run();
            } catch (Throwable t) {
                ErrorEventFactory.fromThrowable(t).handle();
            }
        };

        if (!Platform.isFxApplicationThread()) {
            CountDownLatch latch = new CountDownLatch(1);
            Platform.runLater(() -> {
                catcher.run();
                latch.countDown();
            });
            try {
                latch.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        } else {
            catcher.run();
        }
    }
}
