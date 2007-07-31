package org.marketcetera.photon.commands;

import org.apache.log4j.Logger;
import org.marketcetera.core.NoMoreIDsException;
import org.marketcetera.photon.IPhotonCommand;
import org.marketcetera.photon.PhotonPlugin;

public class CancelCommand implements IPhotonCommand {

	private final String id;

	public CancelCommand(String id) {
		this.id = id;
	}

	public void execute() {
		Logger logger = PhotonPlugin.getMainConsoleLogger();
		try {
			PhotonPlugin.getDefault().getPhotonController().cancelOneOrderByClOrdID(id);
		} catch (NoMoreIDsException e) {
			logger.error("Exception cancelling order", e);
		}
	}

	public Object getID() {
		return id;
	}

}
