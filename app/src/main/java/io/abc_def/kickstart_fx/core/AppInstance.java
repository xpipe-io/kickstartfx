package io.abc_def.kickstart_fx.core;

import io.abc_def.kickstart_fx.core.beacon.AppBeacon;
import io.abc_def.kickstart_fx.core.beacon.AppBeaconMessage;
import io.abc_def.kickstart_fx.core.mode.AppOperationMode;
import io.abc_def.kickstart_fx.issue.ErrorEventFactory;
import io.abc_def.kickstart_fx.issue.TrackEvent;
import io.abc_def.kickstart_fx.util.OsType;
import io.abc_def.kickstart_fx.util.ThreadHelper;

import java.awt.*;
import java.util.List;

public class AppInstance {

    public static void init() {
        var reachable = AppBeacon.get() != null && AppBeacon.get().isExistingBeaconRunning();
        if (!reachable) {
            checkDataLock();
            return;
        }

        try {
            var inputs = AppProperties.get().getArguments().getOpenArgs();
            // Assume that we want to open the GUI if we launched again
            AppBeacon.get().sendRequest(AppBeaconMessage.FocusRequest.builder().build());
            if (!inputs.isEmpty()) {
                AppBeacon.get()
                        .sendRequest(AppBeaconMessage.OpenRequest.builder()
                                .arguments(inputs)
                                .build());
            }
        } catch (Exception ex) {
            ErrorEventFactory.fromThrowable(ex).handle();
            // Only continue if there isn't actually another instance running
            checkDataLock();
            return;
        }

        if (OsType.ofLocal() == OsType.MACOS) {
            Desktop.getDesktop().setOpenURIHandler(e -> {
                try {
                    AppBeacon.get()
                            .sendRequest(AppBeaconMessage.OpenRequest.builder()
                                    .arguments(List.of(e.getURI().toString()))
                                    .build());
                } catch (Exception ex) {
                    ErrorEventFactory.fromThrowable(ex).expected().omit().handle();
                }
            });
            ThreadHelper.sleep(1000);
        }
        TrackEvent.info("Another instance is already running. Quitting ...");
        AppOperationMode.halt(1);
    }

    private static void checkDataLock() {
        // Even in case we are unable to reach another beacon server
        // there might be another instance running, for example
        // starting up
        if (!AppDataLock.hasLock()) {
            TrackEvent.info("Data directory " + AppProperties.get().getDataDir().toString()
                    + " is already locked. Is another instance running?");
            AppOperationMode.halt(1);
        }
    }
}
