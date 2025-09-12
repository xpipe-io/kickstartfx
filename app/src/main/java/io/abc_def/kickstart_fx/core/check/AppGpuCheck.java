package io.abc_def.kickstart_fx.core.check;

import io.abc_def.kickstart_fx.core.AppProperties;
import io.abc_def.kickstart_fx.platform.PlatformState;
import io.abc_def.kickstart_fx.prefs.AppPrefs;

import javafx.application.ConditionalFeature;
import javafx.application.Platform;

public class AppGpuCheck {

    public static void check() {
        if (!AppProperties.get().isInitialLaunch()) {
            return;
        }

        // We might launch the platform due to an error early
        if (AppPrefs.get() == null) {
            return;
        }

        if (PlatformState.getCurrent() != PlatformState.RUNNING) {
            return;
        }

        if (Platform.isSupported(ConditionalFeature.SCENE3D)) {
            return;
        }

        AppPrefs.get().performanceMode.setValue(true);
    }
}
