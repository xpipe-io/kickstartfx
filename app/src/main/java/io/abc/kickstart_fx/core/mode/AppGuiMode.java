package io.abc.kickstart_fx.core.mode;

import io.abc.kickstart_fx.core.window.AppMainWindow;
import io.abc.kickstart_fx.issue.TrackEvent;
import io.abc.kickstart_fx.platform.PlatformInit;
import io.abc.kickstart_fx.platform.PlatformThread;
import io.abc.kickstart_fx.util.OsType;

import javafx.application.Platform;
import javafx.stage.Stage;

public class AppGuiMode extends AppOperationMode {

    @Override
    public boolean isSupported() {
        // We force GUI to be supported and fail with a terminal
        // exception if we can't initialize the platform
        return true;
    }

    @Override
    public String getId() {
        return "gui";
    }

    @Override
    public void onSwitchFrom() {
        // If we are in an externally started shutdown hook, don't close the windows until the platform exits
        // That way, it is kept open to block for shutdowns on Windows systems
        if (OsType.ofLocal() != OsType.WINDOWS || !AppOperationMode.isInShutdownHook()) {
            Platform.runLater(() -> {
                TrackEvent.info("Closing windows");
                Stage.getWindows().stream().toList().forEach(w -> {
                    w.hide();
                });
            });
        }
    }

    @Override
    public void onSwitchTo() throws Throwable {
        AppOperationMode.BACKGROUND.onSwitchTo();
        PlatformInit.init(true);

        PlatformThread.runLaterIfNeededBlocking(() -> {
            AppMainWindow.get().show();
        });
    }

    @Override
    public void finalTeardown() throws Throwable {
        onSwitchFrom();
        BACKGROUND.finalTeardown();
    }
}
