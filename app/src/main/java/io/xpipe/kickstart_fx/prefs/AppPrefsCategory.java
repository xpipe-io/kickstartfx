package io.xpipe.kickstart_fx.prefs;

import io.xpipe.kickstart_fx.comp.Comp;
import io.xpipe.kickstart_fx.platform.LabelGraphic;

public abstract class AppPrefsCategory {

    protected int getCompWidth() {
        return 600;
    }

    protected boolean show() {
        return true;
    }

    protected abstract String getId();

    protected abstract LabelGraphic getIcon();

    protected abstract Comp<?> create();
}
