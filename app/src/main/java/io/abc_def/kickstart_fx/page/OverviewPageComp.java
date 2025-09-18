package io.abc_def.kickstart_fx.page;

import atlantafx.sampler.page.showcase.BlueprintsPage;
import atlantafx.sampler.page.showcase.OverviewPage;
import io.abc_def.kickstart_fx.comp.SimpleComp;
import javafx.scene.layout.Region;

public class OverviewPageComp extends SimpleComp {

    @Override
    protected Region createSimple() {
        var atlantaOverview = new OverviewPage();
        return atlantaOverview;
    }
}
