package org.marketcetera.util.ws.stateful;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.stateless.StatelessClientContext;

/**
 * An authenticator of user credentials.
 * 
 * @author tlerios@marketcetera.com
 * @since $Release$
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$") //$NON-NLS-1$
public interface Authenticator
{

    /**
     * Checks whether the given credentials can be used to initiate a
     * new session on behalf of the client with the given context.
     *
     * @param context The context.
     * @param user The user name.
     * @param password The password.
     *
     * @return True if the given credentials are acceptable.
     */

    boolean shouldAllow
        (StatelessClientContext context,
         String user,
         char[] password);
}
