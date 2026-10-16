package io.abc_def.kickstart_fx.page;

import io.abc_def.kickstart_fx.comp.SimpleRegionBuilder;
import io.abc_def.kickstart_fx.comp.base.MarkdownComp;
import io.abc_def.kickstart_fx.core.AppResources;
import io.abc_def.kickstart_fx.issue.ErrorEventFactory;

import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

import java.nio.file.Files;
import java.util.function.UnaryOperator;

public class MarkdownPageComp extends SimpleRegionBuilder {

    @Override
    protected Region createSimple() {
        try {
            var md = AppResources.getResourcePath(AppResources.MAIN_MODULE, "misc/markdown.md")
                    .orElseThrow();
            var string = Files.readString(md);
            return new MarkdownComp(string, UnaryOperator.identity(), true).build();
        } catch (Exception e) {
            ErrorEventFactory.fromThrowable(e).handle();
            return new StackPane();
        }
    }
}
