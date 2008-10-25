package org.marketcetera.bogusfeed;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.marketcetera.marketdata.AbstractMarketDataFeedTest;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.marketdata.bogus.BogusFeedModule;
import org.marketcetera.marketdata.bogus.BogusFeedModuleFactory;
import org.marketcetera.module.DataFlowID;
import org.marketcetera.module.DataRequest;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.module.IllegalRequestParameterValue;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.module.ModuleTestBase;
import org.marketcetera.module.SinkDataListener;
import org.marketcetera.module.UnsupportedRequestParameterType;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id:$
 * @since $Release$
 */
public class BogusFeedEmitterTest
    extends ModuleTestBase
{
    private BogusFeedModuleFactory factory;
    private ModuleManager moduleManager;
    private DataSink dataSink;
    @Before
    public void setup()
        throws Exception
    {
        moduleManager = new ModuleManager();
        moduleManager.init();
        dataSink = new DataSink();
        moduleManager.addSinkListener(dataSink);
        factory = new BogusFeedModuleFactory();
    }
    @After
    public void cleanup()
        throws Exception
    {
        moduleManager.stop();
        moduleManager = null;
    }
    @Test
    public void construction()
        throws Exception
    {
        BogusFeedModule emitter = factory.create(new Object[0]);
        assertNotNull(emitter);
        assertNotNull(emitter = factory.create(this));
        assertNotNull(emitter = factory.create((Object[])null));
    }
    @Test
    public void badDataRequests()
        throws Exception
    {
        // null request data
        new ExpectedFailure<IllegalRequestParameterValue>(org.marketcetera.module.Messages.ILLEGAL_REQ_PARM_VALUE,
                                                          BogusFeedModuleFactory.INSTANCE_URN.toString(),
                                                          null) {
            protected void run()
                throws Exception
            {
                moduleManager.createDataFlow(new DataRequest[] { new DataRequest(BogusFeedModuleFactory.INSTANCE_URN,
                                                                                 null) });
            }
        };
        // inappropriate datatype
        final Object invalidParam = new Object();
        new ExpectedFailure<UnsupportedRequestParameterType>(org.marketcetera.module.Messages.UNSUPPORTED_REQ_PARM_TYPE,
                                                             BogusFeedModuleFactory.INSTANCE_URN.toString(),
                                                             invalidParam.getClass().getName()) {
            protected void run()
                throws Exception
            {
                moduleManager.createDataFlow(new DataRequest[] { new DataRequest(BogusFeedModuleFactory.INSTANCE_URN,
                                                                                 invalidParam) });
            }
        };
        // String, but not from a data request
        final String invalidString = "There is no way you can make a data request from this, so there";
        new ExpectedFailure<IllegalRequestParameterValue>(org.marketcetera.module.Messages.ILLEGAL_REQ_PARM_VALUE,
                                                          BogusFeedModuleFactory.INSTANCE_URN.toString(),
                                                          invalidString) {
            protected void run()
                throws Exception
            {
                moduleManager.createDataFlow(new DataRequest[] { new DataRequest(BogusFeedModuleFactory.INSTANCE_URN,
                                                                                 invalidString) });
            }
        };
    }
    @Ignore
    @Test
    public void dataRequestFromString()
        throws Exception
    {
        assertTrue(moduleManager.getDataFlows(true).isEmpty());
        org.marketcetera.marketdata.DataRequest request = MarketDataRequest.newFullBookRequest("GOOG");
        final DataFlowID flowID = moduleManager.createDataFlow(new DataRequest[] { new DataRequest(BogusFeedModuleFactory.INSTANCE_URN,
                                                                                                   request.toString()) });
        // wait until some arbitrary number of ticks have been received
        AbstractMarketDataFeedTest.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                return dataSink.getData(flowID).size() > 20;
            }});
        // cancel the flow
        moduleManager.cancel(flowID);
    }
    @Test
    public void dataRequestProducesData()
        throws Exception
    {
        assertTrue(moduleManager.getDataFlows(true).isEmpty());
        org.marketcetera.marketdata.DataRequest request = MarketDataRequest.newFullBookRequest("GOOG");
        final DataFlowID flowID = moduleManager.createDataFlow(new DataRequest[] { new DataRequest(BogusFeedModuleFactory.INSTANCE_URN,
                                                                                                   request) });
        // wait until some arbitrary number of ticks have been received
        AbstractMarketDataFeedTest.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                return dataSink.getData(flowID).size() > 20;
            }});
        // cancel the flow
        moduleManager.cancel(flowID);
        
    }
    public static class DataSink
        implements SinkDataListener
    {
        private final Map<DataFlowID,List<Object>> data = new HashMap<DataFlowID,List<Object>>(); 
        /* (non-Javadoc)
         * @see org.marketcetera.module.SinkDataListener#receivedData(org.marketcetera.module.DataFlowID, java.lang.Object)
         */
        @Override
        public void receivedData(DataFlowID inDataFlowID,
                                 Object inData)
        {
            synchronized(data) {
                List<Object> dataForID = data.get(inDataFlowID);
                if(dataForID == null) {
                    dataForID = new ArrayList<Object>();
                    data.put(inDataFlowID,
                             dataForID);
                }
                dataForID.add(inData);
            }
        }
        public List<Object> getData(DataFlowID inDataFlowID)
        {
            synchronized(data) {
                List<Object> result = data.get(inDataFlowID);
                if(result == null) {
                    return new ArrayList<Object>();
                }
                return new ArrayList<Object>(result);
            }
        }
    }
}
