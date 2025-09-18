package io.abc_def.kickstart_fx.page;

import atlantafx.sampler.page.showcase.OverviewPage;
import com.oracle.tools.fx.monkey.MainWindow;
import io.abc_def.kickstart_fx.comp.SimpleComp;
import javafx.application.Platform;
import javafx.scene.control.MenuBar;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

public class MonkeyTesterPageComp extends SimpleComp {

    @Override
    protected Region createSimple() {
        var stack = new StackPane();
        stack.visibleProperty().subscribe(v -> {
            if (!v || stack.getChildren().size() > 0) {
                return;
            }

            var monkeyTesterWindow = new MainWindow();
            var bp = (BorderPane) monkeyTesterWindow.getScene().getRoot();
            var menuBar = (MenuBar) bp.getTop();
            menuBar.getStyleClass().add("background");
            var pane = (SplitPane) bp.getCenter();
            pane.setDividerPosition(0, 0.7);
            stack.getChildren().add(bp);
        });
        return stack;
    }
}
