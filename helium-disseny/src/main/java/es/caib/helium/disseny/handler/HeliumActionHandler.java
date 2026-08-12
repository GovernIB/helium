package es.caib.helium.disseny.handler;

import es.caib.helium.disseny.api.HeliumApi;
import es.caib.helium.disseny.exception.BpmnException;
import es.caib.helium.disseny.exception.HeliumHandlerException;

public interface HeliumActionHandler {

	void execute(HeliumApi heliumApi) throws HeliumHandlerException, BpmnException;

}
