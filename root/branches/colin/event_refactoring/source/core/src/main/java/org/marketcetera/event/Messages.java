package org.marketcetera.event;

import org.marketcetera.util.log.I18NLoggerProxy;
import org.marketcetera.util.log.I18NMessage0P;
import org.marketcetera.util.log.I18NMessage1P;
import org.marketcetera.util.log.I18NMessage4P;
import org.marketcetera.util.log.I18NMessageProvider;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * The internationalization constants used by this package.
 *
 * @author klim@marketcetera.com
 * @since 0.6.0
 * @version $Id$
 */
@ClassVersion("$Id$")
public interface Messages
{
    /**
     * The message provider.
     */

    static final I18NMessageProvider PROVIDER = 
        new I18NMessageProvider("event"); //$NON-NLS-1$

    /**
     * The logger.
     */

    static final I18NLoggerProxy LOGGER = 
        new I18NLoggerProxy(PROVIDER);

    /*
     * The messages.
     */

    static final I18NMessage0P ERROR_MSG_NOT_EXEC_REPORT = 
        new I18NMessage0P(LOGGER,"error_msg_not_exec_report"); //$NON-NLS-1$
    static final I18NMessage4P INVALID_EVENT_TIMESTAMP = new I18NMessage4P(LOGGER,
                                                                           "invalid_event_timestamp"); //$NON-NLS-1$
    static final I18NMessage0P VALIDATION_NULL_AMOUNT = new I18NMessage0P(LOGGER,
                                                                          "validation_null_amount"); //$NON-NLS-1$
    static final I18NMessage0P VALIDATION_NULL_CURRENCY = new I18NMessage0P(LOGGER,
                                                                            "validation_null_currency"); //$NON-NLS-1$
    static final I18NMessage0P VALIDATION_NULL_DECLARE_DATE = new I18NMessage0P(LOGGER,
                                                                                "validation_null_declare_date"); //$NON-NLS-1$
    static final I18NMessage1P VALIDATION_FORMAT_DECLARE_DATE = new I18NMessage1P(LOGGER,
                                                                                  "validation_format_declare_date"); //$NON-NLS-1$
    static final I18NMessage0P VALIDATION_NULL_EXECUTION_DATE = new I18NMessage0P(LOGGER,
                                                                                  "validation_null_execution_date"); //$NON-NLS-1$
    static final I18NMessage1P VALIDATION_FORMAT_EXECUTION_DATE = new I18NMessage1P(LOGGER,
                                                                                    "validation_format_execution_date"); //$NON-NLS-1$
    static final I18NMessage0P VALIDATION_NULL_PAYMENT_DATE = new I18NMessage0P(LOGGER,
                                                                                "validation_null_payment_date"); //$NON-NLS-1$
    static final I18NMessage1P VALIDATION_FORMAT_PAYMENT_DATE = new I18NMessage1P(LOGGER,
                                                                                  "validation_format_payment_date"); //$NON-NLS-1$
    static final I18NMessage0P VALIDATION_NULL_RECORD_DATE = new I18NMessage0P(LOGGER,
                                                                               "validation_null_record_date"); //$NON-NLS-1$
    static final I18NMessage1P VALIDATION_FORMAT_RECORD_DATE = new I18NMessage1P(LOGGER,
                                                                                 "validation_format_record_date"); //$NON-NLS-1$
    static final I18NMessage0P VALIDATION_NULL_FREQUENCY = new I18NMessage0P(LOGGER,
                                                                             "validation_null_frequency"); //$NON-NLS-1$
    static final I18NMessage0P VALIDATION_NULL_STATUS = new I18NMessage0P(LOGGER,
                                                                          "validation_null_status"); //$NON-NLS-1$
    static final I18NMessage0P VALIDATION_NULL_TYPE = new I18NMessage0P(LOGGER,
                                                                        "validation_null_type"); //$NON-NLS-1$
}
