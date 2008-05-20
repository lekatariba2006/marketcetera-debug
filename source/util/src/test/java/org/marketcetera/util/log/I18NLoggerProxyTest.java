package org.marketcetera.util.log;

import java.util.Locale;
import org.apache.log4j.Level;
import org.junit.Test;
import org.marketcetera.util.test.TestCaseBase;

import static org.junit.Assert.*;

public class I18NLoggerProxyTest
	extends TestCaseBase
{
    private static final String TEST_CATEGORY=
        I18NLoggerProxyTest.class.getName();
    private static final String TEST_MSG_EN=
        "Test here (expected): 'a'";
    private static final String TEST_MSG_FR=
        "Test voil\u00E0 (attendu): 'a'";
    private static final String TEST_MSG_EN_NULL=
        "Test here (expected): 'null'";
    private static final String TEST_MSG_FR_NULL=
        "Test voil\u00E0 (attendu): 'null'";
    private static final String TEST_MSG_EN_NOSUB=
        "Test here (expected): ''{0}''";
    private static final String TEST_MSG_FR_NOSUB=
        "Test voil\u00E0 (attendu): ''{0}''";
    private static final Exception TEST_THROWABLE=
        new IllegalArgumentException("Test exception (expected)");


    private void messageCheck
        (Locale locale,
         String msg,
         String msgNull,
         String msgNoSub)
    {
        Messages.PROVIDER.setLocale(locale);
        setLevel(TEST_CATEGORY,Level.OFF);

        TestMessages.LOGGER.error
            (TEST_CATEGORY,TEST_THROWABLE);
        TestMessages.LOGGER.error
            (TEST_CATEGORY,TEST_THROWABLE,TestMessages.LOG_MSG,"a");
        TestMessages.LOGGER.error
            (TEST_CATEGORY,TestMessages.LOG_MSG,"a");
        assertNoEvents();

        setLevel(TEST_CATEGORY,Level.ERROR);
        TestMessages.LOGGER.error
            (TEST_CATEGORY,TEST_THROWABLE);
        assertSingleEvent
            (Level.ERROR,TEST_CATEGORY,SLF4JLoggerProxy.UNKNOWN_MESSAGE);
        TestMessages.LOGGER.error
            (TEST_CATEGORY,TEST_THROWABLE,TestMessages.LOG_MSG,"a");
        assertSingleEvent
            (Level.ERROR,TEST_CATEGORY,msg);
        TestMessages.LOGGER.error(TEST_CATEGORY,TestMessages.LOG_MSG,"a");
        assertSingleEvent
            (Level.ERROR,TEST_CATEGORY,msg);

        TestMessages.LOGGER.error
            (TEST_CATEGORY,TEST_THROWABLE,TestMessages.LOG_MSG,(Object[])null);
        assertSingleEvent
            (Level.ERROR,TEST_CATEGORY,msgNoSub);
        TestMessages.LOGGER.error
            (TEST_CATEGORY,TestMessages.LOG_MSG,(Object[])null);
        assertSingleEvent
            (Level.ERROR,TEST_CATEGORY,msgNoSub);

        TestMessages.LOGGER.error
            (TEST_CATEGORY,TEST_THROWABLE,TestMessages.LOG_MSG,(Object)null);
        assertSingleEvent
            (Level.ERROR,TEST_CATEGORY,msgNull);
        TestMessages.LOGGER.error
            (TEST_CATEGORY,TestMessages.LOG_MSG,(Object)null);
        assertSingleEvent
            (Level.ERROR,TEST_CATEGORY,msgNull);

        TestMessages.LOGGER.warn
            (TEST_CATEGORY,TEST_THROWABLE);
        TestMessages.LOGGER.warn
            (TEST_CATEGORY,TEST_THROWABLE,TestMessages.LOG_MSG,"a");
        TestMessages.LOGGER.warn
            (TEST_CATEGORY,TestMessages.LOG_MSG,"a");
        assertNoEvents();

        setLevel(TEST_CATEGORY,Level.WARN);
        TestMessages.LOGGER.warn
            (TEST_CATEGORY,TEST_THROWABLE);
        assertSingleEvent
            (Level.WARN,TEST_CATEGORY,SLF4JLoggerProxy.UNKNOWN_MESSAGE);
        TestMessages.LOGGER.warn
            (TEST_CATEGORY,TEST_THROWABLE,TestMessages.LOG_MSG,"a");
        assertSingleEvent
            (Level.WARN,TEST_CATEGORY,msg);
        TestMessages.LOGGER.warn(TEST_CATEGORY,TestMessages.LOG_MSG,"a");
        assertSingleEvent
            (Level.WARN,TEST_CATEGORY,msg);

        TestMessages.LOGGER.warn
            (TEST_CATEGORY,TEST_THROWABLE,TestMessages.LOG_MSG,(Object[])null);
        assertSingleEvent
            (Level.WARN,TEST_CATEGORY,msgNoSub);
        TestMessages.LOGGER.warn
            (TEST_CATEGORY,TestMessages.LOG_MSG,(Object[])null);
        assertSingleEvent
            (Level.WARN,TEST_CATEGORY,msgNoSub);

        TestMessages.LOGGER.warn
            (TEST_CATEGORY,TEST_THROWABLE,TestMessages.LOG_MSG,(Object)null);
        assertSingleEvent
            (Level.WARN,TEST_CATEGORY,msgNull);
        TestMessages.LOGGER.warn
            (TEST_CATEGORY,TestMessages.LOG_MSG,(Object)null);
        assertSingleEvent
            (Level.WARN,TEST_CATEGORY,msgNull);

        TestMessages.LOGGER.info
            (TEST_CATEGORY,TEST_THROWABLE);
        TestMessages.LOGGER.info
            (TEST_CATEGORY,TEST_THROWABLE,TestMessages.LOG_MSG,"a");
        TestMessages.LOGGER.info
            (TEST_CATEGORY,TestMessages.LOG_MSG,"a");
        assertNoEvents();

        setLevel(TEST_CATEGORY,Level.INFO);
        TestMessages.LOGGER.info
            (TEST_CATEGORY,TEST_THROWABLE);
        assertSingleEvent
            (Level.INFO,TEST_CATEGORY,SLF4JLoggerProxy.UNKNOWN_MESSAGE);
        TestMessages.LOGGER.info
            (TEST_CATEGORY,TEST_THROWABLE,TestMessages.LOG_MSG,"a");
        assertSingleEvent
            (Level.INFO,TEST_CATEGORY,msg);
        TestMessages.LOGGER.info(TEST_CATEGORY,TestMessages.LOG_MSG,"a");
        assertSingleEvent
            (Level.INFO,TEST_CATEGORY,msg);

        TestMessages.LOGGER.info
            (TEST_CATEGORY,TEST_THROWABLE,TestMessages.LOG_MSG,(Object[])null);
        assertSingleEvent
            (Level.INFO,TEST_CATEGORY,msgNoSub);
        TestMessages.LOGGER.info
            (TEST_CATEGORY,TestMessages.LOG_MSG,(Object[])null);
        assertSingleEvent
            (Level.INFO,TEST_CATEGORY,msgNoSub);

        TestMessages.LOGGER.info
            (TEST_CATEGORY,TEST_THROWABLE,TestMessages.LOG_MSG,(Object)null);
        assertSingleEvent
            (Level.INFO,TEST_CATEGORY,msgNull);
        TestMessages.LOGGER.info
            (TEST_CATEGORY,TestMessages.LOG_MSG,(Object)null);
        assertSingleEvent
            (Level.INFO,TEST_CATEGORY,msgNull);

        TestMessages.LOGGER.debug
            (TEST_CATEGORY,TEST_THROWABLE);
        TestMessages.LOGGER.debug
            (TEST_CATEGORY,TEST_THROWABLE,TestMessages.LOG_MSG,"a");
        TestMessages.LOGGER.debug
            (TEST_CATEGORY,TestMessages.LOG_MSG,"a");
        assertNoEvents();

        setLevel(TEST_CATEGORY,Level.DEBUG);
        TestMessages.LOGGER.debug
            (TEST_CATEGORY,TEST_THROWABLE);
        assertSingleEvent
            (Level.DEBUG,TEST_CATEGORY,SLF4JLoggerProxy.UNKNOWN_MESSAGE);
        TestMessages.LOGGER.debug
            (TEST_CATEGORY,TEST_THROWABLE,TestMessages.LOG_MSG,"a");
        assertSingleEvent
            (Level.DEBUG,TEST_CATEGORY,msg);
        TestMessages.LOGGER.debug(TEST_CATEGORY,TestMessages.LOG_MSG,"a");
        assertSingleEvent
            (Level.DEBUG,TEST_CATEGORY,msg);

        TestMessages.LOGGER.debug
            (TEST_CATEGORY,TEST_THROWABLE,TestMessages.LOG_MSG,(Object[])null);
        assertSingleEvent
            (Level.DEBUG,TEST_CATEGORY,msgNoSub);
        TestMessages.LOGGER.debug
            (TEST_CATEGORY,TestMessages.LOG_MSG,(Object[])null);
        assertSingleEvent
            (Level.DEBUG,TEST_CATEGORY,msgNoSub);

        TestMessages.LOGGER.debug
            (TEST_CATEGORY,TEST_THROWABLE,TestMessages.LOG_MSG,(Object)null);
        assertSingleEvent
            (Level.DEBUG,TEST_CATEGORY,msgNull);
        TestMessages.LOGGER.debug
            (TEST_CATEGORY,TestMessages.LOG_MSG,(Object)null);
        assertSingleEvent
            (Level.DEBUG,TEST_CATEGORY,msgNull);

        TestMessages.LOGGER.trace
            (TEST_CATEGORY,TEST_THROWABLE);
        TestMessages.LOGGER.trace
            (TEST_CATEGORY,TEST_THROWABLE,TestMessages.LOG_MSG,"a");
        TestMessages.LOGGER.trace
            (TEST_CATEGORY,TestMessages.LOG_MSG,"a");
        assertNoEvents();

        setLevel(TEST_CATEGORY,Level.TRACE);
        TestMessages.LOGGER.trace
            (TEST_CATEGORY,TEST_THROWABLE);
        assertSingleEvent
            (Level.TRACE,TEST_CATEGORY,SLF4JLoggerProxy.UNKNOWN_MESSAGE);
        TestMessages.LOGGER.trace
            (TEST_CATEGORY,TEST_THROWABLE,TestMessages.LOG_MSG,"a");
        assertSingleEvent
            (Level.TRACE,TEST_CATEGORY,msg);
        TestMessages.LOGGER.trace(TEST_CATEGORY,TestMessages.LOG_MSG,"a");
        assertSingleEvent
            (Level.TRACE,TEST_CATEGORY,msg);

        TestMessages.LOGGER.trace
            (TEST_CATEGORY,TEST_THROWABLE,TestMessages.LOG_MSG,(Object[])null);
        assertSingleEvent
            (Level.TRACE,TEST_CATEGORY,msgNoSub);
        TestMessages.LOGGER.trace
            (TEST_CATEGORY,TestMessages.LOG_MSG,(Object[])null);
        assertSingleEvent
            (Level.TRACE,TEST_CATEGORY,msgNoSub);

        TestMessages.LOGGER.trace
            (TEST_CATEGORY,TEST_THROWABLE,TestMessages.LOG_MSG,(Object)null);
        assertSingleEvent
            (Level.TRACE,TEST_CATEGORY,msgNull);
        TestMessages.LOGGER.trace
            (TEST_CATEGORY,TestMessages.LOG_MSG,(Object)null);
        assertSingleEvent
            (Level.TRACE,TEST_CATEGORY,msgNull);
    }


    @Test
    public void providerIsValid()
    {
        assertEquals
            (TestMessages.PROVIDER,TestMessages.LOGGER.getMessageProvider());
    }


    @Test
    public void messages()
    {
        messageCheck
            (Locale.US,TEST_MSG_EN,TEST_MSG_EN_NULL,TEST_MSG_EN_NOSUB);
        messageCheck
            (Locale.FRENCH,TEST_MSG_FR,TEST_MSG_FR_NULL,TEST_MSG_FR_NOSUB);
    }
}