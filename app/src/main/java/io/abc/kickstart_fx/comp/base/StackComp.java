package io.abc.kickstart_fx.comp.base;

import io.abc.kickstart_fx.comp.Comp;
import io.abc.kickstart_fx.comp.CompStructure;
import io.abc.kickstart_fx.comp.SimpleCompStructure;

import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;

import java.util.List;

public class StackComp extends Comp<CompStructure<StackPane>> {

    private final List<Comp<?>> comps;

    public StackComp(List<Comp<?>> comps) {
        this.comps = List.copyOf(comps);
    }

    @Override
    public CompStructure<StackPane> createBase() {
        var pane = new StackPane();
        for (var c : comps) {
            pane.getChildren().add(c.createRegion());
        }
        pane.setAlignment(Pos.CENTER);
        return new SimpleCompStructure<>(pane);
    }
}
