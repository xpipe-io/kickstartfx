package io.abc_def.kickstart_fx.update;

import io.abc_def.kickstart_fx.comp.base.MarkdownComp;
import io.abc_def.kickstart_fx.comp.base.ModalOverlay;
import io.abc_def.kickstart_fx.core.AppProperties;
import io.abc_def.kickstart_fx.issue.TrackEvent;

public class UpdateAvailableDialog {

    public static void showIfNeeded(boolean wait) {
        showIfNeeded(wait, true);
    }

    public static void showIfNeeded(boolean wait, boolean refresh) {
        if (AppProperties.get().isAotTrainMode()) {
            return;
        }

        UpdateHandler uh = AppDistributionType.get().getUpdateHandler();
        if (uh.getLastUpdateCheckResult().getValue() == null) {
            return;
        }

        // Check whether we still have the latest version prepared
        if (refresh) {
            uh.refreshUpdateCheckSilent();
            if (uh.getLastUpdateCheckResult().getValue() == null) {
                return;
            }
        }

        TrackEvent.withInfo("Showing update alert ...")
                .tag("version", uh.getLastUpdateCheckResult().getValue().getVersion())
                .handle();
        var u = uh.getLastUpdateCheckResult().getValue();

        var comp = new MarkdownComp(u.getBody() != null ? u.getBody() : "", s -> s, false);
        var modal = ModalOverlay.of("updateReadyAlertTitle", comp.prefWidth(600), null);
        for (var action : uh.createActions()) {
            modal.addButton(action);
        }

        if (wait) {
            modal.showAndWait();
        } else {
            modal.show();
        }
    }
}
