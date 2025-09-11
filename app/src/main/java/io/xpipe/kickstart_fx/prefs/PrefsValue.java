package io.xpipe.kickstart_fx.prefs;

public interface PrefsValue {

    default boolean isAvailable() {
        return true;
    }

    default boolean isSelectable() {
        return true;
    }
}
