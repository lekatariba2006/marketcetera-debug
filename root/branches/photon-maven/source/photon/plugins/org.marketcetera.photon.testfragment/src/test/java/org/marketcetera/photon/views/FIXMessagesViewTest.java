package org.marketcetera.photon.views;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IViewPart;
import org.marketcetera.messagehistory.FIXMessageHistory;
import org.marketcetera.messagehistory.IncomingMessageHolder;
import org.marketcetera.photon.messagehistory.FIXRegexMatcher;
import org.marketcetera.photon.messagehistory.FIXStringMatcher;
import org.marketcetera.photon.ui.IndexedTableViewer;
import org.marketcetera.quickfix.FIXVersion;

import quickfix.Message;
import quickfix.field.AvgPx;
import quickfix.field.ClOrdID;
import quickfix.field.CumQty;
import quickfix.field.ExecID;
import quickfix.field.ExecTransType;
import quickfix.field.ExecType;
import quickfix.field.HandlInst;
import quickfix.field.LastPx;
import quickfix.field.LastQty;
import quickfix.field.LeavesQty;
import quickfix.field.MsgSeqNum;
import quickfix.field.MsgType;
import quickfix.field.OrdStatus;
import quickfix.field.OrdType;
import quickfix.field.OrderID;
import quickfix.field.OrderQty;
import quickfix.field.Price;
import quickfix.field.SenderCompID;
import quickfix.field.SendingTime;
import quickfix.field.Side;
import quickfix.field.Symbol;
import quickfix.field.TargetCompID;
import quickfix.field.TransactTime;
import quickfix.field.Urgency;
import quickfix.fix42.ExecutionReport;
import quickfix.fix42.Heartbeat;
import quickfix.fix42.NewOrderSingle;

public class FIXMessagesViewTest extends ViewTestBase {

	public FIXMessagesViewTest(String name) {
		super(name);
	}

	public void testShowMessage() throws Exception {
		FIXMessageHistory hist = new FIXMessageHistory(FIXVersion.FIX42.getMessageFactory());
		FIXMessagesView view = (FIXMessagesView) getTestView();
		view.setInput(hist);
		hist.addIncomingMessage(new ExecutionReport(
				new OrderID("orderid1"),
				new ExecID("execid1"),
				new ExecTransType(ExecTransType.STATUS),
				new ExecType(ExecType.PARTIAL_FILL),
				new OrdStatus(OrdStatus.PARTIALLY_FILLED),
				new Symbol("symbol1"),
				new Side(Side.BUY),
				new LeavesQty(1),
				new CumQty(2),
				new AvgPx(3)));
		delay(1);
		IndexedTableViewer tableViewer = view.getMessagesViewer();
		Table table = tableViewer.getTable();
		TableItem item = table.getItem(0);
		IncomingMessageHolder returnedMessageHolder = (IncomingMessageHolder) item.getData();
		Message message = returnedMessageHolder.getMessage();
		assertEquals("orderid1", message.getString(OrderID.FIELD));
	}
	
	public void testShowHeartbeats() throws Exception {
		FIXMessageHistory hist = new FIXMessageHistory(FIXVersion.FIX42.getMessageFactory());
		FIXMessagesView view = (FIXMessagesView) getTestView();
		view.setInput(hist);
		view.setShowHeartbeats(true);
		hist.addIncomingMessage(new Heartbeat());
		delay(1);
		IndexedTableViewer tableViewer = view.getMessagesViewer();
		Table table = tableViewer.getTable();
		TableItem item = table.getItem(0);
		IncomingMessageHolder returnedMessageHolder = (IncomingMessageHolder) item.getData();
		Message message = returnedMessageHolder.getMessage();
		assertEquals(MsgType.HEARTBEAT, message.getHeader().getString(MsgType.FIELD));
	}
	/**
     * Tests the filtering ability of the view.
     * 
     * @throws Exception
     */
    public void testFilter()
        throws Exception
    {
        doFilterTest();
    }
	@Override
	protected String getViewID() {
		return FIXMessagesView.ID;
	}
    /* (non-Javadoc)
     * @see org.marketcetera.photon.views.ViewTestBase#getFilterTestConditions()
     */
    @Override
    protected List<FilterTestCondition> getFilterTestConditions()
    {
        List<FilterTestCondition> conditions = new ArrayList<FilterTestCondition>();
        // string match on existing field
        conditions.add(new FilterTestCondition(new FIXStringMatcher(Side.FIELD,
                                                                    "B"),
                                               new int[] { 0, 1, 3 }));
        // string no match on existing field
        conditions.add(new FilterTestCondition(new FIXStringMatcher(Symbol.FIELD,
                                                                    "symbol-not-present"),
                                                                    new int[] { }));
        // string match on non-existent field
        conditions.add(new FilterTestCondition(new FIXStringMatcher(Urgency.FIELD,
                                                                    "0"),
                                               new int[] { }));
        // regex match on existing field
        conditions.add(new FilterTestCondition(new FIXRegexMatcher(Symbol.FIELD,
                                                                   "[a-z]*bol[1|2]"),
                                               new int[] { 0, 1, 3 } ));
        // regex match on non-existent field
        conditions.add(new FilterTestCondition(new FIXRegexMatcher(Symbol.FIELD,
                                                                   "[a-z]*bxol[1|2]"),
                                               new int[] { } ));
        // regex no match in non-existent field
        conditions.add(new FilterTestCondition(new FIXRegexMatcher(Urgency.FIELD,
                                                                   ".*"),
                                               new int[] { } ));
        conditions.add(new FilterTestCondition(new FIXStringMatcher(OrderID.FIELD,
                                                                    "orderid1"),
                                                                    new int[] { 0 } ));
        return conditions;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.photon.views.ViewTestBase#getFilterTestMessages()
     */
    @Override
    protected List<Message> getFilterTestMessages()
    {
        List<Message> messages = new ArrayList<Message>();
        ExecutionReport fill1 = new ExecutionReport(new OrderID("orderid1"),
                                                    new ExecID("execid1"),
                                                    new ExecTransType(ExecTransType.STATUS),
                                                    new ExecType(ExecType.PARTIAL_FILL),
                                                    new OrdStatus(OrdStatus.PARTIALLY_FILLED),
                                                    new Symbol("symbol1"),
                                                    new Side(Side.BUY),
                                                    new LeavesQty(4),
                                                    new CumQty(5),
                                                    new AvgPx(0));
        fill1.setField(new LastPx(1));
        fill1.setField(new LastQty(2));
        fill1.setField(new ClOrdID("orderid1"));
        fill1.setField(new MsgSeqNum(0));
        fill1.setField(new OrderQty(new BigDecimal(100)));
        fill1.setField(new Price(new BigDecimal(100)));
        fill1.setField(new SenderCompID("sender-comp"));
        fill1.setField(new SendingTime(new Date()));
        fill1.setField(new TargetCompID("target-comp"));
        fill1.setField(new TransactTime(new Date()));
        messages.add(fill1);
        ExecutionReport fill2 = new ExecutionReport(new OrderID("orderid2"),
                                                    new ExecID("execid2"),
                                                    new ExecTransType(ExecTransType.STATUS),
                                                    new ExecType(ExecType.PARTIAL_FILL),
                                                    new OrdStatus(OrdStatus.PARTIALLY_FILLED),
                                                    new Symbol("symbol2"),
                                                    new Side(Side.BUY),
                                                    new LeavesQty(4),
                                                    new CumQty(5),
                                                    new AvgPx(6));
        fill2.setField(new LastPx(1));
        fill2.setField(new LastQty(2));
        fill2.setField(new ClOrdID("orderid2"));
        fill2.setField(new MsgSeqNum(0));
        fill2.setField(new OrderQty(new BigDecimal(100)));
        fill2.setField(new Price(new BigDecimal(100)));
        fill2.setField(new SenderCompID("sender-comp"));
        fill2.setField(new SendingTime(new Date()));
        fill2.setField(new TargetCompID("target-comp"));
        fill2.setField(new TransactTime(new Date()));
        messages.add(fill2);
        Heartbeat hb1 = new Heartbeat();
        messages.add(hb1);
        NewOrderSingle order1 = new NewOrderSingle(new ClOrdID("clordid1"),
                                                   new HandlInst(HandlInst.AUTOMATED_EXECUTION_ORDER_PRIVATE),
                                                   new Symbol("symbol1"),
                                                   new Side(Side.BUY),
                                                   new TransactTime(new Date()),
                                                   new OrdType(OrdType.MARKET));
        order1.set(new OrderQty(100));
        messages.add(order1);
        Heartbeat hb2 = new Heartbeat();
        messages.add(hb2);
        
        return messages;
    }

    /* (non-Javadoc)
     * @see org.marketcetera.photon.views.ViewTestBase#getTestView()
     */
    @Override
    public IViewPart getTestView()
    {
        FIXMessagesView view = (FIXMessagesView)super.getTestView();
        view.setShowHeartbeats(true);
        return view;
    }
}
