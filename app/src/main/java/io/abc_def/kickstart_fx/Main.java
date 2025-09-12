package io.abc_def.kickstart_fx;

import io.abc_def.kickstart_fx.core.AppProperties;
import io.abc_def.kickstart_fx.core.mode.AppOperationMode;

public class Main {

    public static void main(String[] args) {
        if (args.length == 1 && args[0].equals("version")) {
            AppProperties.init(args);
            System.out.println(AppProperties.get().getVersion());
            return;
        }

        AppOperationMode.init(args);
    }
}
