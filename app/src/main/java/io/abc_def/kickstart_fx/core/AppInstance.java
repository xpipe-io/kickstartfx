package io.abc_def.kickstart_fx.core;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinError;
import com.sun.jna.platform.win32.WinNT;
import io.abc_def.kickstart_fx.core.mode.AppOperationMode;
import io.abc_def.kickstart_fx.issue.ErrorEventFactory;
import io.abc_def.kickstart_fx.issue.TrackEvent;
import io.abc_def.kickstart_fx.util.OsType;
import io.abc_def.kickstart_fx.util.ThreadHelper;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class AppInstance {

    public static void init() throws IOException {
        var reachable = AppBeacon.get().isExistingBeaconRunning();
        if (!reachable) {
            // Even in case we are unable to reach another beacon server
            // there might be another instance running, for example
            // starting up
            if (!AppDataLock.lock()) {
                TrackEvent.info(
                        "Data directory " + AppProperties.get().getDataDir().toString()
                                + " is already locked. Is another instance running?");
                AppOperationMode.halt(1);
            }

            // We are good to start up!
            return;
        }

        try {
            var inputs = AppProperties.get().getArguments().getOpenArgs();
            // Assume that we want to open the GUI if we launched again
            AppBeacon.get().sendRequest(new AppBeaconMessage.FocusRequest());
            if (!inputs.isEmpty()) {
                AppBeacon.get().sendRequest(new AppBeaconMessage.OpenRequest(inputs));
            }
        } catch (Exception ex) {
            ErrorEventFactory.fromThrowable(ex).handle();
            return;
        }

        if (OsType.ofLocal() == OsType.MACOS) {
            Desktop.getDesktop().setOpenURIHandler(e -> {
                try {
                    AppBeacon.get().sendRequest(new AppBeaconMessage.OpenRequest(List.of(e.getURI().toString())));
                } catch (Exception ex) {
                    ErrorEventFactory.fromThrowable(ex).expected().omit().handle();
                }
            });
            ThreadHelper.sleep(1000);
        }
        TrackEvent.info("Another instance is already running. Quitting ...");
        AppOperationMode.halt(1);
    }
}
