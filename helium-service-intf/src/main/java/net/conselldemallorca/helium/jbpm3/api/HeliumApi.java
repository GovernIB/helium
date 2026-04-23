package net.conselldemallorca.helium.jbpm3.api;

import net.conselldemallorca.helium.jbpm3.handlers.exception.HeliumHandlerException;

public abstract class HeliumApi {

	public abstract void execute(HeliumApi heliumApi) throws HeliumHandlerException;

}
