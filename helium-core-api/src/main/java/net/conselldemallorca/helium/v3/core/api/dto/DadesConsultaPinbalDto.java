/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;


import org.apache.commons.lang.builder.ToStringBuilder;


/**
 * Informació d'una consulta per fer una petició a Pinbal
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */

public class DadesConsultaPinbalDto {
	
	private String serveiCodi;
	private TitularDto titular;
	private FuncionariDto funcionari;
	private String documentCodi;
	private String xmlDadesEspecifiques;
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
	

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public TitularDto getTitular() {
		return titular;
	}

	public void setTitular(TitularDto titular) {
		this.titular = titular;
	}

	public FuncionariDto getFuncionari() {
		return funcionari;
	}

	public void setFuncionari(FuncionariDto funcionari) {
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
