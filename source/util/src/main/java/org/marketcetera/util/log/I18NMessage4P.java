package org.marketcetera.util.log;

/**
 * An internationalized message, requiring exactly four parameters.
 * 
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

import java.util.Locale;
import org.marketcetera.core.ClassVersion;

@ClassVersion("$Id$")
public class I18NMessage4P
    extends I18NMessage
{

    // CONSTRUCTORS.

    /**
     * Constructor mirroring superclass constructor.
     *
     * @see I18NMessage#I18NMessage(I18NLoggerProxy,String,String)
     */

    public I18NMessage4P
        (I18NLoggerProxy loggerProxy,
         String messageId,
         String entryId)
    {
        super(loggerProxy,messageId,entryId);
    }

    /**
     * Constructor mirroring superclass constructor.
     *
     * @see I18NMessage#I18NMessage(I18NLoggerProxy,String)
     */

    public I18NMessage4P
        (I18NLoggerProxy loggerProxy,
         String messageId)
    {
        super(loggerProxy,messageId);
    }


    // INSTANCE METHODS.

    /**
     * A convenience method for {@link
     * I18NMessageProvider#getText(Locale,I18NMessage,Object...)}.
     */

    public String getText
        (Locale locale,
         Object p1,
         Object p2,
         Object p3,
         Object p4)
    {
        return getMessageProvider().getText(locale,this,p1,p2,p3,p4);
    }

    /**
     * A convenience method for {@link
     * I18NMessageProvider#getText(I18NMessage,Object...)}.
     */

    public String getText
        (Object p1,
         Object p2,
         Object p3,
         Object p4)
    {
        return getMessageProvider().getText(this,p1,p2,p3,p4);
    }

    /**
     * A convenience method for {@link
     * I18NLoggerProxy#error(Object,Throwable,I18NMessage,Object...)}.
     */

    public void error
        (Object category,
         Throwable throwable,
         Object p1,
         Object p2,
         Object p3,
         Object p4)
    {
        getLoggerProxy().error(category,throwable,this,p1,p2,p3,p4);
    }

    /**
     * A convenience method for {@link
     * I18NLoggerProxy#error(Object,I18NMessage,Object...)}.
     */
    
    public void error
        (Object category,
         Object p1,
         Object p2,
         Object p3,
         Object p4)
    {
        getLoggerProxy().error(category,this,p1,p2,p3,p4);
    }

    /**
     * A convenience method for {@link
     * I18NLoggerProxy#warn(Object,Throwable,I18NMessage,Object...)}.
     */

    public void warn
        (Object category,
         Throwable throwable,
         Object p1,
         Object p2,
         Object p3,
         Object p4)
    {
        getLoggerProxy().warn(category,throwable,this,p1,p2,p3,p4);
    }

    /**
     * A convenience method for {@link
     * I18NLoggerProxy#warn(Object,I18NMessage,Object...)}.
     */
    
    public void warn
        (Object category,
         Object p1,
         Object p2,
         Object p3,
         Object p4)
    {
        getLoggerProxy().warn(category,this,p1,p2,p3,p4);
    }

    /**
     * A convenience method for {@link
     * I18NLoggerProxy#info(Object,Throwable,I18NMessage,Object...)}.
     */

    public void info
        (Object category,
         Throwable throwable,
         Object p1,
         Object p2,
         Object p3,
         Object p4)
    {
        getLoggerProxy().info(category,throwable,this,p1,p2,p3,p4);
    }

    /**
     * A convenience method for {@link
     * I18NLoggerProxy#info(Object,I18NMessage,Object...)}.
     */
    
    public void info
        (Object category,
         Object p1,
         Object p2,
         Object p3,
         Object p4)
    {
        getLoggerProxy().info(category,this,p1,p2,p3,p4);
    }

    /**
     * A convenience method for {@link
     * I18NLoggerProxy#debug(Object,Throwable,I18NMessage,Object...)}.
     */

    public void debug
        (Object category,
         Throwable throwable,
         Object p1,
         Object p2,
         Object p3,
         Object p4)
    {
        getLoggerProxy().debug(category,throwable,this,p1,p2,p3,p4);
    }

    /**
     * A convenience method for {@link
     * I18NLoggerProxy#debug(Object,I18NMessage,Object...)}.
     */
    
    public void debug
        (Object category,
         Object p1,
         Object p2,
         Object p3,
         Object p4)
    {
        getLoggerProxy().debug(category,this,p1,p2,p3,p4);
    }

    /**
     * A convenience method for {@link
     * I18NLoggerProxy#trace(Object,Throwable,I18NMessage,Object...)}.
     */

    public void trace
        (Object category,
         Throwable throwable,
         Object p1,
         Object p2,
         Object p3,
         Object p4)
    {
        getLoggerProxy().trace(category,throwable,this,p1,p2,p3,p4);
    }

    /**
     * A convenience method for {@link
     * I18NLoggerProxy#trace(Object,I18NMessage,Object...)}.
     */
    
    public void trace
        (Object category,
         Object p1,
         Object p2,
         Object p3,
         Object p4)
    {
        getLoggerProxy().trace(category,this,p1,p2,p3,p4);
    }
}
