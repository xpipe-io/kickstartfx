package io.abc_def.kickstart_fx.core;

import io.abc_def.kickstart_fx.issue.ErrorEventFactory;
import io.abc_def.kickstart_fx.util.OsType;

import java.io.IOException;

public abstract class AppBeacon {

    private static AppBeacon INSTANCE;

    public abstract boolean isExistingBeaconRunning();

    public abstract void sendRequest(AppBeaconMessage message) throws IOException;

    public static void init() {
        try {
            INSTANCE = OsType.ofLocal() == OsType.WINDOWS ? new AppNamedPipeBeacon() : new AppSocketBeacon();
            INSTANCE.start();
        } catch (Exception ex) {
            // Not terminal!
            // We can still continue without the running server
            ErrorEventFactory.fromThrowable("Unable to start beacon", ex)
                    .handle();
        }
    }

    public static void reset() {
        if (INSTANCE != null) {
            INSTANCE.stop();
            INSTANCE = null;
        }
    }

    public static AppBeacon get() {
        return INSTANCE;
    }

    protected abstract void stop();

    protected abstract void start() throws IOException;
}
