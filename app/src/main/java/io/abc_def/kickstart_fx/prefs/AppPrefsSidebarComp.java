package io.abc_def.kickstart_fx.prefs;

import io.abc_def.kickstart_fx.comp.RegionBuilder;
import io.abc_def.kickstart_fx.comp.SimpleRegionBuilder;
import io.abc_def.kickstart_fx.comp.base.ButtonComp;
import io.abc_def.kickstart_fx.comp.base.VerticalComp;
import io.abc_def.kickstart_fx.core.AppI18n;
import io.abc_def.kickstart_fx.core.AppRestart;
import io.abc_def.kickstart_fx.platform.PlatformThread;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.Region;
import javafx.scene.text.TextAlignment;

import org.int4.fx.builders.common.AbstractRegionBuilder;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class AppPrefsSidebarComp extends SimpleRegionBuilder {

    @Override
    protected Region createSimple() {
        var effectiveCategories = AppPrefs.get().getCategories().stream()
                .filter(appPrefsCategory -> appPrefsCategory.show())
                .toList();
        var buttons = effectiveCategories.stream()
                .<AbstractRegionBuilder<?, ?>>map(appPrefsCategory -> {
                    return new ButtonComp(
                                    AppI18n.observable(appPrefsCategory.getId()),
                                    new ReadOnlyObjectWrapper<>(appPrefsCategory.getIcon()),
                                    () -> {
                                        AppPrefs.get().getSelectedCategory().setValue(appPrefsCategory);
                                    })
                            .apply(struc -> {
                                struc.setGraphicTextGap(9);
                                struc.setTextAlignment(TextAlignment.LEFT);
                                struc.setAlignment(Pos.CENTER_LEFT);
                                AppPrefs.get().getSelectedCategory().subscribe(val -> {
                                    struc.pseudoClassStateChanged(
                                            PseudoClass.getPseudoClass("selected"), appPrefsCategory.equals(val));
                                });
                            })
                            .maxWidth(2000);
                })
                .collect(Collectors.toCollection(ArrayList::new));

        var restartButton = new ButtonComp(AppI18n.observable("restartApp"), new FontIcon("mdi2r-restart"), () -> {
            AppRestart.restart();
        });
        restartButton.maxWidth(2000);
        restartButton.visible(AppPrefs.get().getRequiresRestart());
        restartButton.apply(button -> button.setPadding(new Insets(6, 10, 6, 6)));
        buttons.add(RegionBuilder.vspacer());
        buttons.add(restartButton);

        var vbox = new VerticalComp(buttons).style("sidebar");
        vbox.apply(struc -> {
            AppPrefs.get().getSelectedCategory().subscribe(val -> {
                // Null is only set temporarily to reset the scroll position
                if (val == null) {
                    return;
                }

                PlatformThread.runLaterIfNeeded(() -> {
                    var index = effectiveCategories.indexOf(val);
                    if (index < 0 || index >= struc.getChildren().size()) {
                        return;
                    }

                    ((Button) struc.getChildren().get(index)).fire();
                });
            });
        });
        return vbox.build();
    }
}
