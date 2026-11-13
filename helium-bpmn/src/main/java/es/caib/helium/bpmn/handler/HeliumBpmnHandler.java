package es.caib.helium.bpmn.handler;

import es.caib.helium.bpmn.api.HeliumApi;
import es.caib.helium.bpmn.exception.BpmnException;
import es.caib.helium.bpmn.exception.HeliumHandlerException;
import org.flowable.engine.delegate.JavaDelegate;

public interface HeliumBpmnHandler extends JavaDelegate {
	void execute(HeliumApi heliumApi) throws HeliumHandlerException, BpmnException;
}
