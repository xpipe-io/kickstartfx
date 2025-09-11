import io.xpipe.kickstart_fx.core.AppLogs;
import io.xpipe.kickstart_fx.util.AppJacksonModule;
import io.xpipe.kickstart_fx.util.ModuleLayerLoader;

import com.fasterxml.jackson.databind.Module;
import org.slf4j.spi.SLF4JServiceProvider;

open module io.xpipe.kickstart_fx {
    exports io.xpipe.kickstart_fx.core;
    exports io.xpipe.kickstart_fx.util;
    exports io.xpipe.kickstart_fx;
    exports io.xpipe.kickstart_fx.issue;
    exports io.xpipe.kickstart_fx.comp.base;
    exports io.xpipe.kickstart_fx.core.mode;
    exports io.xpipe.kickstart_fx.prefs;
    exports io.xpipe.kickstart_fx.update;
    exports io.xpipe.kickstart_fx.core.check;
    exports io.xpipe.kickstart_fx.core.window;
    exports io.xpipe.kickstart_fx.comp;
    exports io.xpipe.kickstart_fx.platform;

    requires com.sun.jna;
    requires com.sun.jna.platform;
    requires org.slf4j;
    requires org.slf4j.jdk.platform.logging;
    requires atlantafx.base;
    requires com.vladsch.flexmark;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.annotation;
    requires net.synedra.validatorfx;
    requires io.xpipe.modulefs;
    requires static lombok;
    requires org.apache.commons.io;
    requires org.apache.commons.lang3;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.media;
    requires javafx.web;
    requires javafx.graphics;
    requires javafx.fxml;
    requires org.kordamp.ikonli.javafx;
    requires io.sentry;
    requires info.picocli;
    requires java.instrument;
    requires java.management;
    requires jdk.management;
    requires jdk.management.agent;
    requires org.kordamp.ikonli.core;
    requires jdk.httpserver;
    requires java.net.http;
    requires org.jetbrains.annotations;

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
    requires java.rmi;
    requires org.kohsuke.github;
    requires atlantafx.sampler;

    uses ModuleLayerLoader;

    provides Module with
            AppJacksonModule;
    provides SLF4JServiceProvider with
            AppLogs.Slf4jProvider;
}
