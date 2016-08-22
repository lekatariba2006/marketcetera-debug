package org.marketcetera.studio.views.openorders;

import org.marketcetera.studio.components.AbstractStudioViewFactory;
import org.marketcetera.studio.components.StudioViewFactory;
import org.springframework.stereotype.Component;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Component
public class OpenOrdersViewFactory
        extends AbstractStudioViewFactory<OpenOrdersView>
        implements StudioViewFactory<OpenOrdersView>
{
    /* (non-Javadoc)
     * @see org.marketcetera.studio.components.AbstractStudioViewFactory#getViewMenuDescription()
     */
    @Override
    protected String getViewMenuDescription()
    {
        return "Open Orders View";
    }
    /* (non-Javadoc)
     * @see org.marketcetera.studio.components.AbstractStudioViewFactory#create()
     */
    @Override
    protected OpenOrdersView create()
    {
        return new OpenOrdersView();
    }
}
