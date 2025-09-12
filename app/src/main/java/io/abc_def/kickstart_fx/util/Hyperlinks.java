package io.abc_def.kickstart_fx.util;

public class Hyperlinks {

    public static final String GITHUB = "https://github.com/xpipe-io/kickstartfx";
    public static final String DISCORD = "https://discord.gg/8y89vS8cRb";
    public static final String DOCS = "https://kickstartfx.xpipe.io";
    public static final String TRANSLATE = "https://github.com/xpipe-io/kickstartfx/tree/main/lang";

    public static void open(String uri) {
        DesktopHelper.openUrlInBrowser(uri);
    }
}
