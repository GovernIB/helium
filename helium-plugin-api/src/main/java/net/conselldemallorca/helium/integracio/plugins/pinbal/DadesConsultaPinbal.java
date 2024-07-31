/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.pinbal;

import org.apache.commons.lang.builder.ToStringBuilder;

import net.conselldemallorca.helium.v3.core.api.dto.PinbalConsentimentEnum;

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
	//Indicarem si volem guardar la petició pinbal a la taula HEL_PETICIO_PINBAL en cas de error
	//Si cridam desde handler si, pero si cridam desde modal document Pinbal, no, ja es mostra el error i es pot tornar a intentar.
	private boolean guardarError=true;
	private String anyNaixement;
	private PinbalConsentimentEnum consentiment;
	
	public DadesConsultaPinbal(
			Titular titular,
			Funcionari funcionari,
			String xmlDadesEspecifiques,
			String serveiCodi,
			String documentCodi,
			String finalitat,
			PinbalConsentimentEnum consentiment, 
			String interessatCodi,
			String codiProcediment, 
			String entitat_CIF,
			String unitatTramitadora,
			boolean asincrona,
			String anyNaixement) {
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
		this.anyNaixement=anyNaixement;
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

	public PinbalConsentimentEnum getConsentiment() {
		return consentiment;
	}

	public void setConsentiment(PinbalConsentimentEnum consentiment) {
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

	public String getAnyNaixement() {
		return anyNaixement;
	}

	public void setAnyNaixement(String anyNaixement) {
		this.anyNaixement = anyNaixement;
	}

	public boolean isGuardarError() {
		return guardarError;
	}

	public void setGuardarError(boolean guardarError) {
		this.guardarError = guardarError;
	}
	

}
