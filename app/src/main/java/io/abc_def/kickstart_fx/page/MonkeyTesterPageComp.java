package io.abc_def.kickstart_fx.page;

import io.abc_def.kickstart_fx.comp.SimpleRegionBuilder;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

import com.oracle.tools.fx.monkey.MainWindow;

public class MonkeyTesterPageComp extends SimpleRegionBuilder {

    @Override
    @SuppressWarnings("unchecked")
    protected Region createSimple() {
        var stack = new StackPane();
        stack.visibleProperty().subscribe(v -> {
            if (!v || !stack.getChildren().isEmpty()) {
                return;
            }

            var monkeyTesterWindow = new MainWindow(null);
            var bp = (BorderPane) monkeyTesterWindow.getScene().getRoot();
            var menuBar = (MenuBar) bp.getTop();
            menuBar.getStyleClass().add("background");

            var pane = (SplitPane) bp.getCenter();
            // The page list is not resized with its parent, so its width is fixed on the first layout
            // Only set the divider once the actual width is known, otherwise it ends up at a random position
            // Hidden pages can be laid out with a different size, so wait until the page is actually shown
            pane.widthProperty().addListener(new ChangeListener<>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    if (newValue.doubleValue() <= 0 || !isShown(pane)) {
                        return;
                    }

                    pane.widthProperty().removeListener(this);
                    // The split pane skin recomputes the divider positions in its own layout pass after a resize
                    // to keep the size of the page list, so only set the position once the layout is done
                    Platform.runLater(() -> pane.setDividerPosition(0, 0.15));
                }
            });

            var lv = (ListView<Object>) pane.getItems().getFirst();
            lv.setCellFactory(null);

            stack.getChildren().add(bp);
        });
        return stack;
    }

    private static boolean isShown(Node node) {
        if (node.getScene() == null) {
            return false;
        }

        for (var current = node; current != null; current = current.getParent()) {
            if (!current.isVisible()) {
                return false;
            }
        }
        return true;
    }
}
