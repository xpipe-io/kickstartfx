package io.abc_def.kickstart_fx.update;

import io.abc_def.kickstart_fx.comp.base.ModalButton;
import io.abc_def.kickstart_fx.core.AppProperties;
import io.abc_def.kickstart_fx.issue.ErrorEventFactory;
import io.abc_def.kickstart_fx.issue.TrackEvent;
import io.abc_def.kickstart_fx.prefs.AppPrefs;
import io.abc_def.kickstart_fx.util.BooleanScope;
import io.abc_def.kickstart_fx.util.ThreadHelper;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import lombok.With;
import lombok.extern.jackson.Jacksonized;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@SuppressWarnings("InfiniteLoopStatement")
@Getter
public abstract class UpdateHandler {

    protected final Property<AvailableRelease> lastUpdateCheckResult = new SimpleObjectProperty<>();
    protected final BooleanProperty busy = new SimpleBooleanProperty();

    protected UpdateHandler(boolean startBackgroundThread) {
        if (startBackgroundThread) {
            startBackgroundUpdater();
        }
    }

    private void startBackgroundUpdater() {
        ThreadHelper.createPlatformThread("updater", true, () -> {
                    ThreadHelper.sleep(Duration.ofMinutes(1).toMillis());
                    event("Starting background updater thread");
                    while (true) {
                        if (AppPrefs.get() != null && AppPrefs.get().automaticallyUpdate().get()) {
                            event("Performing background update");
                            refreshUpdateCheckSilent();
                        }

                        ThreadHelper.sleep(Duration.ofHours(1).toMillis());
                    }
                })
                .start();
    }

    protected void event(String msg) {
        TrackEvent.builder().type("info").message(msg).handle();
    }

    protected final boolean isUpdate(String releaseVersion) {
        if (!AppProperties.get().getVersion().equals(releaseVersion)) {
            event("Release has a different version");
            return true;
        }

        return false;
    }

    public final AvailableRelease refreshUpdateCheckSilent() {
        try {
            return refreshUpdateCheck();
        } catch (Exception ex) {
            ErrorEventFactory.fromThrowable(ex).discard().handle();
            return null;
        }
    }

    public abstract List<ModalButton> createActions();

    public final AvailableRelease refreshUpdateCheck() throws Exception {
        if (busy.getValue()) {
            return lastUpdateCheckResult.getValue();
        }

        try (var ignored = new BooleanScope(busy).start()) {
            return refreshUpdateCheckImpl();
        }
    }

    public abstract AvailableRelease refreshUpdateCheckImpl() throws Exception;

    @Value
    @Builder
    @Jacksonized
    @With
    public static class AvailableRelease {
        String sourceVersion;
        String sourceDist;
        String version;
        String releaseUrl;
        String body;
        Instant checkTime;
        boolean isUpdate;
    }
}
