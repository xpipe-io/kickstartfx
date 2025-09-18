package io.abc_def.kickstart_fx.core;

import io.abc_def.kickstart_fx.core.mode.AppOperationMode;
import io.abc_def.kickstart_fx.issue.TrackEvent;

import java.util.ArrayList;
import java.util.List;

public class AppOpenArguments {

    private static final List<String> bufferedArguments = new ArrayList<>();

    public static synchronized void init() {
        handleImpl(AppProperties.get().getArguments().getOpenArgs());
        handleImpl(bufferedArguments);
        bufferedArguments.clear();
    }

    public static synchronized void handle(List<String> arguments) {
        if (AppOperationMode.isInShutdown()) {
            return;
        }

        if (AppOperationMode.isInStartup()) {
            TrackEvent.withDebug("Buffering open arguments").elements(arguments).handle();
            bufferedArguments.addAll(arguments);
            return;
        }

        handleImpl(arguments);
    }

    private static synchronized void handleImpl(List<String> arguments) {
        if (arguments.isEmpty()) {
            return;
        }

        TrackEvent.withDebug("Handling arguments").elements(arguments).handle();

        // TODO: Handle any generic arguments passed to the app, e.g. file names to open
    }
}
