package net.conselldemallorca.helium.jbpm3.api;

import java.util.List;

import org.jbpm.graph.def.ActionHandler;

import net.conselldemallorca.helium.jbpm3.handlers.exception.HeliumHandlerException;

@SuppressWarnings("serial")
public abstract class HeliumActionHandler implements ActionHandler {

	public abstract void execute(HeliumApi heliumApi) throws HeliumHandlerException;
	public abstract void retrocedir(HeliumApi heliumApi, List<String> parametres) throws Exception;

}
