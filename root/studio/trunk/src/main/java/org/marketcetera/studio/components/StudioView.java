package org.marketcetera.studio.components;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public interface StudioView
{
    Tab getTab();
    void newView(TabPane inPane);
}