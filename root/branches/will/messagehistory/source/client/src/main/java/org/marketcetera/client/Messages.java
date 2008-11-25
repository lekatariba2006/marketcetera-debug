package org.marketcetera.client;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.log.*;

/* $License$ */
/**
 * Internationalized messages used by this package.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public interface Messages {
    /**
     * The message provider
     */
    static final I18NMessageProvider PROVIDER =
            new I18NMessageProvider("client",  //$NON-NLS-1$ 
                    Messages.class.getClassLoader());
    /**
     * The message logger.
     */
    static final I18NLoggerProxy LOGGER =
            new I18NLoggerProxy(PROVIDER);
    static final I18NMessage0P PROVIDER_DESCRIPTION =
            new I18NMessage0P(LOGGER, "provider_description");   //$NON-NLS-1$
    static final I18NMessage2P UNSUPPORTED_DATA_TYPE =
            new I18NMessage2P(LOGGER, "unsupported_data_type");   //$NON-NLS-1$
    static final I18NMessage1P SEND_ORDER_FAIL_NO_CONNECT =
            new I18NMessage1P(LOGGER, "send_order_fail_no_connect");   //$NON-NLS-1$
    static final I18NMessage2P ERROR_CONNECT_TO_SERVER =
            new I18NMessage2P(LOGGER, "error_connect_to_server");   //$NON-NLS-1$
    static final I18NMessage0P CREATE_MODULE_ERROR =
            new I18NMessage0P(LOGGER, "create_module_error");   //$NON-NLS-1$
    static final I18NMessage0P CLIENT_ALREADY_INITIALIZED =
            new I18NMessage0P(LOGGER, "client_already_initialized");   //$NON-NLS-1$
    static final I18NMessage0P CLIENT_NOT_INITIALIZED =
            new I18NMessage0P(LOGGER, "client_not_initialized");   //$NON-NLS-1$
    static final I18NMessage1P UNEXPECTED_MESSAGE_TO_SEND =
            new I18NMessage1P(LOGGER, "unexpected_message_to_send");   //$NON-NLS-1$
    static final I18NMessage1P UNEXPECTED_MESSAGE_RECEIVED =
            new I18NMessage1P(LOGGER, "unexpected_message_received");   //$NON-NLS-1$
    static final I18NMessage1P ERROR_SEND_MESSAGE =
            new I18NMessage1P(LOGGER, "error_send_message");   //$NON-NLS-1$
    static final I18NMessage0P CONNECT_ERROR_NO_URL =
            new I18NMessage0P(LOGGER, "connect_error_no_url");   //$NON-NLS-1$
    static final I18NMessage0P CONNECT_ERROR_NO_USERNAME =
            new I18NMessage0P(LOGGER, "connect_error_no_username");   //$NON-NLS-1$
    static final I18NMessage0P NOT_CONNECTED_TO_SERVER =
            new I18NMessage0P(LOGGER, "not_connected_to_server");   //$NON-NLS-1$
    static final I18NMessage0P ERROR_RECEIVING_JMS_MESSAGE =
            new I18NMessage0P(LOGGER, "error_receiving_jms_message");   //$NON-NLS-1$
    static final I18NMessage0P REQUEST_PARAMETER_SPECIFIED =
            new I18NMessage0P(LOGGER, "request_parameter_specified");   //$NON-NLS-1$
    static final I18NMessage0P REQUEST_CLIENT_NOT_INITIALIZED =
            new I18NMessage0P(LOGGER, "request_client_not_initialized");   //$NON-NLS-1$
    static final I18NMessage0P CLIENT_CLOSED =
            new I18NMessage0P(LOGGER, "client_closed");   //$NON-NLS-1$


    static final I18NMessage1P LOG_ERROR_RECEIVE_EXEC_REPORT =
            new I18NMessage1P(LOGGER, "log_error_receive_exec_report");   //$NON-NLS-1$
    static final I18NMessage1P LOG_ERROR_RECEIVE_CANCEL_REJECT =
            new I18NMessage1P(LOGGER, "log_error_receive_cancel_reject");   //$NON-NLS-1$
    static final I18NMessage1P LOG_ERROR_NOTIFY_EXCEPTION =
            new I18NMessage1P(LOGGER, "log_error_notify_exception");   //$NON-NLS-1$
    static final I18NMessage1P LOG_ERROR_SEND_EXCEPTION =
            new I18NMessage1P(LOGGER, "log_error_send_exception");   //$NON-NLS-1$
    static final I18NMessage1P LOG_CLIENT_NOT_INIT_CANCEL_REQUEST =
            new I18NMessage1P(LOGGER, "log_client_not_init_cancel_request");   //$NON-NLS-1$


}