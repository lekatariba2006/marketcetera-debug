package org.marketcetera.studio.components;

import javafx.scene.control.MenuItem;
import javafx.scene.control.TabPane;

public interface StudioViewFactory<V extends StudioView>
{
    void register(TabPane inMainPane,
                  MenuItem inMenuItem);
}
