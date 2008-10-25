package org.marketcetera.event;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.CoreException;
import org.marketcetera.event.AbstractEventTranslatorTest.MessageEvent;
import org.marketcetera.marketdata.DataRequest;

import quickfix.Message;

/* $License$ */
/**
 * Test implementation of <code>IEventTranslator</code>.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.5.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class MockEventTranslator
        extends AbstractEventTranslator
{
    private static boolean sTranslateToEventsThrows = false;
    private static boolean sTranslateToEventsReturnsNull = false;
    private static boolean sTranslateToEventsReturnsZeroEvents = false;
    private static MockEventTranslator sInstance = new MockEventTranslator();
    private static DataRequest requestToReturn = null;
    private static Message messageToReturn = null;
    /**
     * Gets a <code>TestEventTranslator</code> value.
     *
     * @return a <code>TestEventTranslator</code> value
     */
    public static MockEventTranslator getTestEventTranslator()
    {
        return sInstance;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.IEventTranslator#translate(java.lang.Object)
     */
    public List<EventBase> translate(Object inData)
            throws CoreException
    {
        if(getTranslateToEventsThrows()) {
            throw new NullPointerException("This exception is expected"); //$NON-NLS-1$
        }
        if(getTranslateToEventsReturnsNull()) {
            return null;
        }
        if(getTranslateToEventsReturnsZeroEvents()) {
            return new ArrayList<EventBase>();
        }
        DataRequest request = null;
        if(requestToReturn != null) {
            request = requestToReturn;
        } else if(inData instanceof DataRequest) {
            request = (DataRequest)inData;
        }
        if(inData instanceof SymbolExchangeEvent) {
            SymbolExchangeEvent see = (SymbolExchangeEvent)inData;
            updateEventFixMessageSnapshot(see);
            return Arrays.asList(new EventBase[] { see });
        }
        return Arrays.asList(new EventBase[] { new MessageEvent(request) });
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.IEventTranslator#translate(org.marketcetera.event.EventBase)
     */
    public String translate(EventBase inEvent)
            throws CoreException
    {
        return inEvent.toString();
    }
    public static boolean getTranslateToEventsThrows()
    {
        return sTranslateToEventsThrows;
    }
    public static void setTranslateToEventsThrows(boolean inTranslateToEventsThrows)
    {
        sTranslateToEventsThrows = inTranslateToEventsThrows;
    }
    public static boolean getTranslateToEventsReturnsZeroEvents()
    {
        return sTranslateToEventsReturnsZeroEvents;
    }
    public static void setTranslateToEventsReturnsZeroEvents(boolean inTranslateToEventsReturnsZeroEvents)
    {
        sTranslateToEventsReturnsZeroEvents = inTranslateToEventsReturnsZeroEvents;
    }
    public static boolean getTranslateToEventsReturnsNull()
    {
        return sTranslateToEventsReturnsNull;
    }
    public static void setTranslateToEventsReturnsNull(boolean inTranslateToEventsReturnsNull)
    {
        sTranslateToEventsReturnsNull = inTranslateToEventsReturnsNull;
    }
    /**
     * Sets the requestToReturn value.
     *
     * @param a <code>DataRequest</code> value
     */
    public static void setRequestToReturn(DataRequest inRequestToReturn)
    {
        requestToReturn = inRequestToReturn;
    }
    /**
     * Sets the messageToReturn value.
     *
     * @param a <code>Message</code> value
     */
    public static void setMessageToReturn(Message inMessageToReturn)
    {
        messageToReturn = inMessageToReturn;
    }
    /**
     * Resets the behavior to the default.  Tests that configure this class should use this method to cleanup.
     */
    public static void reset()
    {
    	requestToReturn = null;
    	sTranslateToEventsReturnsNull = false;
    	sTranslateToEventsReturnsZeroEvents = false;
    	sTranslateToEventsThrows = false;
    }
}
