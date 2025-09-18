import io.abc_def.kickstart_fx.core.AppLogs;
import io.abc_def.kickstart_fx.util.AppJacksonModule;
import io.abc_def.kickstart_fx.util.ModuleLayerLoader;

import com.fasterxml.jackson.databind.Module;
import org.slf4j.spi.SLF4JServiceProvider;

open module io.abc_def.kickstart_fx {
    exports io.abc_def.kickstart_fx.core;
    exports io.abc_def.kickstart_fx.util;
    exports io.abc_def.kickstart_fx;
    exports io.abc_def.kickstart_fx.issue;
    exports io.abc_def.kickstart_fx.comp.base;
    exports io.abc_def.kickstart_fx.core.mode;
    exports io.abc_def.kickstart_fx.prefs;
    exports io.abc_def.kickstart_fx.update;
    exports io.abc_def.kickstart_fx.core.check;
    exports io.abc_def.kickstart_fx.core.window;
    exports io.abc_def.kickstart_fx.comp;
    exports io.abc_def.kickstart_fx.platform;
    exports io.abc_def.kickstart_fx.page;

    requires static lombok;
    requires com.sun.jna;
    requires com.sun.jna.platform;
    requires org.slf4j;
    requires org.slf4j.jdk.platform.logging;
    requires atlantafx.base;
    requires com.vladsch.flexmark;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.annotation;
    requires io.xpipe.modulefs;
    requires org.apache.commons.io;
    requires org.apache.commons.lang3;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.media;
    requires javafx.web;
    requires javafx.graphics;
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    requires io.sentry;
    requires info.picocli;
    requires java.instrument;
    requires java.management;
    requires jdk.management;
    requires jdk.management.agent;
    requires java.net.http;
    requires org.jetbrains.annotations;
    requires org.kohsuke.github;

    // Required runtime modules
    requires jdk.charsets;
    requires jdk.crypto.cryptoki;
    requires jdk.localedata;
    requires jdk.accessibility;
    requires org.kordamp.ikonli.material2;
    requires org.kordamp.ikonli.materialdesign2;
    requires org.kordamp.ikonli.bootstrapicons;
    requires org.kordamp.ikonli.feather;
    requires jdk.zipfs;

    // AtlantaFX Sampler modules
    requires atlantafx.sampler;
    requires datafaker;
    requires javafx.fxml;

    // Monkey tester stuff
    requires monkey_tester;
    requires javafx.swing;

    uses ModuleLayerLoader;
    uses Module;

    provides Module with
            AppJacksonModule;
    provides SLF4JServiceProvider with
            AppLogs.Slf4jProvider;
}
