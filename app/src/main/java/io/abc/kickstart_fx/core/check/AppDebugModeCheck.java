package io.abc.kickstart_fx.core.check;

import io.abc.kickstart_fx.core.AppLogs;
import io.abc.kickstart_fx.core.AppProperties;
import io.abc.kickstart_fx.util.ThreadHelper;

public class AppDebugModeCheck {

    public static void printIfNeeded() {
        if (!AppProperties.get().isImage() || !AppLogs.get().getLogLevel().equals("trace")) {
            return;
        }

        var out = AppLogs.get().getOriginalSysOut();
        var msg =
                """

                  ****************************************
                  * You are running in debug mode!       *
                  * The debug console output can contain *
                  * sensitive information and secrets.   *
                  * Don't share this output via an       *
                  * untrusted website or service.        *
                  ****************************************
                  """;
        out.println(msg);
        ThreadHelper.sleep(1000);
    }
}
