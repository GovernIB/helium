package net.conselldemallorca.helium.jbpm3.api;

import net.conselldemallorca.helium.jbpm3.handlers.exception.HeliumHandlerException;

import java.util.List;

public abstract class HeliumActionHandler {

	public abstract void execute(HeliumApi heliumApi) throws HeliumHandlerException;
	public abstract void retrocedir(HeliumApi heliumApi, List<String> parametres) throws Exception;

}
