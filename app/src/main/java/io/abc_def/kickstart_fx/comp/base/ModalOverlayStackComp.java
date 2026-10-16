package io.abc_def.kickstart_fx.comp.base;

import io.abc_def.kickstart_fx.comp.SimpleRegionBuilder;
import io.abc_def.kickstart_fx.core.window.AppDialog;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.layout.Region;

import org.int4.fx.builders.common.AbstractRegionBuilder;

public class ModalOverlayStackComp extends SimpleRegionBuilder {

    private final AbstractRegionBuilder<?, ?> background;

    public ModalOverlayStackComp(AbstractRegionBuilder<?, ?> background) {
        this.background = background;
    }

    @Override
    protected Region createSimple() {
        var current = background;
        for (var i = 0; i < 6; i++) {
            current = buildModalOverlay(current, i);
        }
        return current.build();
    }

    private AbstractRegionBuilder<?, ?> buildModalOverlay(AbstractRegionBuilder<?, ?> current, int index) {
        ObservableList<ModalOverlay> modalOverlays = AppDialog.getModalOverlays();
        var prop = new SimpleObjectProperty<>(modalOverlays.size() > index ? modalOverlays.get(index) : null);
        modalOverlays.addListener((ListChangeListener<? super ModalOverlay>) c -> {
            prop.set(modalOverlays.size() > index ? modalOverlays.get(index) : null);
        });
        prop.addListener((observable, oldValue, newValue) -> {
            if (newValue == null && oldValue != null) {
                AppDialog.hide(oldValue);
            }
        });
        var comp = new ModalOverlayComp(current, prop);
        comp.style("modal-overlay-stack-element");
        return comp;
    }
}
