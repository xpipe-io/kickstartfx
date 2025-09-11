package io.xpipe.kickstart_fx.core;

import atlantafx.sampler.page.showcase.BlueprintsPage;
import atlantafx.sampler.page.showcase.OverviewPage;
import atlantafx.sampler.page.showcase.filemanager.FileManagerPage;
import atlantafx.sampler.page.showcase.musicplayer.MusicPlayerPage;
import io.xpipe.kickstart_fx.comp.Comp;
import io.xpipe.kickstart_fx.platform.LabelGraphic;
import io.xpipe.kickstart_fx.platform.PlatformThread;
import io.xpipe.kickstart_fx.prefs.AppPrefsComp;

import io.xpipe.kickstart_fx.util.GlobalTimer;
import io.xpipe.kickstart_fx.util.Hyperlinks;
import io.xpipe.kickstart_fx.util.ThreadHelper;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import lombok.*;
import lombok.experimental.NonFinal;
import lombok.extern.jackson.Jacksonized;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Getter
public class AppLayoutModel {

    private static AppLayoutModel INSTANCE;

    private final SavedState savedState;

    private final List<Entry> entries;

    private final Property<Entry> selected;

    private final ObservableList<QueueEntry> queueEntries;

    private final BooleanProperty ptbAvailable = new SimpleBooleanProperty();

    public AppLayoutModel(SavedState savedState) {
        this.savedState = savedState;
        this.entries = createEntryList();
        this.selected = new SimpleObjectProperty<>(entries.getFirst());
        this.queueEntries = FXCollections.observableArrayList();
    }

    public static AppLayoutModel get() {
        return INSTANCE;
    }

    public static void init() {
        var state = AppCache.getNonNull("layoutState", SavedState.class, () -> new SavedState(270, 300));
        INSTANCE = new AppLayoutModel(state);
    }

    public static void reset() {
        if (INSTANCE == null) {
            return;
        }

        AppCache.update("layoutState", INSTANCE.savedState);
        INSTANCE = null;
    }

    public synchronized void showQueueEntry(QueueEntry entry, Duration duration, boolean allowDuplicates) {
        if (!allowDuplicates && queueEntries.contains(entry)) {
            return;
        }

        queueEntries.add(entry);
        if (duration != null) {
            GlobalTimer.delay(
                    () -> {
                        synchronized (this) {
                            queueEntries.remove(entry);
                        }
                    },
                    duration);
        }
    }

    public void selectMusicPlayer() {
        PlatformThread.runLaterIfNeeded(() -> {
            selected.setValue(entries.get(1));
        });
    }

    public void selectFileBrowser() {
        PlatformThread.runLaterIfNeeded(() -> {
            selected.setValue(entries.get(2));
        });
    }

    public void selectOverview() {
        PlatformThread.runLaterIfNeeded(() -> {
            selected.setValue(entries.get(3));
        });
    }

    public void selectDeveloper() {
        PlatformThread.runLaterIfNeeded(() -> {
            selected.setValue(entries.get(4));
        });
    }

    public void selectSettings() {
        PlatformThread.runLaterIfNeeded(() -> {
            selected.setValue(entries.get(5));
        });
    }

    public void selectBlueprints() {
        PlatformThread.runLaterIfNeeded(() -> {
            selected.setValue(entries.getFirst());
        });
    }

    private List<Entry> createEntryList() {
        var l = new ArrayList<>(List.of(
                new Entry(
                        AppI18n.observable("blueprints"),
                        new LabelGraphic.IconGraphic("mdi2a-aspect-ratio"),
                        Comp.of(BlueprintsPage::new),
                        null),
                new Entry(
                        AppI18n.observable("musicPlayer"),
                        new LabelGraphic.IconGraphic("mdi2m-music"),
                        Comp.of(() -> {
                            var bp = new MusicPlayerPage();
                            return bp;
                        }),
                        null),
                new Entry(
                        AppI18n.observable("fileBrowser"),
                        new LabelGraphic.IconGraphic("mdi2f-file-cabinet"),
                        Comp.of(() -> {
                            var bp = new FileManagerPage();
                            VBox vbox = (VBox) bp.getSnapshotTarget();
                            return (Region) ((BorderPane) vbox.getChildren().getFirst()).getCenter();
                        }),
                        null),
                new Entry(
                        AppI18n.observable("overview"),
                        new LabelGraphic.IconGraphic("mdi2l-list-box-outline"),
                        Comp.of(() -> {
                            var bp = new OverviewPage();
                            return bp;
                        }),
                        null),
                new Entry(
                        AppI18n.observable("settings"),
                        new LabelGraphic.IconGraphic("mdsmz-miscellaneous_services"),
                        new AppPrefsComp(),
                        null),
                new Entry(
                        AppI18n.observable("docs"),
                        new LabelGraphic.IconGraphic("mdi2b-book-open-variant"),
                        null,
                        () -> Hyperlinks.open(Hyperlinks.DOCS)),
                new Entry(
                        AppI18n.observable("visitGithubRepository"),
                        new LabelGraphic.IconGraphic("mdi2g-github"),
                        null,
                        () -> Hyperlinks.open(Hyperlinks.GITHUB)),
                new Entry(
                        AppI18n.observable("discord"),
                        new LabelGraphic.IconGraphic("bi-discord"),
                        null,
                        () -> Hyperlinks.open(Hyperlinks.DISCORD))));
        return l;
    }

    @Data
    @Builder
    @Jacksonized
    public static class SavedState {

        double sidebarWidth;
        double browserConnectionsWidth;
    }

    public record Entry(
            ObservableValue<String> name,
            LabelGraphic icon,
            Comp<?> comp,
            Runnable action
    ) {}

    @Value
    @NonFinal
    public static class QueueEntry {

        ObservableValue<String> name;
        LabelGraphic icon;
        Runnable action;

        public void show() {
            ThreadHelper.runAsync(() -> {
                try {
                    getAction().run();
                } finally {
                    AppLayoutModel.get().getQueueEntries().remove(this);
                }
            });
        }
    }
}
