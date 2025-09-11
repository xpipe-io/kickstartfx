package io.abc.kickstart_fx.prefs;

import io.abc.kickstart_fx.core.mode.AppOperationMode;
import io.abc.kickstart_fx.core.mode.AppOperationModeSelection;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StartupBehaviour implements PrefsChoiceValue {
    GUI("app.startGui", AppOperationModeSelection.GUI) {},
    TRAY("app.startInTray", AppOperationModeSelection.TRAY) {
        public boolean isSelectable() {
            return AppOperationMode.TRAY.isSupported();
        }
    },
    BACKGROUND("app.startInBackground", AppOperationModeSelection.BACKGROUND) {
        public boolean isSelectable() {
            return !AppOperationMode.TRAY.isSupported();
        }
    };

    private final String id;
    private final AppOperationModeSelection mode;
}
