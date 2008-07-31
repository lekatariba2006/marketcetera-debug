package org.marketcetera.ors;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.ConfigFileLoadingException;
import org.marketcetera.ors.OrderRoutingSystem;
import org.marketcetera.quickfix.FIXDataDictionary;
import org.marketcetera.quickfix.FIXDataDictionaryManager;
import org.marketcetera.persist.PersistTestBase;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import quickfix.field.Symbol;

/**
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$") //$NON-NLS-1$
public class ORSStartupTest extends TestCase {

    private boolean failed = false;
    private Exception failureEx;

    public ORSStartupTest(String inName) {
        super(inName);
    }

    public static Test suite() {
        return new TestSuite(ORSStartupTest.class);
    }

    /** test the startup of the real ORS appContext, sleeps for 10 secs and exits
     * Really, we just care to check that the spring config is setup correctly, nothing else.
     */
    public void testRealORSStartup() throws Exception {
        failed = false;
        final Thread thread = new Thread(new Runnable() {
            public void run() {
                try {
                    OrderRoutingSystem.main(new String[0]);
                } catch (ConfigFileLoadingException e) {
                    failed = true;
                    failureEx = e;
                }
            }
        });
        thread.start();
        //Wait for ORS to start.
        while(thread.isAlive() && (OrderRoutingSystem.getInstance() == null ||
                (!OrderRoutingSystem.getInstance().isWaitingForever()))) {
            Thread.sleep(10000);
        }

        assertFalse("failure in ORS startup: " + failureEx, failed); //$NON-NLS-1$

        // call through to the FIXDataDictionary in a way that doesn't explicitly load the dictionary
        assertNotNull("fix dictionary not initialized", //$NON-NLS-1$
                FIXDataDictionaryManager.getCurrentFIXDataDictionary().getHumanFieldName(Symbol.FIELD));
        assertEquals("wrong fix version: " + FIXDataDictionaryManager.getCurrentFIXDataDictionary().getDictionary().getVersion(), //$NON-NLS-1$
                FIXDataDictionary.FIX_4_2_BEGIN_STRING,
                FIXDataDictionaryManager.getCurrentFIXDataDictionary().getDictionary().getVersion());
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        //Initialize the schema and create the admin user
        //Close the spring context so that ORS can startup
        PersistTestBase.springSetup(new String[]{
                "ors_initdb_create_admin_vendor.xml", "ors_db.xml"}, //$NON-NLS-1$ //$NON-NLS-2$
                new FileSystemXmlApplicationContext(
                        OrderRoutingSystem.CFG_BASE_FILE_NAME)).close();
    }
}
