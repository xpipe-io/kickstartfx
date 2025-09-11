package io.xpipe.kickstart_fx.prefs;

import io.xpipe.kickstart_fx.core.mode.AppOperationMode;
import io.xpipe.kickstart_fx.util.XPipeDaemonMode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StartupBehaviour implements PrefsChoiceValue {
    GUI("app.startGui", XPipeDaemonMode.GUI) {},
    TRAY("app.startInTray", XPipeDaemonMode.TRAY) {
        public boolean isSelectable() {
            return AppOperationMode.TRAY.isSupported();
        }
    },
    BACKGROUND("app.startInBackground", XPipeDaemonMode.BACKGROUND) {
        public boolean isSelectable() {
            return !AppOperationMode.TRAY.isSupported();
        }
    };

    private final String id;
    private final XPipeDaemonMode mode;
}
