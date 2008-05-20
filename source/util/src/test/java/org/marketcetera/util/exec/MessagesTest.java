package org.marketcetera.util.exec;

import java.util.Locale;
import org.junit.Test;
import org.marketcetera.util.test.TestCaseBase;

import static org.junit.Assert.*;

public class MessagesTest
	extends TestCaseBase
{
    @Test
    public void messagesExist()
    {
        Messages.PROVIDER.setLocale(Locale.US);
        assertEquals
            ("Cannot copy output of command 'a'",
             Messages.CANNOT_COPY_OUTPUT.getText("a"));
        assertEquals
            ("Cannot execute command 'a'",
             Messages.CANNOT_EXECUTE.getText("a"));
        assertEquals
            ("Unexpected termination of command 'a'",
             Messages.UNEXPECTED_TERMINATION.getText("a"));
    }
}