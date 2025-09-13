package io.abc_def.kickstart_fx.core;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.*;
import com.sun.jna.ptr.IntByReference;
import io.abc_def.kickstart_fx.issue.ErrorEventFactory;
import io.abc_def.kickstart_fx.issue.TrackEvent;
import io.abc_def.kickstart_fx.util.JacksonMapper;
import io.abc_def.kickstart_fx.util.OsType;
import io.abc_def.kickstart_fx.util.ThreadHelper;
import lombok.Value;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public abstract class AppBeacon {

    private static AppBeacon INSTANCE;

    public abstract boolean isExistingBeaconRunning();

    public abstract void sendRequest(AppBeaconMessage message) throws IOException;

    public static void init() {
        try {
            INSTANCE = OsType.ofLocal() == OsType.WINDOWS ? new AppWindowsBeacon() : null;
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
