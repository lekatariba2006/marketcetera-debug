package org.marketcetera.studio.components;

import java.util.UUID;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TabPane;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class AbstractStudioViewFactory<V extends StudioView>
        implements StudioViewFactory<V>
{
    /* (non-Javadoc)
     * @see org.marketcetera.studio.components.StudioViewFactory#register(javafx.scene.control.TabPane, javafx.scene.control.MenuItem)
     */
    @Override
    public void register(TabPane inMainPane,
                         MenuItem inMenuItem)
    {
        inMenuItem.setId(id);
        inMenuItem.setText(getViewMenuDescription());
        inMenuItem.addEventHandler(ActionEvent.ACTION,
                                   new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent inEvent)
            {
                V view = create();
                view.newView(inMainPane);
            }
        });
    }
    protected abstract String getViewMenuDescription();
    protected abstract V create();
    /**
     * Get the id value.
     *
     * @return a <code>String</code> value
     */
    protected String getId()
    {
        return id;
    }
    /**
     * 
     */
    private final String id = UUID.randomUUID().toString();
}
