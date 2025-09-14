package io.abc_def.kickstart_fx.core;

import io.abc_def.kickstart_fx.comp.base.ModalButton;
import io.abc_def.kickstart_fx.comp.base.ModalOverlay;
import io.abc_def.kickstart_fx.comp.base.ScrollComp;
import io.abc_def.kickstart_fx.core.window.AppDialog;
import io.abc_def.kickstart_fx.platform.OptionsBuilder;
import io.abc_def.kickstart_fx.prefs.AppearanceCategory;
import io.abc_def.kickstart_fx.util.Hyperlinks;

import javafx.scene.layout.Region;

public class AppConfigurationDialog {

    public static void showIfNeeded() {
        if (!AppProperties.get().isInitialLaunch()) {
            return;
        }

        var options = new OptionsBuilder()
                .sub(AppearanceCategory.languageChoice())
                .sub(AppearanceCategory.themeChoice())
                .buildComp();
        options.styleClass("initial-setup");
        options.styleClass("prefs-container");

        var scroll = new ScrollComp(options);
        scroll.apply(struc -> {
            struc.get().prefHeightProperty().bind(((Region) struc.get().getContent()).heightProperty());
        });
        scroll.minWidth(650);
        scroll.prefWidth(650);

        var modal = ModalOverlay.of("initialSetup", scroll);
        modal.addButton(new ModalButton(
                "docs",
                () -> {
                    Hyperlinks.open(Hyperlinks.DOCS);
                },
                false,
                false));
        modal.addButton(ModalButton.ok());
        AppDialog.show(modal);
    }
}
