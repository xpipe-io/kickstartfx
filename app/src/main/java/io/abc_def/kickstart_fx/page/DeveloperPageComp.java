package io.abc_def.kickstart_fx.page;

import io.abc_def.kickstart_fx.comp.SimpleComp;
import io.abc_def.kickstart_fx.comp.base.ButtonComp;
import io.abc_def.kickstart_fx.comp.base.VerticalComp;
import io.abc_def.kickstart_fx.core.AppI18n;
import io.abc_def.kickstart_fx.core.AppRestart;
import io.abc_def.kickstart_fx.core.mode.AppOperationMode;
import io.abc_def.kickstart_fx.issue.ErrorEventFactory;
import javafx.geometry.Insets;
import javafx.scene.layout.Region;

import java.util.List;

public class DeveloperPageComp extends SimpleComp {

    @Override
    protected Region createSimple() {
        var restart = new ButtonComp(AppI18n.observable("restart"), () -> {
            AppRestart.restart();
        });

        var throwException = new ButtonComp(AppI18n.observable("throwException"), () -> {
            ErrorEventFactory.fromThrowable(new RuntimeException("This is a test exception")).expected().handle();
        });

        var throwReportableException = new ButtonComp(AppI18n.observable("throwReportableException"), () -> {
            ErrorEventFactory.fromThrowable(new RuntimeException("This is a test exception")).handle();
        });

        var throwOmittedException = new ButtonComp(AppI18n.observable("throwOmittedException"), () -> {
            ErrorEventFactory.fromThrowable(new RuntimeException("This is a test exception")).expected().omit().handle();
        });

        var throwTerminalException = new ButtonComp(AppI18n.observable("throwTerminalException"), () -> {
            ErrorEventFactory.fromThrowable(new RuntimeException("This is a test exception")).expected().term().handle();
        });

        var tray = new ButtonComp(AppI18n.observable("toSystemTray"), () -> {
            AppOperationMode.switchToAsync(AppOperationMode.TRAY);
        });


        var background = new ButtonComp(AppI18n.observable("toBackground"), () -> {
            AppOperationMode.switchToAsync(AppOperationMode.BACKGROUND);
        });

        var buttons = new VerticalComp(List.of(restart, throwException, throwReportableException, throwOmittedException, throwTerminalException, tray, background));
        buttons.spacing(20);
        buttons.padding(new Insets(50));

        return buttons.createRegion();
    }
}
