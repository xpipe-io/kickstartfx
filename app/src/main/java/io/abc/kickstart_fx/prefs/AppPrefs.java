package io.abc.kickstart_fx.prefs;

import io.abc.kickstart_fx.core.AppLayoutModel;
import io.abc.kickstart_fx.core.AppProperties;
import io.abc.kickstart_fx.core.AppTheme;
import io.abc.kickstart_fx.core.mode.AppOperationMode;

import io.abc.kickstart_fx.platform.GlobalBooleanProperty;
import io.abc.kickstart_fx.platform.GlobalDoubleProperty;
import io.abc.kickstart_fx.platform.GlobalObjectProperty;
import io.abc.kickstart_fx.platform.PlatformThread;
import io.abc.kickstart_fx.util.OsType;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableDoubleValue;
import javafx.beans.value.ObservableValue;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;

import java.util.*;

public final class AppPrefs {

    private static AppPrefs INSTANCE;
    private final List<Mapping> mapping = new ArrayList<>();

    public static void initLocal() {
        INSTANCE = new AppPrefs();
        INSTANCE.loadLocal();
        INSTANCE.fixLocalValues();
    }

    public static void reset() {
        INSTANCE.save();

        // Keep instance as we might need some values on shutdown, e.g. on update with terminals
        // INSTANCE = null;
    }

    public static AppPrefs get() {
        return INSTANCE;
    }


    @Getter
    private final BooleanProperty requiresRestart = new GlobalBooleanProperty(false);

    final BooleanProperty disableHardwareAcceleration = map(Mapping.builder()
            .property(new GlobalBooleanProperty(false))
            .key("disableHardwareAcceleration")
            .valueClass(Boolean.class)
            .requiresRestart(true)
            .build());
    public final BooleanProperty performanceMode =
            map(Mapping.builder()
                    .property(new GlobalBooleanProperty())
                    .key("performanceMode")
                    .valueClass(Boolean.class)
                    .build());
    public final ObjectProperty<AppTheme.Theme> theme =
            map(Mapping.builder()
                    .property(new GlobalObjectProperty<>())
                    .key("theme")
                    .valueClass(AppTheme.Theme.class)
                    .build());
    final BooleanProperty useSystemFont =
            map(Mapping.builder()
                    .property(new GlobalBooleanProperty(OsType.ofLocal() != OsType.MACOS))
                    .key("useSystemFont")
                    .valueClass(Boolean.class)
                    .build());
    final Property<Integer> uiScale =
            map(Mapping.builder()
                    .property(new GlobalObjectProperty<>())
                    .key("uiScale")
                    .valueClass(Integer.class)
                    .requiresRestart(true)
                    .build());
    final BooleanProperty saveWindowLocation =
            map(Mapping.builder()
                    .property(new GlobalBooleanProperty(true))
                    .key("saveWindowLocation")
                    .valueClass(Boolean.class)
                    .requiresRestart(false)
                    .build());
    final DoubleProperty windowOpacity =
            map(Mapping.builder()
                    .property(new GlobalDoubleProperty(1.0))
                    .key("windowOpacity")
                    .valueClass(Double.class)
                    .requiresRestart(false)
                    .build());
    public final BooleanProperty focusWindowOnNotifications =
            map(Mapping.builder()
                    .property(new GlobalBooleanProperty(false))
                    .key("focusWindowOnNotifications")
                    .valueClass(Boolean.class)
                    .build());
    final ObjectProperty<StartupBehaviour> startupBehaviour =
            map(Mapping.builder()
                    .property(new GlobalObjectProperty<>(StartupBehaviour.GUI))
                    .key("startupBehaviour")
                    .valueClass(StartupBehaviour.class)
                    .requiresRestart(true)
                    .build());
    final ObjectProperty<CloseBehaviour> closeBehaviour =
            map(Mapping.builder()
                    .property(new GlobalObjectProperty<>(CloseBehaviour.QUIT))
                    .key("closeBehaviour")
                    .valueClass(CloseBehaviour.class)
                    .build());
    final ObjectProperty<SupportedLocale> language =
            map(Mapping.builder()
                    .property(new GlobalObjectProperty<>(SupportedLocale.ENGLISH))
                    .key("language")
                    .valueClass(SupportedLocale.class)
                    .build());
    final BooleanProperty automaticallyCheckForUpdates =
            map(Mapping.builder()
                    .property(new GlobalBooleanProperty(true))
                    .key("automaticallyCheckForUpdates")
                    .valueClass(Boolean.class)
                    .build());

    @Getter
    private final List<AppPrefsCategory> categories = List.of(
            new AboutCategory(),
            new AppearanceCategory(),
            new SystemCategory(),
            new UpdatesCategory(),
            new TroubleshootCategory(),
            new LinksCategory());

    private final AppPrefsStorageHandler globalStorageHandler = new AppPrefsStorageHandler(
            AppProperties.get().getDataDir().resolve("settings").resolve("preferences.json"));

    @Getter
    private final Property<AppPrefsCategory> selectedCategory = new GlobalObjectProperty<>(categories.getFirst());

    private AppPrefs() {}

    public ObservableBooleanValue disableHardwareAcceleration() {
        return disableHardwareAcceleration;
    }

    public ObservableBooleanValue focusWindowOnNotifications() {
        return focusWindowOnNotifications;
    }

    public ObservableValue<AppTheme.Theme> theme() {
        return theme;
    }

    public ObservableValue<SupportedLocale> language() {
        return language;
    }

    public ObservableBooleanValue performanceMode() {
        return performanceMode;
    }

    public ObservableValue<Boolean> useSystemFont() {
        return useSystemFont;
    }

    public ReadOnlyProperty<Integer> uiScale() {
        return uiScale;
    }

    public ReadOnlyProperty<CloseBehaviour> closeBehaviour() {
        return closeBehaviour;
    }

    public ReadOnlyProperty<StartupBehaviour> startupBehaviour() {
        return startupBehaviour;
    }

    public ReadOnlyBooleanProperty automaticallyUpdate() {
        return automaticallyCheckForUpdates;
    }

    public ObservableDoubleValue windowOpacity() {
        return windowOpacity;
    }

    public ObservableBooleanValue saveWindowLocation() {
        return saveWindowLocation;
    }

    @SuppressWarnings("unchecked")
    private <T> T map(Mapping m) {
        mapping.add(m);
        m.property.addListener((observable, oldValue, newValue) -> {
            var running = AppOperationMode.get() == AppOperationMode.GUI;
            if (running && m.requiresRestart) {
                AppPrefs.get().requiresRestart.set(true);
            }
        });
        return (T) m.getProperty();
    }

    public <T> void setFromExternal(ObservableValue<T> prop, T newValue) {
        var writable = (Property<T>) prop;
        PlatformThread.runLaterIfNeededBlocking(() -> {
            writable.setValue(newValue);
        });
        save();
    }

    private void fixLocalValues() {
        if (System.getProperty("os.name").toLowerCase().contains("server")) {
            performanceMode.setValue(true);
        }

        if (OsType.ofLocal() == OsType.MACOS
                && AppProperties.get()
                        .getCanonicalVersion()
                        .map(appVersion -> appVersion.getMajor() == 18)
                        .orElse(false)) {
            useSystemFont.set(false);
        }
    }

    private void loadLocal() {
        for (Mapping value : mapping) {
            loadValue(globalStorageHandler, value);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T loadValue(AppPrefsStorageHandler handler, Mapping value) {
        T def = (T) value.getProperty().getValue();
        Property<T> property = (Property<T>) value.getProperty();
        var val = handler.loadObject(value.getKey(), value.getValueType(), def, value.isLog());
        property.setValue(val);
        return val;
    }

    public synchronized void save() {
        for (Mapping m : mapping) {
            // It might be possible that we save while the handler is not initialized yet / has no file or
            // directory
            if (!globalStorageHandler.isInitialized()) {
                continue;
            }
            globalStorageHandler.updateObject(m.getKey(), m.getProperty().getValue(), m.getValueType());
        }

        if (globalStorageHandler.isInitialized()) {
            globalStorageHandler.save();
        }
    }

    public void selectCategory(String id) {
        var found = categories.stream()
                .filter(appPrefsCategory -> appPrefsCategory.getId().equals(id))
                .findFirst();
        found.ifPresent(appPrefsCategory -> {
            PlatformThread.runLaterIfNeeded(() -> {
                AppLayoutModel.get().selectFileBrowser();

                Platform.runLater(() -> {
                    // Reset scroll in case the target category is already somewhat in focus
                    selectedCategory.setValue(null);
                    selectedCategory.setValue(appPrefsCategory);
                });
            });
        });
    }

    public Mapping getMapping(Object property) {
        return mapping.stream().filter(m -> m.property == property).findFirst().orElseThrow();
    }

    @Value
    @Builder
    @AllArgsConstructor
    public static class Mapping {

        String key;
        Property<?> property;
        JavaType valueType;
        boolean requiresRestart;
        boolean log;

        public static class MappingBuilder {

            MappingBuilder valueClass(Class<?> clazz) {
                this.valueType(TypeFactory.defaultInstance().constructType(clazz));
                return this;
            }
        }
    }
}
