package io.xpipe.kickstart_fx.update;

import io.xpipe.kickstart_fx.comp.base.ModalButton;
import io.xpipe.kickstart_fx.core.AppProperties;
import io.xpipe.kickstart_fx.util.Hyperlinks;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class BasicUpdater extends UpdateHandler {

    public BasicUpdater(boolean thread) {
        super(thread);
    }

    @Override
    public List<ModalButton> createActions() {
        var list = new ArrayList<ModalButton>();
        list.add(new ModalButton("ignore", null, true, false));
        list.add(new ModalButton(
                "checkOutUpdate",
                () -> {
                    var rel = getLastUpdateCheckResult().getValue();
                    if (rel == null || !rel.isUpdate()) {
                        return;
                    }

                    Hyperlinks.open(rel.getReleaseUrl());
                },
                false,
                true));
        return list;
    }

    public synchronized AvailableRelease refreshUpdateCheckImpl() throws Exception {
        var found = AppDownloads.getMarkedLatestRelease();
        if (found.isEmpty()) {
            return null;
        }

        var rel = found.get();
        event("Determined latest suitable release " + rel.getTagName());
        var isUpdate = isUpdate(rel.getTagName());
        lastUpdateCheckResult.setValue(new AvailableRelease(
                AppProperties.get().getVersion(),
                AppDistributionType.get().getId(),
                rel.getTagName(),
                rel.getHtmlUrl().toString(),
                "## Changes in v" + rel.getTagName() + "\n\n" + rel.getBody(),
                Instant.now(),
                isUpdate));
        return lastUpdateCheckResult.getValue();
    }
}
