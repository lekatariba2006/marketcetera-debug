package org.marketcetera.studio.views.marketdata;

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
public class MarketDataViewFactory
        extends AbstractStudioViewFactory<MarketDataView>
        implements StudioViewFactory<MarketDataView>
{
    /* (non-Javadoc)
     * @see org.marketcetera.studio.components.AbstractStudioViewFactory#getViewMenuDescription()
     */
    @Override
    protected String getViewMenuDescription()
    {
        return "Market Data View";
    }
    /* (non-Javadoc)
     * @see org.marketcetera.studio.components.AbstractStudioViewFactory#create()
     */
    @Override
    protected MarketDataView create()
    {
        return new MarketDataView();
    }
}
