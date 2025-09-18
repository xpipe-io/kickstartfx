package io.abc_def.kickstart_fx.prefs;

import io.abc_def.kickstart_fx.core.mode.AppOperationMode;
import io.abc_def.kickstart_fx.core.mode.AppOperationModeSelection;

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
    BACKGROUND("app.startInBackground", AppOperationModeSelection.BACKGROUND) {};

    private final String id;
    private final AppOperationModeSelection mode;
}
