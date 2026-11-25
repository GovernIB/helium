package es.caib.helium.disseny.handler;

/**
 * Handler per fer una consulta genèrica a PINBAL i desar-ne el justificant en un document.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface PinbalConsultaGenericaHandler {
	void setServeiCodi(String serveiCodi);
	void setDocumentCodi(String documentCodi);
	void setFinalitat(String finalitat);
	void setConsentiment(String consentiment);
	void setTitularTipusDocumentacio(String titularTipusDocumentacio);
	void setTitularDocumentacio(String titularDocumentacio);
	void setInteressatCodi(String interessatCodi);
	void setTitularNom(String titularNom);
	void setTitularLlinatge1(String titularLlinatge1);
	void setTitularLlinatge2(String titularLlinatge2);
	void setDadesEspecifiques(String dadesEspecifiques);
	void setAsincrona(String asincrona);
	void setTransicioOK(String transicioOK);
	void setTransicioKO(String transicioKO);
}
