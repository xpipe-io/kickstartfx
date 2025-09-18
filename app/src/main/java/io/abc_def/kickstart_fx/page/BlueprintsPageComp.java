package io.abc_def.kickstart_fx.page;

import atlantafx.sampler.page.showcase.BlueprintsPage;
import atlantafx.sampler.page.showcase.musicplayer.MusicPlayerPage;
import io.abc_def.kickstart_fx.comp.SimpleComp;
import javafx.scene.layout.Region;

public class BlueprintsPageComp extends SimpleComp {

    @Override
    protected Region createSimple() {
        var atlantaBlueprints = new BlueprintsPage();
        return atlantaBlueprints;
    }
}
