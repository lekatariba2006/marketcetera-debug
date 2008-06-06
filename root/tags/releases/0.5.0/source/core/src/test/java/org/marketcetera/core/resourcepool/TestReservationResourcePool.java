package org.marketcetera.core.resourcepool;

import java.util.Iterator;


public class TestReservationResourcePool
        extends ReservationResourcePool
{
    private boolean mRenderThrows = false;
    private boolean mRenderReturnsNull = false;
    private boolean mCreateResourceReturnsNull = false;
    private boolean mCreateResourceThrows = false;
    private boolean mAddResourceThrows = false;
    private boolean mResourceContentionThrows = false;
    private boolean mReleaseResourceThrows = false;
    private TestResource mLastResourceCreated = null;
    
    protected TestResource createResource(Object inData)
            throws ResourcePoolException
    {
        setLastResourceCreated(null);
        if(getCreateResourceReturnsNull()) {
            return null;
        }
        if(getCreateResourceThrows()) {
            throw new NullPointerException("This exception is expected");
        }
        ReservationData data = null;
        if(inData == null) {
            data = new ReservationData("user_" + System.nanoTime(),
                                       "password_" + System.nanoTime());
        } else {
            data = (ReservationData)inData;
        }
        TestResource resource = new TestResource(data.getUser(),
                                                 data.getPassword());
        setLastResourceCreated(resource);
        return resource;
    }

    protected ReservationData renderReservationKey(Resource inResource)
    {
        if(getRenderThrows()) {
            throw new NullPointerException("This exception is expected");
        }
        if(getRenderReturnsNull()) {
            return null;
        }
        TestResource r = (TestResource) inResource;
        return new ReservationData(r.getUser(),
                                   r.getPassword());
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return "TestReservationResourcePool(" + hashCode() + ")";
    }

    static class ReservationData
    {
        private String mUser;
        private String mPassword;
        
        ReservationData(String inUser,
                        String inPassword)
        {
            setUser(inUser);
            setPassword(inPassword);
        }

        /**
         * @return the password
         */
        String getPassword()
        {
            return mPassword;
        }

        /**
         * @param inPassword the password to set
         */
        void setPassword(String inPassword)
        {
            mPassword = inPassword;
        }

        /**
         * @return the user
         */
        String getUser()
        {
            return mUser;
        }

        /**
         * @param inUser the user to set
         */
        void setUser(String inUser)
        {
            mUser = inUser;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        public int hashCode()
        {
            final int PRIME = 31;
            int result = 1;
            result = PRIME * result + ((mPassword == null) ? 0 : mPassword.hashCode());
            result = PRIME * result + ((mUser == null) ? 0 : mUser.hashCode());
            return result;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        public boolean equals(Object obj)
        {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            final ReservationData other = (ReservationData) obj;
            if (mPassword == null) {
                if (other.mPassword != null)
                    return false;
            } else if (!mPassword.equals(other.mPassword))
                return false;
            if (mUser == null) {
                if (other.mUser != null)
                    return false;
            } else if (!mUser.equals(other.mUser))
                return false;
            return true;
        }
        
        public String toString()
        {
            return "ReservationData(" + hashCode() + ") " + getUser() + "/" + getPassword();
        }
    }
    
    int getPoolSize()
    {
        synchronized(getPoolLock()) {
            int counter = 0;
            Iterator<Resource> iterator = getPoolIterator();
            while(iterator.hasNext()) {
                iterator.next();
                counter += 1;
            }
            return counter;
        }        
    }

    /**
     * @return the renderThrows
     */
    boolean getRenderThrows()
    {
        return mRenderThrows;
    }

    /**
     * @param inRenderThrows the renderThrows to set
     */
    void setRenderThrows(boolean inRenderThrows)
    {
        mRenderThrows = inRenderThrows;
    }

    /**
     * @return the renderReturnsNull
     */
    boolean getRenderReturnsNull()
    {
        return mRenderReturnsNull;
    }

    /**
     * @param inRenderReturnsNull the renderReturnsNull to set
     */
    void setRenderReturnsNull(boolean inRenderReturnsNull)
    {
        mRenderReturnsNull = inRenderReturnsNull;
    }

    /* (non-Javadoc)
     * @see org.marketcetera.core.resourcepool.ReservationResourcePool#getNextResource(java.lang.Object)
     */
    protected TestResource getNextResource(Object inData)
            throws ResourcePoolException
    {
        return (TestResource)super.getNextResource(inData);
    }

    /**
     * @return the createResourceReturnsNull
     */
    boolean getCreateResourceReturnsNull()
    {
        return mCreateResourceReturnsNull;
    }

    /**
     * @param inCreateResourceReturnsNull the createResourceReturnsNull to set
     */
    void setCreateResourceReturnsNull(boolean inCreateResourceReturnsNull)
    {
        mCreateResourceReturnsNull = inCreateResourceReturnsNull;
    }

    /**
     * @return the createResourceThrows
     */
    boolean getCreateResourceThrows()
    {
        return mCreateResourceThrows;
    }

    /**
     * @param inCreateResourceThrows the createResourceThrows to set
     */
    void setCreateResourceThrows(boolean inCreateResourceThrows)
    {
        mCreateResourceThrows = inCreateResourceThrows;
    }

    /**
     * @return the addResourceThrows
     */
    boolean getAddResourceThrows()
    {
        return mAddResourceThrows;
    }

    /**
     * @param inAddResourceThrows the addResourceThrows to set
     */
    void setAddResourceThrows(boolean inAddResourceThrows)
    {
        mAddResourceThrows = inAddResourceThrows;
    }

    /* (non-Javadoc)
     * @see org.marketcetera.core.resourcepool.ReservationResourcePool#addResourceToPool(org.marketcetera.core.resourcepool.Resource)
     */
    protected void addResourceToPool(Resource inResource)
    {
        if(getAddResourceThrows()) {
            throw new NullPointerException("This exception is expected");
        }
        super.addResourceToPool(inResource);
    }

    /**
     * @return the lastResourceCreated
     */
    TestResource getLastResourceCreated()
    {
        return mLastResourceCreated;
    }

    /**
     * @param inLastResourceCreated the lastResourceCreated to set
     */
    void setLastResourceCreated(TestResource inLastResourceCreated)
    {
        mLastResourceCreated = inLastResourceCreated;
    }

    /* (non-Javadoc)
     * @see org.marketcetera.core.resourcepool.ReservationResourcePool#allocateResource(org.marketcetera.core.resourcepool.Resource)
     */
    protected TestResource allocateResource(Resource inResource)
    {
        return (TestResource)super.allocateResource(inResource);
    }

    /* (non-Javadoc)
     * @see org.marketcetera.core.resourcepool.ReservationResourcePool#requestResource(java.lang.Object)
     */
    protected TestResource requestResource(Object inData)
            throws ResourcePoolException
    {
        return (TestResource)super.requestResource(inData);
    }

    /* (non-Javadoc)
     * @see org.marketcetera.core.resourcepool.ReservationResourcePool#resourceContention(org.marketcetera.core.resourcepool.ReservationResourcePool.ReservationEntry)
     */
    protected TestResource resourceContention(ReservationEntry inReservationEntry)
            throws ResourcePoolException
    {
        TestResource r = (TestResource)inReservationEntry.getResource();
        r.setContentionStamp(System.currentTimeMillis());
        if(getResourceContentionThrows()) {
            throw new NullPointerException("This exception is expected");
        }
        return (TestResource)super.resourceContention(inReservationEntry);
    }

    /**
     * @return the resourceContentionThrows
     */
    boolean getResourceContentionThrows()
    {
        return mResourceContentionThrows;
    }

    /**
     * @param inResourceContentionThrows the resourceContentionThrows to set
     */
    void setResourceContentionThrows(boolean inResourceContentionThrows)
    {
        mResourceContentionThrows = inResourceContentionThrows;
    }

    /* (non-Javadoc)
     * @see org.marketcetera.core.resourcepool.ReservationResourcePool#releaseResource(org.marketcetera.core.resourcepool.Resource)
     */
    protected void releaseResource(Resource inResource)
            throws ReleasedResourceException
    {
        if(getReleaseResourceThrows()) {
            throw new NullPointerException("This exception is expected");
        }
        super.releaseResource(inResource);
    }

    /**
     * @return the releaseResourceThrows
     */
    boolean getReleaseResourceThrows()
    {
        return mReleaseResourceThrows;
    }

    /**
     * @param inReleaseResourceThrows the releaseResourceThrows to set
     */
    void setReleaseResourceThrows(boolean inReleaseResourceThrows)
    {
        mReleaseResourceThrows = inReleaseResourceThrows;
    }
}
