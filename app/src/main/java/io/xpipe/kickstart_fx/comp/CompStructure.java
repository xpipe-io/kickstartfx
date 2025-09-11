package io.xpipe.kickstart_fx.comp;

import javafx.scene.layout.Region;

public interface CompStructure<R extends Region> {
    R get();
}
