package org.marketcetera.photon.commands;

import org.marketcetera.photon.IPhotonCommand;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.views.StockOrderTicketController;

import quickfix.Message;

public class ShowOrderInTicketCommand implements IPhotonCommand {

	Message order;
	
	public ShowOrderInTicketCommand(Message order) {
		super();
		this.order = order;
	}

	public void execute() {
		// todo: Make this work with options
		StockOrderTicketController controller = PhotonPlugin.getStockOrderTicketController();
		if(controller != null) {
			controller.showMessage(order);
		}
	}

}
