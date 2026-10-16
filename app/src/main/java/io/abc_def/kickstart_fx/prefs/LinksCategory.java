package io.abc_def.kickstart_fx.prefs;

import io.abc_def.kickstart_fx.comp.RegionBuilder;
import io.abc_def.kickstart_fx.comp.base.ModalOverlay;
import io.abc_def.kickstart_fx.comp.base.TileButtonComp;
import io.abc_def.kickstart_fx.platform.LabelGraphic;
import io.abc_def.kickstart_fx.platform.OptionsBuilder;
import io.abc_def.kickstart_fx.util.Hyperlinks;

import org.int4.fx.builders.common.AbstractRegionBuilder;

public class LinksCategory extends AppPrefsCategory {

    private AbstractRegionBuilder<?, ?> createLinks() {
        return new OptionsBuilder()
                .addTitle("links")
                .addComp(RegionBuilder.vspacer(19))
                .addComp(
                        new TileButtonComp(
                                        "documentation", "documentationDescription", "mdi2b-book-open-variant", e -> {
                                            Hyperlinks.open(Hyperlinks.DOCS);
                                            e.consume();
                                        })
                                .maxWidth(2000),
                        null)
                .addComp(new TileButtonComp(
                                "thirdParty", "thirdPartyDescription", "mdi2o-open-source-initiative", e -> {
                                    var comp = new ThirdPartyDependencyListComp()
                                            .prefWidth(650)
                                            .style("open-source-notices");
                                    var modal = ModalOverlay.of("openSourceNotices", comp);
                                    modal.show();
                                })
                        .maxWidth(2000))
                .addComp(RegionBuilder.vspacer(40))
                .buildComp();
    }

    @Override
    public String getId() {
        return "links";
    }

    @Override
    protected LabelGraphic getIcon() {
        return new LabelGraphic.IconGraphic("mdi2l-link-box-outline");
    }

    @Override
    public AbstractRegionBuilder<?, ?> create() {
        return createLinks().style("information").style("about-tab").apply(struc -> struc.setPrefWidth(600));
    }
}
