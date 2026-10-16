package io.abc_def.kickstart_fx.platform;

import javafx.scene.Node;

import java.util.ArrayList;
import java.util.List;

public class BindingsHelper {

    // Keeps the target alive for as long as the node is alive
    // The node properties are released together with the node, so no manual cleanup is needed
    @SuppressWarnings("unchecked")
    public static void preserve(Node source, Object target) {
        var list = (List<Object>)
                source.getProperties().computeIfAbsent("bindingsHelperPreserved", k -> new ArrayList<>());
        list.add(target);
    }
}
