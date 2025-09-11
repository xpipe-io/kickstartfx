package io.xpipe.kickstart_fx.issue;

import io.xpipe.kickstart_fx.core.AppProperties;
import io.xpipe.kickstart_fx.core.window.AppMainWindow;
import io.xpipe.kickstart_fx.platform.PlatformInit;
import io.xpipe.kickstart_fx.util.ModuleLayerLoader;

import java.util.function.Consumer;

public class GuiErrorHandlerBase {

    protected boolean startupGui(Consumer<Throwable> onFail) {
        try {
            AppProperties.init();
            ModuleLayerLoader.loadAll(ModuleLayer.boot(), t -> {
                ErrorEventFactory.fromThrowable(t).handle();
            });

            if (PlatformInit.isLoadingThread()) {
                return false;
            }

            PlatformInit.init(true);
            AppMainWindow.init(true);
        } catch (Throwable ex) {
            onFail.accept(ex);
            return false;
        }

        return true;
    }
}
