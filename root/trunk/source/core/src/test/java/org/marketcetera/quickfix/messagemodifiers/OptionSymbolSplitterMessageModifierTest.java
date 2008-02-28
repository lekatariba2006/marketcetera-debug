package org.marketcetera.quickfix.messagemodifiers;

import junit.framework.TestCase;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.ExpectedTestFailure;
import org.marketcetera.core.MarketceteraException;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXMessageUtilTest;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.quickfix.messagefactory.NoOpFIXMessageAugmentor;
import quickfix.Message;
import quickfix.fix42.Logon;
import quickfix.field.SecurityType;
import quickfix.field.Side;
import quickfix.field.Symbol;

import java.math.BigDecimal;

/**
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$")
public class OptionSymbolSplitterMessageModifierTest extends TestCase {
    private FIXMessageFactory msgFactory = FIXVersion.FIX42.getMessageFactory();

    public void testPlainSymbol() throws Exception {
        Message msg = FIXMessageUtilTest.createNOS("ABC", new BigDecimal("23.33"), new BigDecimal("100"), Side.BUY, msgFactory);
        msg.setField(new SecurityType(SecurityType.OPTION));
        OptionSymbolSplitterMessageModifier mmod = new OptionSymbolSplitterMessageModifier();
        assertFalse(mmod.modifyMessage(msg, new NoOpFIXMessageAugmentor()));
        assertEquals("ABC", msg.getString(Symbol.FIELD));
    }

    public void testNotOptionOrder() throws Exception {
        Message msg = FIXMessageUtilTest.createNOS("ABC", new BigDecimal("23.33"), new BigDecimal("100"), Side.BUY, msgFactory);
        msg.setField(new SecurityType(SecurityType.COMMON_STOCK));
        OptionSymbolSplitterMessageModifier mmod = new OptionSymbolSplitterMessageModifier();
        assertFalse(mmod.modifyMessage(msg, new NoOpFIXMessageAugmentor()));
        assertEquals("ABC", msg.getString(Symbol.FIELD));
    }

    public void testCompositeSymbol() throws Exception {
        Message msg = FIXMessageUtilTest.createNOS("ABC+JE", new BigDecimal("23.33"), new BigDecimal("100"), Side.BUY, msgFactory);
        msg.setField(new SecurityType(SecurityType.OPTION));
        OptionSymbolSplitterMessageModifier mmod = new OptionSymbolSplitterMessageModifier();
        assertTrue(mmod.modifyMessage(msg, new NoOpFIXMessageAugmentor()));
        assertEquals("ABC", msg.getString(Symbol.FIELD));
    }

    public void testCompositeSymbol_NotOption() throws Exception {
        Message msg = FIXMessageUtilTest.createNOS("ABC+JE", new BigDecimal("23.33"), new BigDecimal("100"), Side.BUY, msgFactory);
        msg.setField(new SecurityType(SecurityType.COMMON_STOCK));
        OptionSymbolSplitterMessageModifier mmod = new OptionSymbolSplitterMessageModifier();
        assertFalse(mmod.modifyMessage(msg, new NoOpFIXMessageAugmentor()));
        assertEquals("ABC+JE", msg.getString(Symbol.FIELD));
    }

    public void testCompositeSymbol_WithRoute() throws Exception {
        Message msg = FIXMessageUtilTest.createNOS("IBM+IB.ARCA", new BigDecimal("23.33"), new BigDecimal("100"), Side.BUY, msgFactory);
        msg.setField(new SecurityType(SecurityType.OPTION));
        OptionSymbolSplitterMessageModifier mmod = new OptionSymbolSplitterMessageModifier();
        assertTrue(mmod.modifyMessage(msg, new NoOpFIXMessageAugmentor()));
        assertEquals("IBM.ARCA", msg.getString(Symbol.FIELD));
    }

    public void testNoSecurityType() throws Exception {
        new ExpectedTestFailure(MarketceteraException.class, ""+SecurityType.FIELD) {
            protected void execute() throws Throwable {
                Message msg = FIXMessageUtilTest.createNOS("ABC+JE", new BigDecimal("23.33"), new BigDecimal("100"), Side.BUY, msgFactory);
                OptionSymbolSplitterMessageModifier mmod = new OptionSymbolSplitterMessageModifier();
                mmod.modifyMessage(msg, new NoOpFIXMessageAugmentor());
            }
        }.run();
    }

    public void testNonNewOrderSingle() throws Exception {
        Logon logon =  new Logon();
        OptionSymbolSplitterMessageModifier mmod = new OptionSymbolSplitterMessageModifier();
        assertFalse("logon messaged shouldn't have been modified", mmod.modifyMessage(logon, new NoOpFIXMessageAugmentor()));
    }
}
