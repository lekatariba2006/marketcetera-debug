package org.marketcetera.studio.components;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class AbstractStudioView
        implements StudioView
{
    /* (non-Javadoc)
     * @see org.marketcetera.studio.components.MainLayout.View#newView(javafx.scene.control.TabPane)
     */
    @Override
    public void newView(TabPane inPane)
    {
        inPane.getTabs().add(tab);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.studio.components.MainLayout.View#getTab()
     */
    @Override
    public Tab getTab()
    {
        return tab;
    }
    /**
     * Create a new AbstractStudioView instance.
     *
     * @param inViewTitle
     */
    protected AbstractStudioView(String inViewTitle)
    {
        tab = new Tab();
        tab.setText(inViewTitle);
    }
    private final Tab tab;
}
