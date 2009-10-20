package org.marketcetera.event.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.SystemUtils;
import org.marketcetera.event.AskEvent;
import org.marketcetera.event.BidEvent;
import org.marketcetera.event.DepthOfBookEvent;
import org.marketcetera.event.Event;
import org.marketcetera.event.QuoteEvent;
import org.marketcetera.event.beans.InstrumentBean;
import org.marketcetera.event.util.PriceAndSizeComparator;
import org.marketcetera.marketdata.DateUtils;
import org.marketcetera.marketdata.OrderBook;
import org.marketcetera.trade.Instrument;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
class DepthOfBookEventImpl
        implements DepthOfBookEvent
{
    /* (non-Javadoc)
     * @see org.marketcetera.event.DepthOfBookEvent#getAsks()
     */
    @Override
    public List<AskEvent> getAsks()
    {
        return Collections.unmodifiableList(asks);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.DepthOfBookEvent#getBids()
     */
    @Override
    public List<BidEvent> getBids()
    {
        return Collections.unmodifiableList(bids);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.AggregateEvent#decompose()
     */
    @Override
    public List<Event> decompose()
    {
        List<Event> events = new ArrayList<Event>();
        events.addAll(bids);
        events.addAll(asks);
        return Collections.unmodifiableList(events);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.Event#getMessageId()
     */
    @Override
    public long getMessageId()
    {
        return event.getMessageId();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.Event#getSource()
     */
    @Override
    public Object getSource()
    {
        return event.getSource();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.Event#getTimestamp()
     */
    @Override
    public Date getTimestamp()
    {
        return event.getTimestamp();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.Event#setSource(java.lang.Object)
     */
    @Override
    public void setSource(Object inSource)
    {
        event.setSource(inSource);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.TimestampCarrier#getTimeMillis()
     */
    @Override
    public long getTimeMillis()
    {
        return event.getTimeMillis();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.HasInstrument#getInstrument()
     */
    @Override
    public Instrument getInstrument()
    {
        return instrument.getInstrument();
    }
    /**
     * Create a new DepthOfBookEventImpl instance.
     *
     * @param inMessageId
     * @param inTimestamp
     * @param inBids
     * @param inAsks
     * @param inInstrument
     * @throws EventValidationException
     */
    DepthOfBookEventImpl(long inMessageId,
                         Date inTimestamp,
                         List<BidEvent> inBids,
                         List<AskEvent> inAsks,
                         Instrument inInstrument)
    {
        instrument.setInstrument(inInstrument);
        event = new EventImpl(inMessageId,
                              inTimestamp);
        validateList(inBids,
                     inInstrument);
        bids.addAll(inBids);
        validateList(inAsks,
                     inInstrument);
        asks.addAll(inAsks);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.DepthOfBookEvent#equivalent(org.marketcetera.event.DepthOfBookEvent)
     */
    @Override
    public boolean equivalent(DepthOfBookEvent inOther)
    {
        if(inOther == null) {
            return false;
        }
        if(this == inOther) {
            return true;
        }
        if(!getInstrument().equals(inOther.getInstrument())) {
            return false;
        }
        return compareList(asks,
                           inOther.getAsks()) &&
                           compareList(bids,
                                       inOther.getBids());
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder output = new StringBuilder();
        output.append("Depth of book for ").append(getInstrument()).append(" at ").append(DateUtils.dateToString(getTimestamp())).append(SystemUtils.LINE_SEPARATOR); //$NON-NLS-1$ //$NON-NLS-2$
        output.append(OrderBook.printBook(bids.iterator(),
                                          asks.iterator(),
                                          true));
        return output.toString();
    }
    /**
     * Validates the contents of the given list.
     *
     *
     * @param inQuotes a <code>List&lt;? extends QuoteEvent&gt;</code> value
     * @param inInstrument an <code>Instrument</code> value
     * @throws NullPointerException if the list or any entry in the list is null
     * @throws IllegalArgumentException if an entry in the list is for a different instrument than the one given
     */
    private static <I extends Instrument> void validateList(List<? extends QuoteEvent> inQuotes,
                                                            I inInstrument)
    {
        for(QuoteEvent quote : inQuotes) {
            if(quote == null) {
                throw new NullPointerException();
            }
            if(!inInstrument.equals(quote.getInstrument())) {
                throw new IllegalArgumentException();
            }
        }
    }
    /**
     * Compares two lists of objects of a given subclass of {@link QuoteEvent} on the basis of their
     * price and size only. 
     *
     * @param inQuotes1 a <code>List&lt;T&gt;</code> value
     * @param inQuotes2 a <code>List&lt;T&gt;</code> value
     * @return a <code>boolean</code> value
     */
    private static <I extends Instrument,T extends QuoteEvent> boolean compareList(List<T> inQuotes1,
                                                                                      List<T> inQuotes2)
    {
        if(inQuotes1.size() != inQuotes2.size()) {
            return false;
        }
        int index = 0;
        for(QuoteEvent quote : inQuotes1) {
            QuoteEvent otherQuote = inQuotes2.get(index++);
            if(PriceAndSizeComparator.instance.compare(quote,
                                                       otherQuote) != 0) {
                return false;
            }
        }
        return true;
    }
    /**
     * 
     */
    private final EventImpl event;
    /**
     * 
     */
    private final InstrumentBean instrument = new InstrumentBean();
    /**
     * the bid side of the book
     */
    private final List<BidEvent> bids = new ArrayList<BidEvent>();
    /**
     * the ask side of the book
     */
    private final List<AskEvent> asks = new ArrayList<AskEvent>();
    private static final long serialVersionUID = 1L;
}
