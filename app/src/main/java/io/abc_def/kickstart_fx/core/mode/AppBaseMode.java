package io.abc_def.kickstart_fx.core.mode;

import io.abc_def.kickstart_fx.core.*;
import io.abc_def.kickstart_fx.core.check.*;
import io.abc_def.kickstart_fx.core.window.AppDialog;
import io.abc_def.kickstart_fx.core.window.AppMainWindow;
import io.abc_def.kickstart_fx.core.window.AppWindowTitle;
import io.abc_def.kickstart_fx.issue.TrackEvent;
import io.abc_def.kickstart_fx.platform.PlatformInit;
import io.abc_def.kickstart_fx.platform.PlatformState;
import io.abc_def.kickstart_fx.prefs.AppPrefs;
import io.abc_def.kickstart_fx.update.UpdateAvailableDialog;
import io.abc_def.kickstart_fx.util.GlobalTimer;
import io.abc_def.kickstart_fx.util.WindowsRegistry;

public class AppBaseMode extends AppOperationMode {

    private boolean initialized;

    @Override
    public boolean isSupported() {
        return true;
    }

    @Override
    public String getId() {
        return "background";
    }

    @Override
    public void onSwitchTo() {
        if (initialized) {
            return;
        }

        // For debugging error handling
        // if (true) throw new IllegalStateException();

        TrackEvent.info("Initializing base mode components ...");
        AppMainWindow.loadingText("initializingApp");
        AppWindowTitle.init();
        AppPathCorruptCheck.check();
        AppWindowsTempCheck.check();
        AppDirectoryPermissionsCheck.checkDirectory(AppSystemInfo.ofCurrent().getTemp());
        WindowsRegistry.init();
        AppJavaOptionsCheck.check();
        // AppBeaconServer.init();
        AppLayoutModel.init();

        // If we downloaded an update, and decided to no longer automatically update, don't remind us!
        // You can still update manually in the about tab
        if (AppPrefs.get().automaticallyUpdate().get()) {
            UpdateAvailableDialog.showIfNeeded(true);
        }

        AppRosettaCheck.check();
        AppWindowsArmCheck.check();
        AppMainWindow.loadingText("loadingUserInterface");
        PlatformInit.init(true);
        AppImages.init();
        AppMainWindow.initContent();

        TrackEvent.info("Waiting for startup dialogs to close");
        AppDialog.waitForAllDialogsClose();

        AppConfigurationDialog.showIfNeeded();

        TrackEvent.info("Finished base components initialization");
        initialized = true;
    }

    @Override
    public void onSwitchFrom() {}

    @Override
    public void finalTeardown() {
        TrackEvent.withInfo("Base mode shutdown started").build();
        AppPrefs.reset();
        AppLayoutModel.reset();
        AppTheme.reset();
        PlatformState.reset();
        AppResources.reset();
        AppDataLock.unlock();
        GlobalTimer.reset();
        TrackEvent.info("Base mode shutdown finished");
    }
}
