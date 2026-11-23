package es.caib.helium.bpmn.handler;

import es.caib.helium.bpmn.api.HeliumApi;
import es.caib.helium.bpmn.exception.BpmnException;
import es.caib.helium.bpmn.exception.HeliumHandlerException;

/**
 * Contracte dels handlers de Helium.
 *
 * <p>És independent del motor de workflow: el handler només rep la {@link HeliumApi} i els
 * seus paràmetres s'injecten com a propietats JavaBean (mètodes {@code setXxx}) abans
 * d'executar-lo. Cada motor de workflow disposa d'un adaptador que llegeix la definició
 * del handler del BPMN (veure {@link HandlerDefinicio}), crea una instància nova per a cada
 * execució, li injecta els paràmetres amb {@link HandlerParametres} i crida
 * {@link #execute(HeliumApi)}.</p>
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface HeliumBpmnHandler {

	void execute(HeliumApi heliumApi) throws HeliumHandlerException, BpmnException;

}
