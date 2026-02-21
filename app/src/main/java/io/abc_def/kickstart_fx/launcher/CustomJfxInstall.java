package io.abc_def.kickstart_fx.launcher;

import lombok.Value;

import java.nio.file.Path;

@Value
public class CustomJfxInstall {

    Path basePath;

    public Path getSdkLibs() {
        return basePath.resolve("build", "sdk", "lib");
    }
}
