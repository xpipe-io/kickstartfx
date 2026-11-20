package io.abc_def.kickstart_fx.comp.base;

import io.abc_def.kickstart_fx.core.AppI18n;
import io.abc_def.kickstart_fx.core.window.AppDialog;
import io.abc_def.kickstart_fx.platform.LabelGraphic;

import javafx.beans.value.ObservableValue;

import lombok.Builder;
import lombok.Setter;
import lombok.Singular;
import lombok.Value;
import lombok.With;
import lombok.experimental.NonFinal;
import org.int4.fx.builders.common.AbstractRegionBuilder;

import java.util.ArrayList;
import java.util.List;

@Value
@With
@Builder(toBuilder = true)
public class ModalOverlay {

    ObservableValue<String> title;
    AbstractRegionBuilder<?, ?> content;
    LabelGraphic graphic;

    @Singular
    List<Object> buttons;

    @NonFinal
    @Setter
    boolean hasCloseButton;

    @NonFinal
    @Setter
    boolean requireCloseButtonForClose;

    public static ModalOverlay of(AbstractRegionBuilder<?, ?> content) {
        return of((ObservableValue<String>) null, content, null);
    }

    public static ModalOverlay of(String titleKey, AbstractRegionBuilder<?, ?> content) {
        return of(titleKey, content, null);
    }

    public static ModalOverlay of(String titleKey, AbstractRegionBuilder<?, ?> content, LabelGraphic graphic) {
        return of(titleKey != null ? AppI18n.observable(titleKey) : null, content, graphic);
    }

    public static ModalOverlay of(
            ObservableValue<String> title, AbstractRegionBuilder<?, ?> content, LabelGraphic graphic) {
        return new ModalOverlay(title, content, graphic, new ArrayList<>(), true, false);
    }

    public ModalOverlay withDefaultButtons(Runnable action) {
        addButton(ModalButton.cancel());
        addButton(ModalButton.ok(action));
        return this;
    }

    public ModalButton addButton(ModalButton button) {
        buttons.add(button);
        return button;
    }

    public void addButtonBarComp(AbstractRegionBuilder<?, ?> comp) {
        buttons.add(comp);
    }

    public void persist() {
        this.hasCloseButton = false;
        this.requireCloseButtonForClose = true;
    }

    public void show() {
        AppDialog.show(this, false);
    }

    public void hide() {
        AppDialog.hide(this);
    }

    public boolean isShowing() {
        return AppDialog.getModalOverlays().contains(this);
    }

    public void showAndWait() {
        AppDialog.showAndWait(this);
    }

    public void close() {
        AppDialog.closeDialog(this);
    }
}
