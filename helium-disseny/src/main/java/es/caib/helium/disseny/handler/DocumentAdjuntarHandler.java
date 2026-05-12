package es.caib.helium.disseny.handler;

/**
 * Handler per a consultar la informació d'un document i desar-la a dins variables.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface DocumentAdjuntarHandler extends HeliumActionHandler {

	void setDocumentOrigen(String documentOrigen);
	void setVarDocumentOrigen(String varDocumentOrigen);
	void setTitol(String titol);
	void setVarTitol(String varTitol);
	void setData(String data);
	void setVarData(String varData);
	void setConcatenarTitol(String concatenarTitol);
	void setEsborrarDocument(String esborrarDocument);

}
