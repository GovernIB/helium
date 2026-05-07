package es.caib.helium.bpmn.handler;

/**
 * Handler per a consultar la informació d'un document i desar-la a dins variables.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface DocumentConsultarHandler extends HeliumActionHandler {

	void setDocument(String document);
	void setVarDocument(String varDocument);
	void setVarCsv(String varCsv);
	void setVarUrl(String varUrl);

}
