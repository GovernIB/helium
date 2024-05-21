/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.pinbal;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Informació d'una notificació per al seu enviament.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */

public class DadesConsultaPinbal {
	
	
	private Titular titular;
	private Funcionari funcionari;
	private String xmlDadesEspecifiques;
	private String documentCodi;
	private String serveiCodi;
	private String finalitat;
	private String interessatCodi;
	private String codiProcediment;
	private String entitat_CIF;
	private String unitatTramitadora;
	private boolean asincrona;
	

	public enum consentiment {
		SI,
		LLEI
	}
	private String consentiment;
	
	public DadesConsultaPinbal(
			Titular titular,
			Funcionari funcionari,
			String xmlDadesEspecifiques,
			String serveiCodi,
			String documentCodi,
			String finalitat,
			String consentiment, 
			String interessatCodi,
			String codiProcediment, 
			String entitat_CIF,
			String unitatTramitadora,
			boolean asincrona) {
		this.titular=titular;
		this.funcionari=funcionari;
		this.xmlDadesEspecifiques=xmlDadesEspecifiques;
		this.serveiCodi=serveiCodi;	
		this.documentCodi=documentCodi;
		this.finalitat=finalitat;
		this.consentiment=consentiment;
		this.interessatCodi=interessatCodi;
		this.codiProcediment=codiProcediment;
		this.entitat_CIF=entitat_CIF;
		this.unitatTramitadora=unitatTramitadora;
		this.asincrona=asincrona;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}


	public Titular getTitular() {
		return titular;
	}

	public void setTitular(Titular titular) {
		this.titular = titular;
	}

	public Funcionari getFuncionari() {
		return funcionari;
	}

	public void setFuncionari(Funcionari funcionari) {
		this.funcionari = funcionari;
	}

	public String getXmlDadesEspecifiques() {
		return xmlDadesEspecifiques;
	}

	public void setXmlDadesEspecifiques(String xmlDadesEspecifiques) {
		this.xmlDadesEspecifiques = xmlDadesEspecifiques;
	}

	public String getDocumentCodi() {
		return documentCodi;
	}

	public void setDocumentCodi(String documentCodi) {
		this.documentCodi = documentCodi;
	}

	public String getServeiCodi() {
		return serveiCodi;
	}

	public void setServeiCodi(String serveiCodi) {
		this.serveiCodi = serveiCodi;
	}

	public String getFinalitat() {
		return finalitat;
	}

	public void setFinalitat(String finalitat) {
		this.finalitat = finalitat;
	}

	public String getInteressatCodi() {
		return interessatCodi;
	}

	public void setInteressatCodi(String interessatCodi) {
		this.interessatCodi = interessatCodi;
	}

	public String getConsentiment() {
		return consentiment;
	}

	public void setConsentiment(String consentiment) {
		this.consentiment = consentiment;
	}

	public String getCodiProcediment() {
		return codiProcediment;
	}

	public void setCodiProcediment(String codiProcediment) {
		this.codiProcediment = codiProcediment;
	}

	public String getEntitat_CIF() {
		return entitat_CIF;
	}

	public void setEntitat_CIF(String entitat_CIF) {
		this.entitat_CIF = entitat_CIF;
	}

	public String getUnitatTramitadora() {
		return unitatTramitadora;
	}

	public void setUnitatTramitadora(String unitatTramitadora) {
		this.unitatTramitadora = unitatTramitadora;
	}
	
	public boolean isAsincrona() {
		return asincrona;
	}

	public void setAsincrona(boolean asincrona) {
		this.asincrona = asincrona;
	}
	

}
