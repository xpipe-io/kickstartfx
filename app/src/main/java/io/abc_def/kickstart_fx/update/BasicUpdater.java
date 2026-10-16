package io.abc_def.kickstart_fx.update;

import io.abc_def.kickstart_fx.comp.base.ModalButton;
import io.abc_def.kickstart_fx.core.*;
import io.abc_def.kickstart_fx.core.mode.AppOperationMode;
import io.abc_def.kickstart_fx.util.Hyperlinks;
import io.abc_def.kickstart_fx.util.LocalExec;
import io.abc_def.kickstart_fx.util.OsType;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class BasicUpdater extends UpdateHandler {

    public BasicUpdater(boolean thread) {
        super(thread);
    }

    @Override
    public List<ModalButton> createActions() {
        // Allow for installing in development with no runtime image
        var canInstall = OsType.ofLocal() == OsType.WINDOWS
                && (AppDistributionType.get() == AppDistributionType.NATIVE_INSTALLATION
                        || !AppProperties.get().isRuntimeImage());

        var list = new ArrayList<ModalButton>();
        list.add(new ModalButton("ignore", null, true, false));
        list.add(new ModalButton(
                "checkOutUpdate",
                () -> {
                    var rel = getLastUpdateCheckResult().getValue();
                    if (rel == null) {
                        return;
                    }

                    Hyperlinks.open(rel.getReleaseUrl());
                },
                !canInstall,
                true));

        // On Windows, we can implement a simple autoupdater
        // This is however very basic
        if (canInstall) {
            list.add(new ModalButton(
                    "installUpdate",
                    () -> {
                        var rel = getLastUpdateCheckResult().getValue();
                        if (rel == null) {
                            return;
                        }

                        var url = rel.getRepository() + "/releases/download/" + rel.getVersion() + "/"
                                + AppNames.ofCurrent().getDistName() + "-installer-windows-"
                                + AppProperties.get().getArch() + ".msi";
                        // Note that the downloaded installer is not verified against a checksum
                        // Consider adding verification for production use
                        var systemWide = AppInstallation.ofWindows().isSystemWide();
                        var properties = "MSIFASTINSTALL=7 DISABLEROLLBACK=1" + (systemWide ? " ALLUSERS=1" : "");
                        var logFile = AppLogs.get()
                                .getSessionLogsDirectory()
                                .getParent()
                                .resolve("installer.log");
                        var command = "start \"\" /wait msiexec /i \"" + url + "\" /lv \"" + logFile + "\" /qb "
                                + properties + "&start \"\" \""
                                + AppInstallation.ofCurrent().getExecutablePath()
                                + "\"";
                        AppOperationMode.executeAfterShutdown(() -> {
                            if (LocalExec.executeAsync("cmd", "/c", command) == null) {
                                throw new IOException("Failed to launch update installer");
                            }
                        });
                    },
                    false,
                    true));
        }
        return list;
    }

    private boolean isUpdate(String releaseVersion) {
        var current = AppVersion.parse(stripPrefix(AppProperties.get().getVersion()));
        var release = AppVersion.parse(stripPrefix(releaseVersion));
        if (current.isEmpty() || release.isEmpty()) {
            if (!stripPrefix(AppProperties.get().getVersion()).equals(stripPrefix(releaseVersion))) {
                event("Release has a different unparsable version");
                return true;
            }

            return false;
        }

        if (release.get().greaterThan(current.get())) {
            event("Release has a newer version");
            return true;
        }

        return false;
    }

    private static String stripPrefix(String version) {
        return version.startsWith("v") ? version.substring(1) : version;
    }

    public synchronized AvailableRelease refreshUpdateCheckImpl() throws Exception {
        var found = AppReleases.getMarkedLatestRelease();
        if (found.isEmpty()) {
            lastUpdateCheckResult.setValue(null);
            return null;
        }

        var rel = found.get();
        event("Determined latest suitable release " + rel.getTagName());
        var isUpdate = isUpdate(rel.getTagName());
        var val = isUpdate
                ? new AvailableRelease(
                        AppProperties.get().getVersion(),
                        AppDistributionType.get().getId(),
                        rel.getTagName(),
                        rel.getHtmlUrl().toString(),
                        rel.getOwner().getHtmlUrl().toString(),
                        "## Changes in v" + rel.getTagName() + "\n\n" + rel.getBody(),
                        Instant.now())
                : null;
        lastUpdateCheckResult.setValue(val);
        return val;
    }
}
