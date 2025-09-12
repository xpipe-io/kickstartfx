package io.abc_def.kickstart_fx.core.check;

import io.abc_def.kickstart_fx.core.AppProperties;
import io.abc_def.kickstart_fx.issue.ErrorEventFactory;
import io.abc_def.kickstart_fx.util.LocalExec;
import io.abc_def.kickstart_fx.util.OsType;

public class AppRosettaCheck {

    public static void check() {
        if (OsType.ofLocal() != OsType.MACOS) {
            return;
        }

        if (!AppProperties.get().getArch().equals("x86_64")) {
            return;
        }

        var ret = LocalExec.readStdoutIfPossible("sysctl -n sysctl.proc_translated");
        if (ret.isEmpty()) {
            return;
        }

        if (ret.get().equals("1")) {
            ErrorEventFactory.fromMessage("You are running the Intel version on an Apple Silicon system."
                            + " There is a native build available that comes with much better performance."
                            + " Please install that one instead.")
                    .expected()
                    .handle();
        }
    }
}
