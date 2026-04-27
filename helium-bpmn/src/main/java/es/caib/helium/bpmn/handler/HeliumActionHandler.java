package es.caib.helium.bpmn.handler;

import es.caib.helium.bpmn.api.HeliumApi;
import es.caib.helium.bpmn.exception.HeliumHandlerException;

public interface HeliumActionHandler {

	void execute(HeliumApi heliumApi) throws HeliumHandlerException;

}
