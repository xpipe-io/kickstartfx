package io.xpipe.kickstart_fx.core.check;

import io.xpipe.kickstart_fx.core.AppSystemInfo;
import io.xpipe.kickstart_fx.issue.ErrorEventFactory;
import io.xpipe.kickstart_fx.util.OsType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;

public class AppWindowsTempCheck {

    private static void checkTemp(String tmpdir) {
        Path dir = null;
        if (tmpdir != null) {
            try {
                dir = Path.of(tmpdir);
            } catch (InvalidPathException ignored) {
            }
        }

        if (dir == null || !Files.exists(dir) || !Files.isDirectory(dir)) {
            ErrorEventFactory.fromThrowable(new IOException("Specified temporary directory " + tmpdir
                            + ", set via the environment variable %TEMP% is invalid."))
                    .term()
                    .expected()
                    .handle();
        }
    }

    public static void check() {
        if (OsType.ofLocal() != OsType.WINDOWS) {
            return;
        }

        checkTemp(AppSystemInfo.ofWindows().getTemp().toString());
    }
}
