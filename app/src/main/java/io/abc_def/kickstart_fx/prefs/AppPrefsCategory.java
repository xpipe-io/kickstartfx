package io.abc_def.kickstart_fx.prefs;

import io.abc_def.kickstart_fx.comp.Comp;
import io.abc_def.kickstart_fx.platform.LabelGraphic;

public abstract class AppPrefsCategory {

    public boolean show() {
        return true;
    }

    public abstract String getId();

    protected abstract LabelGraphic getIcon();

    public abstract Comp<?> create();
}
