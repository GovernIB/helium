package es.caib.helium.disseny.handler;

/**
 * Handler per consultar a PINBAL les dades d'identitat (SVDDGPCIWS02).
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface PinbalSvddgpciws02Handler {
	void setDocumentCodi(String documentCodi);
	void setFinalitat(String finalitat);
	void setConsentiment(String consentiment);
	void setAnyNaixement(String anyNaixement);
	void setTitularTipusDocumentacio(String titularTipusDocumentacio);
	void setTitularDocumentacio(String titularDocumentacio);
	void setTitularLlinatge1(String titularLlinatge1);
	void setTitularLlinatge2(String titularLlinatge2);
	void setTitularNom(String titularNom);
	void setInteressatCodi(String interessatCodi);
	void setAsincrona(String asincrona);
	void setTransicioOK(String transicioOK);
	void setTransicioKO(String transicioKO);
}
