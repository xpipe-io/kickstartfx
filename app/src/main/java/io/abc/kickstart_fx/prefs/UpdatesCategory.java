package io.abc.kickstart_fx.prefs;

import io.abc.kickstart_fx.comp.Comp;
import io.abc.kickstart_fx.platform.LabelGraphic;
import io.abc.kickstart_fx.platform.OptionsBuilder;

public class UpdatesCategory extends AppPrefsCategory {

    @Override
    protected String getId() {
        return "updates";
    }

    @Override
    protected LabelGraphic getIcon() {
        return new LabelGraphic.IconGraphic("mdi2d-download-box-outline");
    }

    public Comp<?> create() {
        var prefs = AppPrefs.get();
        var builder = new OptionsBuilder();
        builder.addTitle("updates")
                .sub(new OptionsBuilder()
                        .pref(prefs.automaticallyCheckForUpdates)
                        .addToggle(prefs.automaticallyCheckForUpdates));
        return builder.buildComp();
    }
}
