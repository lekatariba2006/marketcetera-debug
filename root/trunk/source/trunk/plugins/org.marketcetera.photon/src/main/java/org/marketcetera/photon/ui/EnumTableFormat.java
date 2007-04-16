package org.marketcetera.photon.ui;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.marketcetera.photon.FIXFieldLocalizer;
import org.marketcetera.photon.IFieldIdentifier;
import org.marketcetera.photon.core.MessageHolder;
import org.marketcetera.quickfix.FIXDataDictionaryManager;

import quickfix.DataDictionary;
import quickfix.FieldMap;
import quickfix.FieldNotFound;
import quickfix.FieldType;
import quickfix.Message;
import ca.odell.glazedlists.gui.TableFormat;

public class EnumTableFormat<T> implements TableFormat<T>, ITableLabelProvider
{
	Enum [] columns;
	private DataDictionary dataDictionary;
//	private Map<String, Integer> fieldMap = new HashMap<String, Integer>();
	
	private static final String COLUMN_WIDTH_SAVED_KEY_NAME = "width.saved";  //$NON-NLS-1$
	private static final DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	private static final int ORDERID_FIELDID = 11;  //agl todo:change figure out how to retrieve this id with quickfix

	
	public EnumTableFormat(Table table, Enum[] columns) {
		this.columns = columns;
		dataDictionary = FIXDataDictionaryManager.getCurrentFIXDataDictionary().getDictionary();
		int i = 0;
        for (Enum aColumn : columns) {
			int alignment;
			if (isNumericColumn(aColumn, dataDictionary)){
				alignment = SWT.RIGHT;
			} else {
				alignment = SWT.LEFT;
			}
			TableColumn tableColumn = new TableColumn(table, alignment);
			String localizedName = FIXFieldLocalizer.getLocalizedMessage(getColumnName(i++));
			tableColumn.setText(localizedName);
		}
	}

	private boolean isNumericColumn(Enum column, DataDictionary dict) {
		Class javaType;
		FieldType fieldTypeEnum;
		Integer fieldID;
		if (column instanceof IFieldIdentifier
				&& (fieldID = ((IFieldIdentifier)column).getFieldID()) != null
				&& (fieldTypeEnum = dict.getFieldTypeEnum(fieldID)) != null
				&& (javaType = fieldTypeEnum.getJavaType()) != null
				&& (Number.class.isAssignableFrom(javaType)
					|| Date.class.isAssignableFrom(javaType)
					|| Calendar.class.isAssignableFrom(javaType)))
		{
			return true;
		}
		return false;
	}

	public int getColumnCount() {
		return columns.length;
	}
	
	public String getColumnName(int index) {
		return columns[index].toString();
	}

	public Object getColumnValue(T element, int columnIndex) {
		Enum columnEnum = columns[columnIndex];
		if (columnEnum instanceof IFieldIdentifier)
		{
			Integer fieldID = ((IFieldIdentifier)columnEnum).getFieldID();
			Object value = null;
			if (fieldID != null) {
				FieldMap map = extractMap(element, fieldID);
				value = fieldValueFromMap(map, fieldID);
			}
			return value;
		} else {
			return null;
		}
	}

	protected Object fieldValueFromMap(FieldMap map, Integer fieldID) {
		Object value = null;
		if (map != null){
			try {
				FieldType fieldType = dataDictionary.getFieldTypeEnum(fieldID);
				if (fieldType.equals(FieldType.UtcTimeOnly)) {
					value = map.getUtcTimeOnly(fieldID);
				} else if (fieldType.equals(FieldType.UtcTimeStamp)){
					value = map.getUtcTimeStamp(fieldID);
				} else if (fieldType.equals(FieldType.UtcDateOnly)
						||fieldType.equals(FieldType.UtcDate)){
					value = map.getUtcDateOnly(fieldID);
				} else if (Number.class.isAssignableFrom(fieldType.getJavaType())){
					value = new BigDecimal(map.getString(fieldID));
				} else if (dataDictionary.hasFieldValue(fieldID)){
					value = FIXDataDictionaryManager.getCurrentFIXDataDictionary().getHumanFieldValue(fieldID, map.getString(fieldID));
				} else if (fieldID.intValue() == ORDERID_FIELDID) {
					value = new SortableOrderID(map.getString(fieldID));
				} else {
					value = map.getString(fieldID);
				}
			} catch (FieldNotFound e) {
			}
		}
		return value;
	}

	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		Enum columnEnum = columns[columnIndex];
		Integer fieldID;
		if (columnEnum instanceof IFieldIdentifier && 
				(fieldID = ((IFieldIdentifier)columnEnum).getFieldID()) != null){
			Object objValue = getColumnValue((T)element, columnIndex);
			String value = "";
			if (objValue != null){
				FieldType fieldType = dataDictionary.getFieldTypeEnum(fieldID);
				if (fieldType.equals(FieldType.UtcTimeOnly)
						|| fieldType.equals(FieldType.UtcTimeStamp)){
					value = TIME_FORMAT.format((Date)objValue);
				} else if (fieldType.equals(FieldType.UtcDateOnly)
						||fieldType.equals(FieldType.UtcDate)){
					value = DATE_FORMAT.format((Date)objValue);
				} else if (objValue instanceof BigDecimal){
					value  = ((BigDecimal)objValue).toPlainString();
				} else {
					value = objValue.toString();
				}
			}
			return value;
		} else {
			return null;
		}
	}


	private FieldMap extractMap(T element, Integer fieldID) {
		FieldMap map = null;
		Message message = null;
		if (element instanceof MessageHolder) {
			MessageHolder holder = (MessageHolder) element;
			message = holder.getMessage();
			map = getAppropriateMap(fieldID, message);
		} if (element instanceof Message) {
			message = (Message) element;
			map = getAppropriateMap(fieldID, message);
		} else if (element instanceof FieldMap){
			map = (FieldMap) element;
		}
		return map;
	}

	private FieldMap getAppropriateMap(Integer fieldID, Message message) {
		FieldMap map;
		if (dataDictionary.isHeaderField(fieldID)) {
			map = message.getHeader();
		} else if (dataDictionary.isTrailerField(fieldID)) {
			map = message.getTrailer();
		} else {
			map = message;
		}
		return map;
	}

	public void addListener(ILabelProviderListener listener) {
		FieldType fieldTypeEnum = dataDictionary.getFieldTypeEnum(1);
	}

	public void dispose() {
	}

	public boolean isLabelProperty(Object element, String property) {
		return true;
	}

	public void removeListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub
		
	}
	
	protected void hideColumn(TableColumn column) {
		// short of rebuilding the table, the only way to hide a table column with swt 3.2
		// is to set its width to 0 and make it non-resizable.
		
		column.setData(COLUMN_WIDTH_SAVED_KEY_NAME, column.getWidth());  // save the current width so that we could restore it when the column is shown again

		column.setResizable(false);
		column.setWidth(0);
	}

	protected void showColumn(TableColumn column) {
		column.setResizable(true);
		if (column.getData(COLUMN_WIDTH_SAVED_KEY_NAME) != null) {
			column.setWidth((Integer) column.getData(COLUMN_WIDTH_SAVED_KEY_NAME));
		}
	}

	protected boolean isColumnHidden(TableColumn column) {
		return column.getWidth() == 0 && !column.getResizable();
	}

}
