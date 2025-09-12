package io.abc_def.kickstart_fx.prefs;

import io.abc_def.kickstart_fx.comp.Comp;
import io.abc_def.kickstart_fx.platform.LabelGraphic;

public abstract class AppPrefsCategory {

    protected boolean show() {
        return true;
    }

    protected abstract String getId();

    protected abstract LabelGraphic getIcon();

    protected abstract Comp<?> create();
}
