package io.abc_def.kickstart_fx.core;

import io.abc_def.kickstart_fx.Main;
import io.abc_def.kickstart_fx.core.mode.AppOperationMode;
import io.abc_def.kickstart_fx.issue.ErrorEventFactory;
import io.abc_def.kickstart_fx.platform.PlatformState;
import io.abc_def.kickstart_fx.util.OsType;

import java.awt.*;
import java.awt.desktop.*;
import java.util.List;
import javax.imageio.ImageIO;

public class AppDesktopIntegration {

    public static void init() {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().addAppEventListener(new SystemSleepListener() {
                    @Override
                    public void systemAboutToSleep(SystemSleepEvent e) {}

                    @Override
                    public void systemAwoke(SystemSleepEvent e) {

                        // TODO: Do we want to do something on hibernation?

                        // If we run this at the same time as the system is sleeping, there might be exceptions
                        // because the platform does not like being shut down while sleeping
                        // This assures that it will be run later, on system wake
                        //                        ThreadHelper.runAsync(() -> {
                        //                            ThreadHelper.sleep(1000);
                        //                            AppOperationMode.close();
                        //                        });
                    }
                });
            }

            // This will initialize the toolkit on macOS and create the dock icon
            // macOS does not like applications that run fully in the background, so always do it
            if (OsType.ofLocal() == OsType.MACOS && Desktop.isDesktopSupported()) {
                Desktop.getDesktop().setPreferencesHandler(e -> {
                    if (PlatformState.getCurrent() != PlatformState.RUNNING) {
                        return;
                    }

                    if (AppLayoutModel.get() != null) {
                        AppLayoutModel.get().selectFileBrowser();
                    }
                });

                // URL open operations have to be handled in a special way on macOS!
                Desktop.getDesktop().setOpenURIHandler(e -> {
                    AppOpenArguments.handle(List.of(e.getURI().toString()));
                });

                Desktop.getDesktop().addAppEventListener(new AppReopenedListener() {
                    @Override
                    public void appReopened(AppReopenedEvent e) {
                        AppOperationMode.switchToAsync(AppOperationMode.GUI);
                    }
                });

                // Set dock icon explicitly on macOS
                // This is necessary in case the app was started through a script as it will have no icon otherwise
                if (AppLogs.get().isWriteToSysout() && Taskbar.isTaskbarSupported()) {
                    try {
                        var iconUrl = Main.class.getResourceAsStream("resources/img/logo/padded/logo_128x128.png");
                        if (iconUrl != null) {
                            var awtIcon = ImageIO.read(iconUrl);
                            Taskbar.getTaskbar().setIconImage(awtIcon);
                        }
                    } catch (Exception ex) {
                        ErrorEventFactory.fromThrowable(ex)
                                .omitted(true)
                                .build()
                                .handle();
                    }
                }
            }
        } catch (Throwable ex) {
            ErrorEventFactory.fromThrowable(ex).term().handle();
        }
    }
}
