package io.abc_def.kickstart_fx.core;

import io.abc_def.kickstart_fx.core.mode.AppOperationMode;
import io.abc_def.kickstart_fx.util.OsType;
import io.abc_def.kickstart_fx.util.ThreadHelper;

public class AppAotTrain {

    public static void runTrainingMode() throws Throwable {
        // Linux runners don't support graphics
        if (OsType.ofLocal() == OsType.LINUX) {
            return;
        }

        AppOperationMode.switchToSyncOrThrow(AppOperationMode.GUI);

        for (AppLayoutModel.Entry entry : AppLayoutModel.get().getEntries()) {
            AppLayoutModel.get().getSelected().setValue(entry);
            ThreadHelper.sleep(1000);
        }
    }
}
