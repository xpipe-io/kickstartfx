package io.abc_def.kickstart_fx.core.check;

import io.abc_def.kickstart_fx.core.AppInstallation;
import io.abc_def.kickstart_fx.util.LocalExec;
import io.abc_def.kickstart_fx.util.OsType;

public class AppSystemFontCheck {

    public static void init() {
        if (OsType.ofLocal() != OsType.LINUX) {
            return;
        }

        if (hasFonts()) {
            return;
        }

        System.setProperty(
                "prism.fontdir",
                AppInstallation.ofCurrent()
                        .getBaseInstallationPath()
                        .resolve("fonts")
                        .toString());
        System.setProperty("prism.embeddedfonts", "true");
    }

    private static boolean hasFonts() {
        var out = LocalExec.readStdoutIfPossible("fc-match");
        return out.isPresent() && !out.get().isBlank();
    }
}
