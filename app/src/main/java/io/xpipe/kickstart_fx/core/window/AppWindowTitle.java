package io.xpipe.kickstart_fx.core.window;

import io.xpipe.kickstart_fx.core.AppI18n;
import io.xpipe.kickstart_fx.core.AppNames;
import io.xpipe.kickstart_fx.core.AppProperties;
import io.xpipe.kickstart_fx.update.AppDistributionType;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import lombok.Getter;

public class AppWindowTitle {

    @Getter
    private static final StringProperty title = new SimpleStringProperty(createTitle());

    public static void init() {
        var l = AppI18n.activeLanguage();
        l.subscribe(ignored -> {
            title.setValue(createTitle());
        });

        if (AppDistributionType.get() != AppDistributionType.UNKNOWN) {
            var u = AppDistributionType.get().getUpdateHandler().getLastUpdateCheckResult();
            u.subscribe(ignored -> {
                title.setValue(createTitle());
            });
        }
    }

    private static String createTitle() {
        var base = String.format(
                AppNames.ofMain().getName() + " (%s)", AppProperties.get().getVersion());
        var dist = AppDistributionType.get();
        if (dist != AppDistributionType.UNKNOWN) {
            var u = dist.getUpdateHandler().getLastUpdateCheckResult();
            var suffix = u.getValue() != null
                    ? " " + AppI18n.get("updateReadyTitle", u.getValue().getVersion())
                    : "";
            return base + suffix;
        } else {
            return base;
        }
    }
}
