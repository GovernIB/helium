package es.caib.helium.bpmn.api;

import es.caib.helium.bpmn.exception.HeliumHandlerException;

public abstract class HeliumApi {

	public abstract void execute(HeliumApi heliumApi) throws HeliumHandlerException;

}
