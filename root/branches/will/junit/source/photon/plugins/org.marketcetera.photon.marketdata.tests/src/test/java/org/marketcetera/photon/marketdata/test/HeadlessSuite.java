package org.marketcetera.photon.marketdata.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.marketcetera.photon.marketdata.MessagesTest;

/* $License$ */

/**
 * Test suite that can run as a regular JUnit suite.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
	MessagesTest.class
})
public class HeadlessSuite {

}
