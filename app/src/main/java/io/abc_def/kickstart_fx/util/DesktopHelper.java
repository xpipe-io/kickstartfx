package io.abc_def.kickstart_fx.util;

import io.abc_def.kickstart_fx.issue.ErrorEventFactory;

import com.sun.jna.platform.win32.Shell32;
import com.sun.jna.platform.win32.ShellAPI;
import com.sun.jna.platform.win32.User32;

import java.awt.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class DesktopHelper {

    private static boolean isSupported(Desktop.Action action) {
        // Desktop.getDesktop() throws if the desktop is not supported at all, e.g. on minimal linux systems
        return Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(action);
    }

    public static void openUrlInBrowser(String uri) {
        if (uri == null) {
            return;
        }

        URI parsed;
        try {
            parsed = URI.create(uri);
        } catch (IllegalArgumentException e) {
            ErrorEventFactory.fromThrowable("Invalid URI: " + uri, e.getCause() != null ? e.getCause() : e)
                    .handle();
            return;
        }

        if (!isSupported(Desktop.Action.BROWSE)) {
            if (OsType.ofLocal() == OsType.LINUX) {
                LocalExec.executeAsync("xdg-open", parsed.toString());
                return;
            }
        }

        // This can be a blocking operation
        ThreadHelper.runAsync(() -> {
            try {
                Desktop.getDesktop().browse(parsed);
                return;
            } catch (Exception e) {
                // Some basic linux systems have trouble with the API call
                ErrorEventFactory.fromThrowable(e)
                        .expected()
                        .omitted(OsType.ofLocal() == OsType.LINUX)
                        .handle();
            }

            if (OsType.ofLocal() == OsType.LINUX) {
                LocalExec.executeAsync("xdg-open", parsed.toString());
            }
        });
    }

    public static void browseFile(Path file) {
        if (file == null || !Files.exists(file)) {
            return;
        }

        if (!isSupported(Desktop.Action.OPEN)) {
            if (OsType.ofLocal() == OsType.LINUX) {
                LocalExec.executeAsync("xdg-open", file.toString());
                return;
            }
        }

        // This can be a blocking operation
        ThreadHelper.runAsync(() -> {
            if (isSupported(Desktop.Action.OPEN)) {
                try {
                    Desktop.getDesktop().open(file.toFile());
                    return;
                } catch (Exception e) {
                    // Some basic linux systems have trouble with the API call
                    ErrorEventFactory.fromThrowable(e)
                            .expected()
                            .omitted(OsType.ofLocal() == OsType.LINUX)
                            .handle();
                }
            }

            if (OsType.ofLocal() == OsType.LINUX) {
                LocalExec.executeAsync("xdg-open", file.toString());
            }
        });
    }

    public static void browseFileInDirectory(Path file) {
        if (file == null || !Files.exists(file)) {
            return;
        }

        // This can be a blocking operation
        ThreadHelper.runAsync(() -> {
            // Windows does not support Action.BROWSE_FILE_DIR
            if (OsType.ofLocal() == OsType.WINDOWS) {
                // Explorer does not support single quotes, so use normal quotes
                if (Files.isDirectory(file)) {
                    LocalExec.executeAsync("explorer", "\"" + file + "\"");
                } else {
                    LocalExec.executeAsync("explorer", "/select,", "\"" + file + "\"");
                }
                return;
            }

            // Linux does not support Action.BROWSE_FILE_DIR
            if (OsType.ofLocal() == OsType.LINUX) {
                var action = Files.isDirectory(file)
                        ? "org.freedesktop.FileManager1.ShowFolders"
                        : "org.freedesktop.FileManager1.ShowItems";
                var args = List.of(
                        "dbus-send",
                        "--session",
                        "--print-reply",
                        "--dest=org.freedesktop.FileManager1",
                        "--type=method_call",
                        "/org/freedesktop/FileManager1",
                        action,
                        "array:string:" + file.toUri(),
                        "string:");
                try {
                    var success = LocalExec.readStdoutIfPossible(args.toArray(String[]::new))
                            .isPresent();
                    if (success) {
                        return;
                    }
                } catch (Exception e) {
                    ErrorEventFactory.fromThrowable(e).omit().handle();
                }
            }

            if (!isSupported(Desktop.Action.BROWSE_FILE_DIR)) {
                browseFile(file.getParent());
                return;
            }

            try {
                Desktop.getDesktop().browseFileDirectory(file.toFile());
            } catch (Exception e) {
                // Some basic linux systems have trouble with the API call
                ErrorEventFactory.fromThrowable(e)
                        .expected()
                        .omitted(OsType.ofLocal() == OsType.LINUX)
                        .handle();
                if (OsType.ofLocal() == OsType.LINUX) {
                    browseFile(file.getParent());
                }
            }
        });
    }

    public static void openWithAnyApplication(Path localFile) {
        try {
            switch (OsType.ofLocal()) {
                case OsType.Windows ignored -> {
                    // See https://learn.microsoft.com/en-us/windows/win32/api/shellapi/ns-shellapi-shellexecuteinfoa
                    var struct = new ShellAPI.SHELLEXECUTEINFO();
                    struct.fMask = 0x100 | 0xC;
                    struct.lpVerb = "openas";
                    struct.lpFile = localFile.toString();
                    struct.nShow = User32.SW_SHOWDEFAULT;
                    Shell32.INSTANCE.ShellExecuteEx(struct);
                }
                case OsType.Linux ignored -> throw new UnsupportedOperationException();
                case OsType.MacOs ignored -> throw new UnsupportedOperationException();
            }
        } catch (Throwable e) {
            ErrorEventFactory.fromThrowable("Unable to open file " + localFile, e)
                    .handle();
        }
    }
}
