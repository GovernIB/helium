package es.caib.helium.logic.bpmn;

import es.caib.helium.bpmn.api.HeliumApi;
import es.caib.helium.bpmn.exception.HeliumHandlerException;
import es.caib.helium.bpmn.handler.AbstractHeliumActionHandler;
import lombok.Setter;

/**
 * Implementació del handler per modificar el títol de la instància de procés on s'executa. És el títol que es
 * mostra a l'arbre de processos de l'expedient, útil per distingir les instàncies d'un mateix subprocés.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Setter
public class ProcesTitolModificarHandler extends AbstractHeliumActionHandler implements es.caib.helium.disseny.handler.ProcesTitolModificarHandler {

	private String titol;

	@Override
	public void execute(HeliumApi heliumApi) throws HeliumHandlerException {
		heliumApi.procesTitolModificar(titol);
	}

}
