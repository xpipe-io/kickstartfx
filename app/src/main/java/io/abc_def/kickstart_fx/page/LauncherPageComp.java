package io.abc_def.kickstart_fx.page;

import atlantafx.sampler.page.showcase.BlueprintsPage;
import io.abc_def.kickstart_fx.comp.Comp;
import io.abc_def.kickstart_fx.comp.SimpleComp;
import io.abc_def.kickstart_fx.comp.base.ButtonComp;
import io.abc_def.kickstart_fx.core.AppI18n;
import io.abc_def.kickstart_fx.platform.OptionsBuilder;
import io.abc_def.kickstart_fx.prefs.AppPrefs;
import io.abc_def.kickstart_fx.util.ThreadHelper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.Region;
import org.kordamp.ikonli.javafx.FontIcon;

public class LauncherPageComp extends SimpleComp {

    private final StringProperty repoUrl = new SimpleStringProperty();
    private final StringProperty refName = new SimpleStringProperty("master");
    private final StringProperty properties = new SimpleStringProperty();

    @Override
    protected Region createSimple() {
        var launchButton = new ButtonComp(AppI18n.observable("launch"), new FontIcon("mdi2p-play"), () -> {
            ThreadHelper.runFailableAsync(() -> {
                launch();
            });
        })
                .padding(new Insets(6, 11, 6, 5))
                .apply(struc -> struc.get().setAlignment(Pos.CENTER_LEFT))
                .disable(repoUrl.isNull().or(refName.isNull()));


        var options = new OptionsBuilder()
                .addTitle("remoteLaunch")
                .name("about")
                .description("remoteLaunchDescription")
                .addComp(Comp.empty())
                .name("repoUrl")
                .description("repoUrlDescription")
                .addString(repoUrl)
                .name("refName")
                .description("refNameDescription")
                .addString(refName)
                .name("additionalProperties")
                .description("additionalPropertiesDescription")
                .addString(properties)
                .addComp(launchButton);
        return options.buildComp().createRegion();
    }

    private void launch() {

    }
}
