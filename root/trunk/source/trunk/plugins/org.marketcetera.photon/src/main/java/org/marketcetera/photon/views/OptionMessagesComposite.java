package org.marketcetera.photon.views;

import java.text.ParseException;
import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IWorkbenchPartSite;
import org.marketcetera.core.MSymbol;
import org.marketcetera.photon.EclipseUtils;
import org.marketcetera.photon.IFieldIdentifier;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.marketdata.MarketDataFeedService;
import org.marketcetera.photon.marketdata.MarketDataFeedTracker;
import org.marketcetera.photon.marketdata.OptionInfoComponent;
import org.marketcetera.photon.marketdata.OptionMarketDataUtils;
import org.marketcetera.photon.marketdata.OptionMessageHolder;
import org.marketcetera.photon.marketdata.OptionMessageHolder.OptionPairKey;
import org.marketcetera.photon.ui.EnumTableFormat;
import org.marketcetera.photon.ui.EventListContentProvider;
import org.marketcetera.photon.ui.IndexedTableViewer;
import org.marketcetera.photon.ui.OptionMessageListTableFormat;
import org.marketcetera.photon.ui.TableComparatorChooser;
import org.marketcetera.quickfix.FIXDataDictionary;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.quickfix.FIXVersion;

import quickfix.DataDictionary;
import quickfix.FieldMap;
import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.MDEntryPx;
import quickfix.field.MDEntrySize;
import quickfix.field.MDEntryType;
import quickfix.field.MaturityMonthYear;
import quickfix.field.NoMDEntries;
import quickfix.field.NoRelatedSym;
import quickfix.field.StrikePrice;
import quickfix.field.Symbol;
import quickfix.fix44.DerivativeSecurityList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.SortedList;

/**
 * @author caroline.leung@softwaregoodness.com
 * @author michael.lossos@softwaregoodness.com
 */
public class OptionMessagesComposite extends Composite {

	public enum OptionDataColumns implements IFieldIdentifier {
		ZEROWIDTH(""), 
		CVOL("cVol", OptionInfoComponent.CALL_EXTRA_INFO, MDEntrySize.FIELD, NoMDEntries.FIELD, MDEntryType.FIELD, MDEntryType.TRADE_VOLUME), 
		CBIDSZ("cBidSz", OptionInfoComponent.CALL_MARKET_DATA, MDEntrySize.FIELD, NoMDEntries.FIELD, MDEntryType.FIELD, MDEntryType.BID), 
		CBID("cBid", OptionInfoComponent.CALL_MARKET_DATA, MDEntryPx.FIELD, NoMDEntries.FIELD, MDEntryType.FIELD, MDEntryType.BID),
		CASK("cAsk", OptionInfoComponent.CALL_MARKET_DATA, MDEntryPx.FIELD, NoMDEntries.FIELD, MDEntryType.FIELD, MDEntryType.OFFER),
		CASKSZ("cAskSz", OptionInfoComponent.CALL_MARKET_DATA, MDEntrySize.FIELD, NoMDEntries.FIELD, MDEntryType.FIELD, MDEntryType.OFFER),
		CSYM("cSym", OptionInfoComponent.CALL_EXTRA_INFO, Symbol.FIELD, null, null, null),
		STRIKE("Strike", OptionInfoComponent.STRIKE_INFO, StrikePrice.FIELD, null, null, null),
		EXP("Exp", OptionInfoComponent.STRIKE_INFO, MaturityMonthYear.FIELD, null, null, null),
		PSYM("pSym", OptionInfoComponent.PUT_EXTRA_INFO, Symbol.FIELD, null, null, null),
		PBIDSZ("pBidSz", OptionInfoComponent.PUT_MARKET_DATA, MDEntrySize.FIELD, NoMDEntries.FIELD, MDEntryType.FIELD, MDEntryType.BID), 
		PBID("pBid", OptionInfoComponent.PUT_MARKET_DATA, MDEntryPx.FIELD, NoMDEntries.FIELD, MDEntryType.FIELD, MDEntryType.BID),
		PASK("pAsk", OptionInfoComponent.PUT_MARKET_DATA, MDEntryPx.FIELD, NoMDEntries.FIELD, MDEntryType.FIELD, MDEntryType.OFFER),
		PASKSZ("pAskSz", OptionInfoComponent.PUT_MARKET_DATA, MDEntrySize.FIELD, NoMDEntries.FIELD, MDEntryType.FIELD, MDEntryType.OFFER),
		PVOL("pVol", OptionInfoComponent.PUT_EXTRA_INFO, MDEntrySize.FIELD, NoMDEntries.FIELD, MDEntryType.FIELD, MDEntryType.TRADE_VOLUME);


		private String name;
		private Integer fieldID;
		private Integer groupID;
		private Integer groupDiscriminatorID;
		private Object groupDiscriminatorValue;
		private OptionInfoComponent component;

		OptionDataColumns(String name){
			this.name = name;
		}

		OptionDataColumns(String name, OptionInfoComponent component, Integer fieldID, Integer groupID, Integer groupDiscriminatorID, Object groupDiscriminatorValue){
			this.name=name;
			this.component = component;
			this.fieldID = fieldID;
			this.groupID = groupID;
			this.groupDiscriminatorID = groupDiscriminatorID;
			this.groupDiscriminatorValue = groupDiscriminatorValue;
		}

		public String toString() {
			return name;
		}

		public Integer getFieldID() {
			return fieldID;
		}
		
		public Integer getGroupID() {
			return groupID;
		}

		public Integer getGroupDiscriminatorID() {
			return groupDiscriminatorID;
		}

		public Object getGroupDiscriminatorValue() {
			return groupDiscriminatorValue;
		}

		public OptionInfoComponent getComponent() {
			return component;
		}
	}
	
	public static final String COLUMN_ORDER_KEY = "COLUMN_ORDER";  //$NON-NLS-1$
	public static final String COLUMN_ORDER_DELIMITER = ",";  //$NON-NLS-1$
	public static final String SORT_BY_COLUMN_KEY = "SORT_BY_COLUMN";  //$NON-NLS-1$

	public static final int FIRST_PUT_DATA_COLUMN_INDEX = 9;
	private static final int ZERO_WIDTH_COLUMN_INDEX = 0;
	private static final int LAST_NORMAL_COLUMN_INDEX = ZERO_WIDTH_COLUMN_INDEX;

	
	private static final int CALL_VOLUME_INDEX = LAST_NORMAL_COLUMN_INDEX + 1;
	private static final int CALL_BID_SIZE_INDEX = LAST_NORMAL_COLUMN_INDEX + 2;
	private static final int CALL_BID_PRICE_INDEX = LAST_NORMAL_COLUMN_INDEX + 3;
	private static final int CALL_ASK_PRICE_INDEX = LAST_NORMAL_COLUMN_INDEX + 4;
	private static final int CALL_ASK_SIZE_INDEX = LAST_NORMAL_COLUMN_INDEX + 5;
	private static final int CALL_SYMBOL_INDEX = LAST_NORMAL_COLUMN_INDEX + 6;
	public static final int STRIKE_INDEX = LAST_NORMAL_COLUMN_INDEX + 7;
	public static final int EXP_DATE_INDEX = LAST_NORMAL_COLUMN_INDEX + 8;
	private static final int PUT_SYMBOL_INDEX = LAST_NORMAL_COLUMN_INDEX + 9;
	private static final int PUT_BID_SIZE_INDEX = LAST_NORMAL_COLUMN_INDEX + 10;
	private static final int PUT_BID_PRICE_INDEX = LAST_NORMAL_COLUMN_INDEX + 11;
	private static final int PUT_ASK_PRICE_INDEX = LAST_NORMAL_COLUMN_INDEX + 12;
	private static final int PUT_ASK_SIZE_INDEX = LAST_NORMAL_COLUMN_INDEX + 13;
	private static final int PUT_VOLUME_INDEX = LAST_NORMAL_COLUMN_INDEX + 14;

	

	private Label errorLabel;
	private Table messageTable;
	private IndexedTableViewer messagesViewer;
	private EnumTableFormat<OptionMessageHolder> tableFormat;
	private TableComparatorChooser<OptionMessageHolder> chooser;
	private EventList<OptionMessageHolder> rawInputList;
	private final boolean sortableColumns;
	
	private final IWorkbenchPartSite site;
	private IMemento viewStateMemento; 

	private HashMap<OptionPairKey, OptionMessageHolder> optionContractMap;
	private HashMap<String, OptionPairKey> optionSymbolToKeyMap;
	private FIXMessageFactory messageFactory = FIXVersion.FIX44.getMessageFactory();
	
	public OptionMessagesComposite(Composite parent, IWorkbenchPartSite site, IMemento memento) {
		this(parent, site, memento, true);
	}
	
    public OptionMessagesComposite(Composite parent, IWorkbenchPartSite site, IMemento memento, boolean sortableColumns) {
    	super(parent, SWT.MULTI | SWT.FULL_SELECTION | SWT.VIRTUAL);
    	this.sortableColumns = sortableColumns;   	
    	this.site = site;
    	this.viewStateMemento = memento;
    	createErrorLabel(this);
		createTable(this);
		this.setLayout(createBasicGridLayout(1));
		initializeDataMaps();
    }

	private GridLayout createBasicGridLayout(int numColumns) {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = numColumns;
		gridLayout.marginWidth = 2;
		gridLayout.verticalSpacing = 0;
		gridLayout.horizontalSpacing = 0;
		gridLayout.marginHeight = 0;
		return gridLayout;
	}

	private void initializeDataMaps() {
		optionSymbolToKeyMap = new HashMap<String, OptionPairKey>();
//		optionSymbolToSideMap = new HashMap<String, Boolean>();
		optionContractMap = new HashMap<OptionPairKey, OptionMessageHolder>();
	}

	protected void formatTable(Table messageTable) {
        messageTable.getVerticalBar().setEnabled(true);
        messageTable.setForeground(
        		messageTable.getDisplay().getSystemColor(
						SWT.COLOR_INFO_FOREGROUND));

        messageTable.setHeaderVisible(true);

		for (int i = 0; i < messageTable.getColumnCount(); i++) {
			messageTable.getColumn(i).setMoveable(true);
		}
    }

	private void createTable(Composite parent) {
        messageTable = createMessageTable(parent);
		messagesViewer = createTableViewer(messageTable, getEnumValues());
		tableFormat = (EnumTableFormat<OptionMessageHolder>)messagesViewer.getLabelProvider();
		formatTable(messageTable);
		packColumns(messageTable);
		restoreColumnOrder(viewStateMemento);		
	}
	
	private void createErrorLabel(Composite parent) {
		errorLabel = new Label(parent, SWT.WRAP);
		errorLabel.setForeground(getDisplay().getSystemColor(SWT.COLOR_RED));
		errorLabel.setEnabled(false);
        GridData labelGridData = new GridData();
        labelGridData.horizontalSpan = 1;
        labelGridData.verticalSpan = 1;
        labelGridData.horizontalAlignment = GridData.BEGINNING;
        labelGridData.verticalAlignment = GridData.END;
        labelGridData.grabExcessHorizontalSpace = true;
        errorLabel.setLayoutData(labelGridData);

	}
	
	private void disableErrorLabelText() {
		errorLabel.setText("");
		errorLabel.pack();
		errorLabel.setEnabled(false);
	}
	
	private void enableErrorLabelText(String text) {
		errorLabel.setText(text);
		errorLabel.pack();
		errorLabel.setEnabled(true);
	}
		
	protected Enum[] getEnumValues() {
		return OptionDataColumns.values();
	}
	
	// cl todo: duplicated code from MarketDataView, need to refactor
	public void packColumns(final Table table) {
		TableColumn zeroFirstColumn = table.getColumn(ZERO_WIDTH_COLUMN_INDEX);
		zeroFirstColumn.setWidth(0);
		zeroFirstColumn.setResizable(false);
		zeroFirstColumn.setMoveable(false);
		zeroFirstColumn.setText("");
		zeroFirstColumn.setImage(null);
		for (int i = 1; i < table.getColumnCount(); i++) {
			table.getColumn(i).setWidth(getTableColumnWidth(i));
		}
	}

	private int getColumnWidth(int width) {
		return EclipseUtils.getTextAreaSize(messageTable, null, width, 1.0).x;

	}
	private int getTableColumnWidth(int index) {
		switch (index) {
		case CALL_VOLUME_INDEX:
			return getColumnWidth(10);
		case CALL_BID_SIZE_INDEX:
			return getColumnWidth(10);
		case CALL_BID_PRICE_INDEX:
			return getColumnWidth(10);
		case CALL_ASK_PRICE_INDEX:
			return getColumnWidth(10);
		case CALL_ASK_SIZE_INDEX:
			return getColumnWidth(10);
		case CALL_SYMBOL_INDEX:
			return getColumnWidth(12);
		case STRIKE_INDEX:
			return getColumnWidth(10);
		case EXP_DATE_INDEX:
			return getColumnWidth(12);
		case PUT_SYMBOL_INDEX:
			return getColumnWidth(12);
		case PUT_BID_SIZE_INDEX:
			return getColumnWidth(10);
		case PUT_BID_PRICE_INDEX:
			return getColumnWidth(10);
		case PUT_ASK_PRICE_INDEX:
			return getColumnWidth(10);
		case PUT_ASK_SIZE_INDEX:
			return getColumnWidth(10);
		case PUT_VOLUME_INDEX:
			return getColumnWidth(10);
		default:
			return getColumnWidth(10);
		}
	}
 
	public void saveTableState(IMemento memento) {		
		saveColumnOrder(memento);
	}

	protected String serializeColumnOrder(int[] columnOrder) {
		StringBuilder sb = new StringBuilder();
		for(int columnNumber : columnOrder) {
			sb.append(columnNumber);
			sb.append(COLUMN_ORDER_DELIMITER);
		}
		return sb.toString();
	}
	
	protected int[] deserializeColumnOrder(String delimitedValue) {
		if (delimitedValue == null) {
			return new int[0];
		}
		String[] columnNumbers = delimitedValue.split(COLUMN_ORDER_DELIMITER);
		if (columnNumbers == null || columnNumbers.length == 0) {
			return new int[0];
		}
		int[] columnOrder = new int[columnNumbers.length];
		for(int index = 0; index < columnOrder.length; ++index)  {
			try {
				columnOrder[index] = Integer.parseInt(columnNumbers[index]);
			}
			catch(Exception anyException) {
				// TODO Log?
				// org.marketcetera.photon.PhotonPlugin.getMainConsoleLogger().warn("Failed to load column order.", anyException);
				return new int[0];
			}
		}
		return columnOrder;
	}
	
	private void saveColumnOrder(IMemento memento) {
		if (memento == null) 
			return;
		int[] columnOrder = messageTable.getColumnOrder();
		String serializedColumnOrder = serializeColumnOrder(columnOrder);
		memento.putString(COLUMN_ORDER_KEY, serializedColumnOrder);
	}
	
	private void restoreColumnOrder(IMemento memento) {
		try {
			if (memento == null)
				return;
			String delimitedColumnOrder = memento.getString(COLUMN_ORDER_KEY);
			int[] columnOrder = deserializeColumnOrder(delimitedColumnOrder);
			if(columnOrder != null && columnOrder.length > 0) {
				messageTable.setColumnOrder(columnOrder);
			}
		} catch (Throwable t){
			// do nothing
		}
	}
	
    protected Table createMessageTable(Composite parent) {
        Table messageTable = new Table(parent, SWT.MULTI | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.BORDER);
        GridData messageTableLayout = new GridData();
        messageTableLayout.horizontalSpan = 2;
        messageTableLayout.verticalSpan = 1;
        messageTableLayout.horizontalAlignment = GridData.FILL;
        messageTableLayout.verticalAlignment = GridData.FILL;
        messageTableLayout.grabExcessHorizontalSpace = true;
        messageTableLayout.grabExcessVerticalSpace = true;
        messageTable.setLayoutData(messageTableLayout);
        return messageTable;
    }
    	    
	protected IndexedTableViewer createTableViewer(Table aMessageTable,
			Enum[] enums) {
		IndexedTableViewer aMessagesViewer = new IndexedTableViewer(
				aMessageTable);
		getSite().setSelectionProvider(aMessagesViewer);
		aMessagesViewer
				.setContentProvider(new EventListContentProvider<OptionMessageHolder>());
		FIXDataDictionary dictionary = PhotonPlugin.getDefault().getFIXDataDictionary();
		aMessagesViewer.setLabelProvider(
				new OptionMessageListTableFormat(
						aMessageTable, OptionDataColumns.values(), getSite(), dictionary.getDictionary()));

		return aMessagesViewer;
	}

	public IndexedTableViewer getMessagesViewer() {
		return messagesViewer;
	}
	
	public void setInput(EventList<OptionMessageHolder> input)
	{
		SortedList<OptionMessageHolder> extractedList = 
			new SortedList<OptionMessageHolder>(rawInputList = input);

		if (sortableColumns){
			if (chooser != null){
				chooser.dispose();
				chooser = null;
			}
			chooser = new TableComparatorChooser<OptionMessageHolder>(
								messageTable, 
								tableFormat,
								extractedList, true);
			chooser.disableSortOnColumnHeader();
			
			//Sort by Expiration date first, then by Strike 
			chooser.appendComparator(EXP_DATE_INDEX, 0, false);
			chooser.appendComparator(STRIKE_INDEX, 0, false);
		}
		messagesViewer.setInput(extractedList);
	}
	
	public EventList<OptionMessageHolder> getInput()
	{
		return rawInputList;
	}

	protected IWorkbenchPartSite getSite() {
		return site;
	}


	public Table getMessageTable() {
		return messageTable;
	}
	
	protected void clearDataMaps() {
		optionContractMap.clear();
		optionSymbolToKeyMap.clear();
//		optionSymbolToSideMap.clear();
	}

	public void unlistenAllMarketData(MarketDataFeedTracker marketDataTracker) {
		getInput().clear();
		clearDataMaps();
		getMessagesViewer().refresh();		
	}
	
	@Override
	public void dispose() {
		clearDataMaps();
		super.dispose();
	}
	
	
	private boolean showAllOptions(String filteredByOptionSymbol) {
		return filteredByOptionSymbol == null;
	}
	

	public void handleDerivativeSecuritiyList(Message derivativeSecurityList, MarketDataFeedTracker marketDataTracker){
		MarketDataFeedService feed = marketDataTracker.getMarketDataFeedService();
		if (FIXMessageUtil.isDerivativeSecurityList(derivativeSecurityList) && feed != null){
			try {
				int numDerivs = derivativeSecurityList.getInt(NoRelatedSym.FIELD);
				EventList<OptionMessageHolder> list = getInput();
				for (int i = 1; i <= numDerivs; i++)
				{
					try {
						DerivativeSecurityList.NoRelatedSym info = new DerivativeSecurityList.NoRelatedSym();
						derivativeSecurityList.getGroup(i, info);
	
						int putOrCall = OptionMarketDataUtils.getOptionType(info);
						MSymbol optionSymbol = feed.symbolFromString(info.getString(Symbol.FIELD));
						OptionPairKey optionKey;
							optionKey = OptionPairKey.fromFieldMap(optionSymbol, info);
						optionSymbolToKeyMap.put(optionSymbol.getFullSymbol(), optionKey);
						OptionMessageHolder holder;
						if (optionContractMap.containsKey(optionKey)){
							holder = optionContractMap.get(optionKey);
						} else {
							holder = new OptionMessageHolder(optionSymbol, info);
							optionContractMap.put(optionKey, holder);
							list.add(holder);
						}
						
						holder.setExtraInfo(putOrCall, info);
					} catch (ParseException e) {
						PhotonPlugin.getDefault().getMarketDataLogger().error("Exception parsing option info", e);
					}
				}
				getMessagesViewer().refresh();
			} catch (FieldNotFound e) {
				PhotonPlugin.getDefault().getMarketDataLogger().error("Exception parsing option info", e);
			}
		}
	}
	
	public void handleQuote(Message marketDataRefresh){
		String symbol;
		try {
			symbol = marketDataRefresh.getString(Symbol.FIELD);
			OptionPairKey key = optionSymbolToKeyMap.get(symbol);
			if (key != null){
				OptionMessageHolder line = optionContractMap.get(key);
				FieldMap existingMessage = line.getMarketDataForSymbol(symbol);
				if (FIXMessageUtil.isMarketDataIncrementalRefresh(marketDataRefresh)
						&& existingMessage != null){
					FIXMessageUtil.mergeMarketDataMessages(marketDataRefresh, 
							(Message)existingMessage, messageFactory);
				} else {
					existingMessage = marketDataRefresh;
				}
				int putOrCall = line.symbolOptionType(symbol);
				line.setMarketData(putOrCall, existingMessage);
		
				getMessagesViewer().update(line, null);
			} else {
				PhotonPlugin.getDefault().getMarketDataLogger().info("Uknown symbol: "+symbol);
			}
		} catch (FieldNotFound e) {
			PhotonPlugin.getDefault().getMarketDataLogger().info("Symbol missing from option quote.");
		}
	}
	
	
			
	



	protected HashMap<OptionPairKey, OptionMessageHolder> getOptionContractMap() {
		return optionContractMap;
	}


	protected HashMap<String, OptionPairKey> getOptionSymbolToKeyMap() {
		return optionSymbolToKeyMap;
	}


//	protected HashMap<String, Boolean> getOptionSymbolToSideMap() {
//		return optionSymbolToSideMap;
//	}
	
	
	
}
