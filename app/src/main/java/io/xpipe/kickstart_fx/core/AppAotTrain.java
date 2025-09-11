package io.xpipe.kickstart_fx.core;

import io.xpipe.kickstart_fx.core.mode.AppOperationMode;
import io.xpipe.kickstart_fx.util.ThreadHelper;
import io.xpipe.kickstart_fx.util.OsType;

public class AppAotTrain {

    public static void runTrainingMode() throws Throwable {
        // Linux runners don't support graphics
        if (OsType.ofLocal() == OsType.LINUX) {
            return;
        }

        AppOperationMode.switchToSyncOrThrow(AppOperationMode.GUI);
        ThreadHelper.sleep(1000);
        AppLayoutModel.get().selectBlueprints();
        ThreadHelper.sleep(1000);
        AppLayoutModel.get().selectMusicPlayer();
        ThreadHelper.sleep(1000);
        AppLayoutModel.get().selectFileBrowser();
        ThreadHelper.sleep(1000);
        AppLayoutModel.get().selectOverview();
        ThreadHelper.sleep(1000);
        AppLayoutModel.get().selectSettings();
        ThreadHelper.sleep(5000);
    }
}
