package io.abc_def.kickstart_fx.comp.base;

import io.abc_def.kickstart_fx.comp.RegionStructure;
import io.abc_def.kickstart_fx.comp.RegionStructureBuilder;
import io.abc_def.kickstart_fx.core.AppLayoutModel;
import io.abc_def.kickstart_fx.core.AppProperties;
import io.abc_def.kickstart_fx.page.PrefsPageComp;
import io.abc_def.kickstart_fx.platform.PlatformThread;
import io.abc_def.kickstart_fx.prefs.AppPrefs;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

import org.int4.fx.builders.common.AbstractRegionBuilder;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class AppLayoutComp extends RegionStructureBuilder<BorderPane, AppLayoutComp.Structure> {

    @Override
    protected Structure createBase() {
        var model = AppLayoutModel.get();
        Map<AbstractRegionBuilder<?, ?>, ObservableValue<Boolean>> map = model.getEntries().stream()
                .filter(entry -> entry.comp() != null)
                .collect(Collectors.toMap(
                        entry -> entry.comp(),
                        entry -> Bindings.createBooleanBinding(
                                () -> {
                                    return Objects.equals(model.getSelected().getValue(), entry);
                                },
                                model.getSelected()),
                        (v1, v2) -> v2,
                        LinkedHashMap::new));
        // Pages are only built once they are shown, unless configured otherwise
        var multi = new MultiContentComp(true, !AppProperties.get().isBuildAllPages(), map);
        multi.style("background");

        var pane = new BorderPane();
        var sidebar = new SideMenuBarComp(model.getSelected(), model.getEntries(), model.getQueueEntries());
        StackPane multiR = (StackPane) multi.build();
        pane.setCenter(multiR);
        var sidebarR = sidebar.build();
        pane.setLeft(sidebarR);
        model.getSelected().addListener((c, o, n) -> {
            var wasPrefs = o != null && o.comp() instanceof PrefsPageComp;
            if (wasPrefs) {
                AppPrefs.get().save();
            }
        });
        pane.getStyleClass().add("layout");
        return new Structure(pane, multiR, sidebarR, new ArrayList<>(multiR.getChildren()));
    }

    public record Structure(BorderPane pane, StackPane stack, Region sidebar, List<Node> children)
            implements RegionStructure<BorderPane> {

        public void prepareAddition() {
            stack.getChildren().clear();
            sidebar.setDisable(true);
        }

        public void show() {
            stack.getChildren().add(children.getFirst());
            for (int i = 1; i < children.size(); i++) {
                children.get(i).setVisible(false);
                children.get(i).setManaged(false);
                stack.getChildren().add(children.get(i));
            }
            PlatformThread.runNestedLoopIteration();
            sidebar.setDisable(false);
            stack.requestFocus();
        }

        @Override
        public BorderPane get() {
            return pane;
        }
    }
}
