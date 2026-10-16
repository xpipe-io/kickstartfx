package io.abc_def.kickstart_fx.core;

import io.abc_def.kickstart_fx.core.check.AppDirectoryPermissionsCheck;
import io.abc_def.kickstart_fx.core.mode.AppOperationModeSelection;
import io.abc_def.kickstart_fx.issue.ErrorEventFactory;
import io.abc_def.kickstart_fx.issue.TrackEvent;

import lombok.Value;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Value
public class AppProperties {

    private static AppProperties INSTANCE;

    String version;
    String build;
    UUID buildUuid;
    String sentryUrl;
    String arch;
    boolean runtimeImage;
    boolean useVirtualThreads;
    Path dataDir;
    Path defaultDataDir;
    boolean showcase;
    AppVersion canonicalVersion;
    UUID uuid;
    boolean initialLaunch;
    UUID sessionId;
    boolean newBuildSession;
    boolean aotTrainMode;
    boolean debugPlatformThreadAccess;
    boolean buildAllPages;
    AppArguments arguments;
    AppOperationModeSelection explicitMode;
    boolean logToSysOut;
    boolean logToFile;
    String logLevel;

    public AppProperties(String[] args) {
        var appDir = Path.of(System.getProperty("user.dir")).resolve("app");
        Path propsFile = appDir.resolve("dev.properties");
        if (Files.exists(propsFile)) {
            try (var in = Files.newInputStream(propsFile)) {
                Properties props = new Properties();
                props.load(in);
                props.forEach((key, value) -> {
                    // Don't overwrite existing properties
                    if (System.getProperty(key.toString()) == null) {
                        System.setProperty(key.toString(), value.toString());
                    }
                });
            } catch (IOException e) {
                ErrorEventFactory.fromThrowable(e).handle();
            }
        }
        var referenceDir = Files.exists(appDir) ? appDir : Path.of(System.getProperty("user.dir"));

        runtimeImage = AppProperties.class
                .getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .getProtocol()
                .equals("jrt");
        arguments = AppArguments.init(args);
        version = Optional.ofNullable(System.getProperty(AppNames.propertyName("version")))
                .orElse("dev");
        build = Optional.ofNullable(System.getProperty(AppNames.propertyName("build")))
                .orElse("unknown");
        buildUuid = Optional.ofNullable(System.getProperty(AppNames.propertyName("buildId")))
                .map(UUID::fromString)
                .orElse(UUID.randomUUID());
        sentryUrl = System.getProperty(AppNames.propertyName("sentryUrl"));
        var osArch = System.getProperty("os.arch");
        arch = switch (osArch) {
            case "amd64", "x86_64" -> "x86_64";
            case "aarch64", "arm64" -> "arm64";
            default -> osArch;
        };
        useVirtualThreads = Optional.ofNullable(System.getProperty(AppNames.propertyName("useVirtualThreads")))
                .map(Boolean::parseBoolean)
                .orElse(true);
        debugPlatformThreadAccess = Optional.ofNullable(
                        System.getProperty(AppNames.propertyName("debugPlatformThreadAccess")))
                .map(Boolean::parseBoolean)
                .orElse(false);
        buildAllPages = Optional.ofNullable(System.getProperty(AppNames.propertyName("buildAllPages")))
                .map(Boolean::parseBoolean)
                .orElse(false);
        showcase = Optional.ofNullable(System.getProperty(AppNames.propertyName("showcase")))
                .map(Boolean::parseBoolean)
                .orElse(false);
        canonicalVersion = AppVersion.parse(version).orElse(null);
        logToSysOut = Optional.ofNullable(System.getProperty(AppNames.propertyName("writeSysOut")))
                .map(Boolean::parseBoolean)
                .orElse(false);
        logToFile = Optional.ofNullable(System.getProperty(AppNames.propertyName("writeLogs")))
                .map(Boolean::parseBoolean)
                .orElse(true);
        logLevel = Optional.ofNullable(System.getProperty(AppNames.propertyName("logLevel")))
                .filter(AppLogs.LOG_LEVELS::contains)
                .orElse("info");

        defaultDataDir = AppSystemInfo.ofCurrent()
                .getUserHome()
                .resolve("." + AppNames.ofCurrent().getKebapName());
        dataDir = Optional.ofNullable(System.getProperty(AppNames.propertyName("dataDir")))
                .map(s -> {
                    var p = Path.of(s);
                    if (!p.isAbsolute()) {
                        p = referenceDir.resolve(p);
                    }
                    return p;
                })
                .orElse(defaultDataDir);

        // We require the user dir from here
        AppDirectoryPermissionsCheck.checkDirectory(dataDir);
        AppCache.setBasePath(dataDir.resolve("cache"));
        UUID id = AppCache.getNonNull("uuid", UUID.class, () -> null);
        if (id == null) {
            uuid = UUID.randomUUID();
            AppCache.update("uuid", uuid);
        } else {
            uuid = id;
        }
        initialLaunch = AppCache.getNonNull("lastBuildId", String.class, () -> null) == null;
        sessionId = UUID.randomUUID();
        var cachedBuildId = AppCache.getNonNull("lastBuildId", String.class, () -> null);
        newBuildSession = !buildUuid.toString().equals(cachedBuildId);
        AppCache.update("lastBuildId", buildUuid);
        aotTrainMode = Optional.ofNullable(System.getProperty(AppNames.propertyName("aotTrainMode")))
                .map(Boolean::parseBoolean)
                .orElse(false);
        explicitMode = AppOperationModeSelection.getIfPresent(System.getProperty(AppNames.propertyName("mode")))
                .orElse(null);
    }

    public static void init() {
        init(new String[0]);
    }

    public static void init(String[] args) {
        if (INSTANCE != null) {
            return;
        }

        INSTANCE = new AppProperties(args);
    }

    public static AppProperties get() {
        return INSTANCE;
    }

    public void logArguments() {
        TrackEvent.withInfo("Loaded properties")
                .tag("version", version)
                .tag("build", build)
                .tag("dataDir", dataDir)
                .handle();

        TrackEvent.withInfo("Received arguments")
                .tag("raw", arguments.getRawArgs())
                .tag("resolved", arguments.getResolvedArgs())
                .tag("resolvedCommand", arguments.getOpenArgs())
                .handle();

        for (var e : System.getProperties().entrySet()) {
            if (e.getKey().toString().contains(AppNames.ofCurrent().getGroupName())) {
                TrackEvent.debug("Detected app property " + e.getKey() + "=" + e.getValue());
            }
        }
    }

    public boolean isDevelopmentEnvironment() {
        return !isRuntimeImage();
    }

    public Optional<AppVersion> getCanonicalVersion() {
        return Optional.ofNullable(canonicalVersion);
    }
}
