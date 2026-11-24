package es.caib.helium.disseny.handler;



/**
 * Handler per a adjuntar un document a l'expedient.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface DocumentAdjuntarHandler {
	void setDocumentOrigen(String documentOrigen);
	void setTitol(String titol);
	void setData(String data);
	void setConcatenarTitol(String concatenarTitol);
	void setEsborrarDocument(String esborrarDocument);
}
