package es.caib.helium.logic.bpmn;

import es.caib.helium.bpmn.api.HeliumApi;
import es.caib.helium.bpmn.exception.HeliumHandlerException;
import lombok.Setter;

/**
 * Implementació del handler per a consultar la informació d'un document i desar-la a dins variables.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Setter
public class DocumentConsultarHandler implements es.caib.helium.bpmn.handler.DocumentConsultarHandler {

	private String document;
	private String varDocument;
	private String varCsv;
	private String varUrl;

	@Override
	public void execute(HeliumApi heliumApi) throws HeliumHandlerException {
		heliumApi.documentConsultar(
			heliumApi.getVariableDefaultValue(varDocument, document),
			varCsv,
			varUrl);
	}

}
