package io.xpipe.kickstart_fx.prefs;

import io.xpipe.kickstart_fx.comp.Comp;
import io.xpipe.kickstart_fx.comp.base.ModalOverlay;
import io.xpipe.kickstart_fx.comp.base.TileButtonComp;
import io.xpipe.kickstart_fx.platform.LabelGraphic;
import io.xpipe.kickstart_fx.platform.OptionsBuilder;
import io.xpipe.kickstart_fx.util.Hyperlinks;

public class LinksCategory extends AppPrefsCategory {

    private Comp<?> createLinks() {
        return new OptionsBuilder()
                .addTitle("links")
                .addComp(Comp.vspacer(19))
                .addComp(
                        new TileButtonComp("discord", "discordDescription", "bi-discord", e -> {
                                    Hyperlinks.open(Hyperlinks.DISCORD);
                                    e.consume();
                                })
                                .grow(true, false),
                        null)
                .addComp(
                        new TileButtonComp(
                                        "documentation", "documentationDescription", "mdi2b-book-open-variant", e -> {
                                            Hyperlinks.open(Hyperlinks.DOCS);
                                            e.consume();
                                        })
                                .grow(true, false),
                        null)
                .addComp(
                        new TileButtonComp("thirdParty", "thirdPartyDescription", "mdi2o-open-source-initiative", e -> {
                                    var comp = new ThirdPartyDependencyListComp()
                                            .prefWidth(650)
                                            .styleClass("open-source-notices");
                                    var modal = ModalOverlay.of("openSourceNotices", comp);
                                    modal.show();
                                })
                                .grow(true, false))
                .addComp(Comp.vspacer(40))
                .buildComp();
    }

    @Override
    protected String getId() {
        return "links";
    }

    @Override
    protected LabelGraphic getIcon() {
        return new LabelGraphic.IconGraphic("mdi2l-link-box-outline");
    }

    @Override
    protected Comp<?> create() {
        return createLinks().styleClass("information").styleClass("about-tab").apply(struc -> struc.get()
                .setPrefWidth(600));
    }
}
