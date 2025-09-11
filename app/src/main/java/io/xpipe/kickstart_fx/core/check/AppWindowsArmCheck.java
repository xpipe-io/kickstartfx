package io.xpipe.kickstart_fx.core.check;

import io.xpipe.kickstart_fx.core.AppProperties;
import io.xpipe.kickstart_fx.issue.ErrorEventFactory;
import io.xpipe.kickstart_fx.util.OsType;

public class AppWindowsArmCheck {

    public static void check() {
        if (OsType.ofLocal() != OsType.WINDOWS) {
            return;
        }

        if (!AppProperties.get().getArch().equals("x86_64")) {
            return;
        }

        var armProgramFiles = System.getenv("ProgramFiles(Arm)");
        if (armProgramFiles != null) {
            ErrorEventFactory.fromMessage("You are running the x86-64 version of XPipe on an ARM64 system."
                            + " There is a native build available that comes with much better performance."
                            + " Please install that one instead.")
                    .expected()
                    .handle();
        }
    }
}
