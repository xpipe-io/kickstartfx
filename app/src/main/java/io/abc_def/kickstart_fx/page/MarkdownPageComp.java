package io.abc_def.kickstart_fx.page;

import atlantafx.sampler.page.showcase.OverviewPage;
import io.abc_def.kickstart_fx.comp.SimpleComp;
import io.abc_def.kickstart_fx.comp.base.MarkdownComp;
import io.abc_def.kickstart_fx.core.AppResources;
import io.abc_def.kickstart_fx.issue.ErrorEventFactory;
import javafx.scene.layout.Region;

import java.nio.file.Files;
import java.util.function.UnaryOperator;

public class MarkdownPageComp extends SimpleComp {

    @Override
    protected Region createSimple() {
        try {
            var md = AppResources.getResourcePath(AppResources.MAIN_MODULE, "misc/markdown.md")
                    .orElseThrow();
            var string = Files.readString(md);
            return new MarkdownComp(string, UnaryOperator.identity(), true).createRegion();
        } catch (Exception e) {
            ErrorEventFactory.fromThrowable(e).handle();
            return null;
        }
    }
}
