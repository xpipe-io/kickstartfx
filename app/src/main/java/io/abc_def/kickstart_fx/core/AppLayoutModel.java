package io.abc_def.kickstart_fx.core;

import io.abc_def.kickstart_fx.comp.Comp;
import io.abc_def.kickstart_fx.page.*;
import io.abc_def.kickstart_fx.platform.LabelGraphic;
import io.abc_def.kickstart_fx.platform.PlatformThread;
import io.abc_def.kickstart_fx.page.PrefsPageComp;
import io.abc_def.kickstart_fx.util.GlobalTimer;
import io.abc_def.kickstart_fx.util.Hyperlinks;
import io.abc_def.kickstart_fx.util.ThreadHelper;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

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

    public void selectMarkdown() {
        PlatformThread.runLaterIfNeeded(() -> {
            selected.setValue(entries.get(5));
        });
    }

    public void selectMonkeyTester() {
        PlatformThread.runLaterIfNeeded(() -> {
            selected.setValue(entries.get(6));
        });
    }

    public void selectSettings() {
        PlatformThread.runLaterIfNeeded(() -> {
            selected.setValue(entries.get(7));
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
                        new BlueprintsPageComp(),
                        null),
                new Entry(
                        AppI18n.observable("musicPlayer"),
                        new LabelGraphic.IconGraphic("mdi2m-music"),
                        new MusicPlayerPageComp(),
                        null),
                new Entry(
                        AppI18n.observable("fileBrowser"),
                        new LabelGraphic.IconGraphic("mdi2f-file-cabinet"),
                        new FileBrowserPageComp(),
                        null),
                new Entry(
                        AppI18n.observable("overview"),
                        new LabelGraphic.IconGraphic("mdi2l-list-box-outline"),
                        new OverviewPageComp(),
                        null),
                new Entry(
                        AppI18n.observable("markdown"),
                        new LabelGraphic.IconGraphic("mdi2l-language-markdown-outline"),
                        new MarkdownPageComp(),
                        null),
                new Entry(
                        AppI18n.observable("monkeyTester"),
                        new LabelGraphic.IconGraphic("mdi2s-shape"),
                        new MonkeyTesterPageComp(),
                        null),
                new Entry(
                        AppI18n.observable("developer"),
                        new LabelGraphic.IconGraphic("mdi2c-code-tags"),
                        new DeveloperPageComp(),
                        null),
                new Entry(
                        AppI18n.observable("settings"),
                        new LabelGraphic.IconGraphic("mdsmz-miscellaneous_services"),
                        new PrefsPageComp(),
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
                        () -> Hyperlinks.open(Hyperlinks.GITHUB))));
        return l;
    }

    @Data
    @Builder
    @Jacksonized
    public static class SavedState {

        double sidebarWidth;
        double browserConnectionsWidth;
    }

    public record Entry(ObservableValue<String> name, LabelGraphic icon, Comp<?> comp, Runnable action) {}

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
