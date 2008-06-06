package org.marketcetera.ors.mbeans;

import junit.framework.Test;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.FIXVersionTestSuite;
import org.marketcetera.core.FIXVersionedTestCase;
import org.marketcetera.core.InMemoryIDFactory;
import org.marketcetera.ors.mbeans.ORSAdmin;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.quickfix.NullQuickFIXSender;
import quickfix.Message;
import quickfix.field.MsgType;
import quickfix.field.NewPassword;
import quickfix.field.Password;

/**
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$")
public class ORSAdminTest extends FIXVersionedTestCase {
    public ORSAdminTest(String inName, FIXVersion version) {
        super(inName, version);
    }

    public static Test suite() {
        return new FIXVersionTestSuite(ORSAdminTest.class, FIXVersionTestSuite.ALL_VERSIONS);
    }

    public void testSendPasswordReset() throws Exception {
        NullQuickFIXSender qfSender = new NullQuickFIXSender();
        ORSAdmin admin = new ORSAdmin(qfSender, msgFactory, new InMemoryIDFactory(100));
        admin.sendPasswordReset("sender", "target", "old", "new");
        assertEquals(1, qfSender.getCapturedMessages().size());
        Message msg = qfSender.getCapturedMessages().get(0);
        assertEquals(MsgType.USER_REQUEST, msg.getHeader().getString(MsgType.FIELD));
        assertEquals("old", msg.getString(Password.FIELD));
        assertEquals("new", msg.getString(NewPassword.FIELD));
    }
}
