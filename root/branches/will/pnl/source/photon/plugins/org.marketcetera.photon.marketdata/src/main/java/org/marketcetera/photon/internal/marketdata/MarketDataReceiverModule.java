package org.marketcetera.photon.internal.marketdata;

import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.marketdata.MarketDataRequestException;
import org.marketcetera.marketdata.MarketDataRequest.Content;
import org.marketcetera.module.DataFlowID;
import org.marketcetera.module.DataFlowRequester;
import org.marketcetera.module.DataFlowSupport;
import org.marketcetera.module.DataReceiver;
import org.marketcetera.module.DataRequest;
import org.marketcetera.module.Module;
import org.marketcetera.module.ModuleCreationException;
import org.marketcetera.module.ModuleException;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.module.StopDataFlowException;
import org.marketcetera.module.UnsupportedDataTypeException;
import org.marketcetera.photon.internal.marketdata.MarketDataReceiverFactory.IConfigurationProvider;
import org.marketcetera.photon.marketdata.MarketDataSubscriber;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.misc.ClassVersion;

/**
 * Endpoint for market data flows.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
class MarketDataReceiverModule extends Module implements DataReceiver,
		DataFlowRequester {

	private final IConfigurationProvider mConfigurationProvider;

	private final MarketDataSubscriber mSubscriber;

	private DataFlowSupport mDataFlowSupport;

	/**
	 * Constructor.  Should only be called from {@link MarketDataReceiverFactory}.
	 * 
	 * @param inURN
	 *            instance URN for this module
	 * @param configProvider
	 *            data flow configuration provider
	 * @param subscriber
	 *            subscriber that will process incoming market data
	 */
	MarketDataReceiverModule(ModuleURN inURN,
			IConfigurationProvider configProvider,
			MarketDataSubscriber subscriber) throws ModuleCreationException {
		super(inURN, false);
		if (configProvider == null)
			throw new ModuleCreationException(Messages.MARKET_DATA_RECEIVER_NO_CONFIG);
		if (subscriber == null)
			throw new ModuleCreationException(Messages.MARKET_DATA_RECEIVER_NO_SUBSCRIBER);
		mConfigurationProvider = configProvider;
		mSubscriber = subscriber;
	}

	@Override
	protected void preStart() throws ModuleException {
		if (mSubscriber.getSymbols().length == 0) {
			throw new ModuleException(Messages.MARKET_DATA_RECEIVER_NO_SYMBOL);
		}
		ModuleURN source = mConfigurationProvider.getMarketDataSourceModule();
		if (source == null) {
			throw new ModuleException(new I18NBoundMessage1P(
					Messages.MARKET_DATA_RECEIVER_NO_SOURCE, mSubscriber
							.getSymbols()));
		}
		MarketDataRequest request = null;
        try {
            request = MarketDataRequest.newRequest().withSymbols(mSubscriber.getSymbols()).withContent(Content.TOP_OF_BOOK).fromProvider(source.instanceName());
            mDataFlowSupport.createDataFlow(new DataRequest[] {
                    new DataRequest(source, request),
                    new DataRequest(getURN()) }, false);
        } catch (MarketDataRequestException e) {
            throw new ModuleException(e,
                                      new I18NBoundMessage1P(Messages.MARKET_DATA_RECEIVER_REQUEST_FAILED,
                                                             request));
        }
	}

	@Override
	protected void preStop() throws ModuleException {

	}

	@Override
	public void receiveData(DataFlowID inFlowID, final Object inData)
			throws UnsupportedDataTypeException, StopDataFlowException {
		mSubscriber.receiveData(inData);
	}

	@Override
	public void setFlowSupport(DataFlowSupport inSupport) {
		mDataFlowSupport = inSupport;
	}

}