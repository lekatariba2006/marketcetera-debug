package org.marketcetera.util.log;

import java.text.MessageFormat;
import java.util.Locale;
import org.apache.commons.i18n.MessageManager;
import org.apache.commons.i18n.MessageNotFoundException;
import org.apache.commons.i18n.ResourceBundleMessageProvider;
import org.marketcetera.util.misc.ClassVersion;

/**
 * An internationalized message provider, mapping instances of {@link
 * I18NMessage} onto text. The locale used for the translation is
 * either one supplied at the time of translation, or the active
 * locale per {@link ActiveLocale}; if the chosen locale does not
 * provide a message, the default JVM locale and root locale (in that
 * order) are used as fallbacks.
 * 
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$") //$NON-NLS-1$
public class I18NMessageProvider
{

    // CLASS DATA.

    /**
     * The string added to the end of the provider ID to obtain the
     * stream resource (file) containing the message mappings.
     */

    public static final String MESSAGE_FILE_EXTENSION=
        "_messages"; //$NON-NLS-1$
        // "_message"; // EXTREME TEST 1.

    /*
     * Hard-coded message text for messages used when the mapping file
     * cannot be used.
     */

    private static final String MESSAGE_FILE_NOT_FOUND=
        "Message file missing: provider '{}'; base name '{}'"; //$NON-NLS-1$
    private static final String MESSAGE_NOT_FOUND=
        "Message missing: provider ''{0}''; id ''{1}''; "+ //$NON-NLS-1$
        "entry ''{2}''; parameters {3}"; //$NON-NLS-1$
    private static final String UNEXPECTED_EXCEPTION_CONTEXT=
        "Abnormal exception: provider ''{0}''; id ''{1}''; "+ //$NON-NLS-1$
        "entry ''{2}''; parameters {3}"; //$NON-NLS-1$
    private static final String UNEXPECTED_EXCEPTION_TRACE=
        "Abnormal exception: stack trace"; //$NON-NLS-1$
    private static final String CORRUPTED_STORE=
        "Corrupted/unavailable message map"; //$NON-NLS-1$


    // INSTANCE DATA.

    private String mProviderId;


    // CONSTRUCTORS.

    /**
     * Creates a new message provider with the given ID. The provider
     * ID is combined with the suffix {@link #MESSAGE_FILE_EXTENSION}
     * to form the name of a mapping file. The file should be
     * retrievable as a resource, and its format should be that used
     * by Apache Commons i18n.
     *
     * @param providerId The provider ID.
     */

    public I18NMessageProvider
        (String providerId)
    {
        mProviderId=providerId;
        String baseName=getProviderId()+MESSAGE_FILE_EXTENSION;
        ResourceBundleMessageProvider provider;
        try {
            provider=new ResourceBundleMessageProvider(baseName);
        } catch (MessageNotFoundException ex) {
            SLF4JLoggerProxy.error
                (this,MESSAGE_FILE_NOT_FOUND,getProviderId(),baseName);
            SLF4JLoggerProxy.error(this,UNEXPECTED_EXCEPTION_TRACE,ex);
            return;
        }
        MessageManager.addMessageProvider(getProviderId(),provider);
    }


    // INSTANCE METHODS.

    /**
     * Returns the receiver's provider ID.
     *
     * @return The ID.
     */

    public String getProviderId()
    {
        return mProviderId;
    }

    /**
     * Returns the text of the given message in the given locale,
     * using the receiver's map. The given parameters are used to
     * instantiate parameterized messages.
     *
     * @param locale The locale.
     * @param message The message.
     * @param params The message parameters.
     *
     * @return Returns the text. If the message cannot be found, an
     * error is logged, and a simple form of the message comprising
     * its IDs and parameters is returned.
     */

    public String getText
        (Locale locale,
         I18NMessage message,
         Object... params)
    {
        String messageId=message.getMessageId();
        String entryId=message.getEntryId();
        try {
            //throw new IllegalArgumentException(); // EXTREME TEST 2.
            return MessageManager.getText
                (getProviderId(),messageId,entryId,params,locale);
        } catch (Exception ex) {

            // Handle mutually recursive call.

            if ((message==Messages.MESSAGE_NOT_FOUND) ||
                (message==Messages.UNEXPECTED_EXCEPTION)) {
                SLF4JLoggerProxy.error(this,CORRUPTED_STORE);
                if (message==Messages.MESSAGE_NOT_FOUND) {
                    return MessageFormat.format(MESSAGE_NOT_FOUND,params);
                }
                SLF4JLoggerProxy.error
                    (this,UNEXPECTED_EXCEPTION_TRACE,ex);
                return MessageFormat.format
                    (UNEXPECTED_EXCEPTION_CONTEXT,params);
            }

            // Turn arguments into a string.

            String paramsText=LogUtils.getListText(params);

            // Show exception: this may result in a mutually recursive
            // call into this exception handler via Messages.LOGGER if,
            // for example, the core message map is missing.

            if (ex instanceof MessageNotFoundException) {
                Messages.MESSAGE_NOT_FOUND.error
                    (this,ex,getProviderId(),messageId,entryId,paramsText);
            } else {
                Messages.UNEXPECTED_EXCEPTION.error
                    (this,ex,getProviderId(),messageId,entryId,paramsText);
                SLF4JLoggerProxy.error(this,UNEXPECTED_EXCEPTION_TRACE,ex);
            }

            // Return simple form of message.

            return LogUtils.getSimpleMessage(this,message,params);
        }
    }

    /**
     * Returns the text of the given message in the active locale per
     * {@link ActiveLocale}. The given parameters are used to
     * instantiate parameterized messages.
     *
     * @param message The message.
     * @param params The message parameters.
     *
     * @return Returns the text. If the message cannot be found, an
     * error is logged, and a simple form of the message comprising
     * its IDs and parameters is returned.
     */

    public String getText
        (I18NMessage message,
         Object... params)
    {
        return getText(ActiveLocale.getLocale(),message,params);
    }
}