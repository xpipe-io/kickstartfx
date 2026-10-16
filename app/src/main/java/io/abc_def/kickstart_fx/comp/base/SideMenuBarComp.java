package io.abc_def.kickstart_fx.comp.base;

import io.abc_def.kickstart_fx.comp.RegionBuilder;
import io.abc_def.kickstart_fx.core.AppFontSizes;
import io.abc_def.kickstart_fx.core.AppLayoutModel;
import io.abc_def.kickstart_fx.core.mode.AppOperationMode;
import io.abc_def.kickstart_fx.platform.PlatformThread;
import io.abc_def.kickstart_fx.update.AppDistributionType;
import io.abc_def.kickstart_fx.update.UpdateAvailableDialog;
import io.abc_def.kickstart_fx.util.ThreadHelper;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.Property;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import lombok.AllArgsConstructor;
import org.int4.fx.builders.common.AbstractRegionBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

@AllArgsConstructor
public class SideMenuBarComp extends RegionBuilder<VBox> {

    private final Property<AppLayoutModel.Entry> value;
    private final List<AppLayoutModel.Entry> entries;
    private final ObservableList<AppLayoutModel.QueueEntry> queueEntries;

    @Override
    protected VBox createSimple() {
        var vbox = new VBox();
        vbox.setFillWidth(true);

        for (AppLayoutModel.Entry e : entries) {
            var b = new IconButtonComp(e.icon(), () -> {
                // Don't allow switching prior to startup
                if (AppOperationMode.isInStartup() || AppOperationMode.isInShutdown()) {
                    return;
                }

                if (e.action() != null) {
                    e.action().run();
                    return;
                }

                value.setValue(e);
            });

            var stack = createStyle(e, b);
            vbox.getChildren().add(stack.build());
        }

        {
            var b = new IconButtonComp("mdi2u-update", () -> UpdateAvailableDialog.showIfNeeded(false));
            var stack = createStyle(null, b);
            stack.invisible(Bindings.createBooleanBinding(
                    () -> {
                        return AppDistributionType.get()
                                        .getUpdateHandler()
                                        .getLastUpdateCheckResult()
                                        .getValue()
                                == null;
                    },
                    AppDistributionType.get().getUpdateHandler().getLastUpdateCheckResult()));
            vbox.getChildren().add(stack.build());
        }

        var filler = new Button();
        filler.setDisable(true);
        filler.setMaxHeight(3000);
        vbox.getChildren().add(filler);
        VBox.setVgrow(filler, Priority.ALWAYS);
        vbox.getStyleClass().add("sidebar-comp");

        var queueButtons = new VBox();
        Consumer<List<? extends AppLayoutModel.QueueEntry>> update = l -> {
            queueButtons.getChildren().clear();
            for (int i = l.size() - 1; i >= 0; i--) {
                var item = l.get(i);
                var b = new IconButtonComp(item.getIcon(), null);
                b.apply(struc -> {
                    var tt = TooltipHelper.create(item.getName(), null);
                    tt.setShowDelay(Duration.millis(50));
                    Tooltip.install(struc, tt);

                    struc.setOnAction(e -> {
                        struc.setDisable(true);
                        ThreadHelper.runAsync(() -> {
                            try {
                                item.getAction().run();
                            } finally {
                                Platform.runLater(() -> {
                                    queueEntries.remove(item);
                                });
                            }
                        });
                        e.consume();
                    });
                });
                var stack = createStyle(null, b);
                queueButtons.getChildren().add(stack.build());
            }
        };

        update.accept(new ArrayList<>(queueEntries));
        queueEntries.addListener((ListChangeListener<? super AppLayoutModel.QueueEntry>) c -> {
            var l = new ArrayList<>(c.getList());
            PlatformThread.runLaterIfNeeded(() -> {
                update.accept(l);
            });
        });
        vbox.getChildren().add(queueButtons);
        vbox.setMinHeight(0);
        vbox.setPrefHeight(0);

        return vbox;
    }

    private AbstractRegionBuilder<?, ?> createStyle(AppLayoutModel.Entry e, IconButtonComp b) {
        var selected = PseudoClass.getPseudoClass("selected");

        b.apply(struc -> {
            AppFontSizes.lg(struc);
            struc.setAlignment(Pos.CENTER);

            struc.pseudoClassStateChanged(selected, Objects.equals(value.getValue(), e));
            if (e == null) {
                return;
            }

            value.addListener((c, o, n) -> {
                PlatformThread.runLaterIfNeeded(() -> {
                    struc.pseudoClassStateChanged(selected, Objects.equals(n, e));
                });
            });
        });

        var selectedBorder = Bindings.createObjectBinding(
                () -> {
                    var c = Platform.getPreferences()
                            .getAccentColor()
                            .desaturate()
                            .desaturate();
                    return new Background(new BackgroundFill(c, new CornerRadii(8), new Insets(16, 2, 16, 1)));
                },
                Platform.getPreferences().accentColorProperty());
        var hoverBorder = Bindings.createObjectBinding(
                () -> {
                    var c = Platform.getPreferences()
                            .getAccentColor()
                            .darker()
                            .desaturate()
                            .desaturate();
                    return new Background(new BackgroundFill(c, new CornerRadii(8), new Insets(16, 2, 16, 1)));
                },
                Platform.getPreferences().accentColorProperty());
        var noneBorder = Bindings.createObjectBinding(
                () -> {
                    return Background.fill(Color.TRANSPARENT);
                },
                Platform.getPreferences().accentColorProperty());

        var indicator = RegionBuilder.empty().style("indicator");
        var stack = new StackComp(List.of(indicator, b)).apply(struc -> struc.setAlignment(Pos.CENTER_LEFT));
        stack.apply(struc -> {
            var indicatorRegion = (Region) struc.getChildren().getFirst();
            var buttonRegion = (Region) struc.getChildren().get(1);
            indicatorRegion.setMaxWidth(7);
            indicatorRegion.prefHeightProperty().bind(buttonRegion.heightProperty());
            indicatorRegion
                    .backgroundProperty()
                    .bind(Bindings.createObjectBinding(
                            () -> {
                                if (Objects.equals(value.getValue(), e)) {
                                    return selectedBorder.get();
                                }

                                if (struc.isHover()) {
                                    return hoverBorder.get();
                                }

                                return noneBorder.get();
                            },
                            struc.hoverProperty(),
                            value,
                            hoverBorder,
                            selectedBorder,
                            noneBorder));
        });
        return stack;
    }
}
