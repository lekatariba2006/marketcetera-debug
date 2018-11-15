package org.marketcetera.admin.impl;

import java.util.Set;

import org.marketcetera.ors.security.SimpleUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;


/* $License$ */

/**
 * Provides datastore access to {@link PersistentSupervisorPermission} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface PersistentSupervisorPermissionDao
        extends JpaRepository<PersistentSupervisorPermission, Long>,QueryDslPredicateExecutor<PersistentSupervisorPermission>
{
    /**
     * Find the supervisor permission with the given name.
     *
     * @param inName a <code>String</code> value
     * @return a <code>PersistentSupervisorPermission</code> value
     */
    PersistentSupervisorPermission findByName(String inName);
    /**
     * 
     *
     *
     * @param inSupervisor
     * @return
     */
    Set<PersistentSupervisorPermission> findBySupervisor(SimpleUser inSupervisor);
}
