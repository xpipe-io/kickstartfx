package io.abc_def.kickstart_fx.issue;

import io.abc_def.kickstart_fx.core.AppI18n;
import io.abc_def.kickstart_fx.core.AppLayoutModel;
import io.abc_def.kickstart_fx.platform.LabelGraphic;

import java.time.Duration;

public class GuiErrorHandler extends GuiErrorHandlerBase implements ErrorHandler {

    private final ErrorHandler log = new LogErrorHandler();

    @Override
    public void handle(ErrorEvent event) {
        log.handle(event);

        if (!startupGui(throwable -> {
            var second = ErrorEventFactory.fromThrowable(throwable).build();
            log.handle(second);
            ErrorAction.ignore().handle(second);
        })) {
            return;
        }

        if (event.isOmitted()) {
            ErrorAction.ignore().handle(event);
            if (AppLayoutModel.get() != null) {
                AppLayoutModel.get()
                        .showQueueEntry(
                                new AppLayoutModel.QueueEntry(
                                        AppI18n.observable("errorOccurred"),
                                        new LabelGraphic.IconGraphic("mdoal-error_outline"),
                                        () -> {
                                            handleGui(event);
                                        }),
                                Duration.ofSeconds(10),
                                true);
            }
            return;
        }

        handleGui(event);
    }

    private void handleGui(ErrorEvent event) {
        ErrorHandlerDialog.showAndWait(event);
    }
}
