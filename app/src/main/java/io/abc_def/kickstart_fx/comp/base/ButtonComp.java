package io.abc_def.kickstart_fx.comp.base;

import io.abc_def.kickstart_fx.comp.RegionBuilder;
import io.abc_def.kickstart_fx.platform.LabelGraphic;
import io.abc_def.kickstart_fx.platform.PlatformThread;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.css.Size;
import javafx.css.SizeUnits;
import javafx.scene.Node;
import javafx.scene.control.Button;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.kordamp.ikonli.javafx.FontIcon;

@Getter
@AllArgsConstructor
public class ButtonComp extends RegionBuilder<Button> {

    private final ObservableValue<String> name;
    private final ObservableValue<LabelGraphic> graphic;
    private final Runnable listener;

    public ButtonComp(ObservableValue<String> name, Runnable listener) {
        this.name = name;
        this.graphic = new SimpleObjectProperty<>(null);
        this.listener = listener;
    }

    public ButtonComp(ObservableValue<String> name, Node graphic, Runnable listener) {
        this.name = name;
        this.graphic = new SimpleObjectProperty<>(new LabelGraphic.NodeGraphic(() -> graphic));
        this.listener = listener;
    }

    @Override
    public Button createSimple() {
        var button = new Button(null);
        button.setMnemonicParsing(false);
        if (name != null) {
            name.subscribe(t -> {
                PlatformThread.runLaterIfNeeded(() -> button.setText(t));
            });
        }
        if (graphic != null) {
            graphic.subscribe(t -> {
                PlatformThread.runLaterIfNeeded(() -> {
                    if (t == null) {
                        button.setGraphic(null);
                        return;
                    }

                    var n = t.createGraphicNode();
                    button.setGraphic(n);
                    if (n instanceof FontIcon f && button.getFont() != null) {
                        f.setIconSize((int) new Size(button.getFont().getSize(), SizeUnits.PT).pixels());
                    }
                });
            });

            button.fontProperty().subscribe(c -> {
                if (button.getGraphic() instanceof FontIcon f) {
                    f.setIconSize((int) new Size(c.getSize(), SizeUnits.PT).pixels());
                }
            });
        }
        if (listener != null) {
            button.setOnAction(e -> getListener().run());
        }
        button.getStyleClass().add("button-comp");
        return button;
    }
}
