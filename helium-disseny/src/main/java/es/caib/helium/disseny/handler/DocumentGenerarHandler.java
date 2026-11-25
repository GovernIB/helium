package es.caib.helium.disseny.handler;

/**
 * Handler per generar un document a partir de la seva plantilla.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface DocumentGenerarHandler {
	void setDocumentCodi(String documentCodi);
	void setData(String data);
}
