package io.abc_def.kickstart_fx.prefs;

import io.abc_def.kickstart_fx.comp.Comp;
import io.abc_def.kickstart_fx.comp.base.ChoiceComp;
import io.abc_def.kickstart_fx.platform.LabelGraphic;
import io.abc_def.kickstart_fx.platform.OptionsBuilder;

public class SystemCategory extends AppPrefsCategory {

    @Override
    protected String getId() {
        return "system";
    }

    @Override
    protected LabelGraphic getIcon() {
        return new LabelGraphic.IconGraphic("mdi2d-desktop-classic");
    }

    public Comp<?> create() {
        var prefs = AppPrefs.get();
        var builder = new OptionsBuilder();
        builder.addTitle("system")
                .sub(new OptionsBuilder()
                        .pref(prefs.startupBehaviour)
                        .addComp(ChoiceComp.ofTranslatable(
                                        prefs.startupBehaviour,
                                        PrefsChoiceValue.getSupported(StartupBehaviour.class),
                                        false)
                                .maxWidth(300.0))
                        .pref(prefs.closeBehaviour)
                        .addComp(ChoiceComp.ofTranslatable(
                                        prefs.closeBehaviour,
                                        PrefsChoiceValue.getSupported(CloseBehaviour.class),
                                        false)
                                .maxWidth(300.0)));
        return builder.buildComp();
    }
}
