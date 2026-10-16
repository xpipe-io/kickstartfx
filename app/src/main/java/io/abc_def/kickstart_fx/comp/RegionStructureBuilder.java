package io.abc_def.kickstart_fx.comp;

import io.abc_def.kickstart_fx.platform.BindingsHelper;

import javafx.scene.layout.Region;

import org.int4.fx.builders.common.AbstractRegionBuilder;
import org.int4.fx.builders.context.BuildContext;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class RegionStructureBuilder<R extends Region, S extends RegionStructure<R>>
        extends AbstractRegionBuilder<R, RegionStructureBuilder<R, S>> {

    private final List<Consumer<? super S>> options = new ArrayList<>();

    public RegionStructureBuilder() {
        apply(t -> {
            BindingsHelper.preserve(t, RegionStructureBuilder.this);
        });
    }

    public final RegionStructureBuilder<R, S> applyStructure(Consumer<? super S> option) {
        options.add(option);
        return self();
    }

    protected final void initializeStructure(S obj) {
        for (Consumer<? super S> option : options) {
            option.accept(obj);
        }
    }

    @Override
    public final R build(BuildContext context) {
        S struc = buildStructure();
        return struc.get();
    }

    public final S buildStructure() {
        S struc = createBase();
        initializeStructure(struc);
        initialize(struc.get());
        return struc;
    }

    protected abstract S createBase();
}
