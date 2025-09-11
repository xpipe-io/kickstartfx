package io.abc.kickstart_fx.prefs;

public interface PrefsValue {

    default boolean isSelectable() {
        return true;
    }
}
