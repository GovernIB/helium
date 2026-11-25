package es.caib.helium.disseny.handler;

/**
 * Handler per consultar a PINBAL si el titular està al corrent de les obligacions tributàries (SVDCCAACPASWS01).
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface PinbalSvdccaacpasws01Handler {
	void setDocumentCodi(String documentCodi);
	void setFinalitat(String finalitat);
	void setConsentiment(String consentiment);
	void setTitularTipusDocumentacio(String titularTipusDocumentacio);
	void setTitularDocumentacio(String titularDocumentacio);
	void setInteressatCodi(String interessatCodi);
	void setTitularNom(String titularNom);
	void setTitularLlinatge1(String titularLlinatge1);
	void setTitularLlinatge2(String titularLlinatge2);
	void setAsincrona(String asincrona);
	void setTransicioOK(String transicioOK);
	void setTransicioKO(String transicioKO);
}
