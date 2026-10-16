package io.abc_def.kickstart_fx.comp.base;

import io.abc_def.kickstart_fx.comp.SimpleRegionBuilder;
import io.abc_def.kickstart_fx.platform.PlatformThread;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

import org.int4.fx.builders.common.AbstractRegionBuilder;

import java.util.Map;

public class MultiContentComp extends SimpleRegionBuilder {

    private final boolean requestFocus;
    private final boolean lazy;
    private final Map<AbstractRegionBuilder<?, ?>, ObservableValue<Boolean>> content;

    public MultiContentComp(boolean requestFocus, Map<AbstractRegionBuilder<?, ?>, ObservableValue<Boolean>> content) {
        this(requestFocus, true, content);
    }

    public MultiContentComp(
            boolean requestFocus, boolean lazy, Map<AbstractRegionBuilder<?, ?>, ObservableValue<Boolean>> content) {
        this.requestFocus = requestFocus;
        this.lazy = lazy;
        this.content = FXCollections.observableMap(content);
    }

    @Override
    protected Region createSimple() {
        ObservableMap<AbstractRegionBuilder<?, ?>, StackPane> m = FXCollections.observableHashMap();
        var stack = new StackPane();
        m.addListener((MapChangeListener<? super AbstractRegionBuilder<?, ?>, StackPane>) change -> {
            if (change.wasAdded()) {
                stack.getChildren().add(change.getValueAdded());
            } else {
                stack.getChildren().remove(change.getValueRemoved());
            }
        });

        stack.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                var selected = content.entrySet().stream()
                        .filter(e -> e.getValue().getValue())
                        .map(e -> getFocusTarget(m.get(e.getKey())))
                        .findFirst();
                if (selected.isPresent()) {
                    selected.get().requestFocus();
                }
            }
        });

        for (Map.Entry<AbstractRegionBuilder<?, ?>, ObservableValue<Boolean>> e : content.entrySet()) {
            // Only build the content once it is shown for the first time to speed up the startup
            var r = new StackPane();
            if (!lazy) {
                r.getChildren().add(e.getKey().build());
            }
            e.getValue().subscribe(val -> {
                PlatformThread.runLaterIfNeeded(() -> {
                    if (val && r.getChildren().isEmpty()) {
                        r.getChildren().add(e.getKey().build());
                    }

                    r.setManaged(val);
                    r.setVisible(val);
                    if (requestFocus && val) {
                        Platform.runLater(() -> {
                            getFocusTarget(r).requestFocus();
                        });
                    }
                });
            });
            m.put(e.getKey(), r);
        }

        return stack;
    }

    private static Node getFocusTarget(StackPane holder) {
        return holder.getChildren().isEmpty() ? holder : holder.getChildren().getFirst();
    }
}
