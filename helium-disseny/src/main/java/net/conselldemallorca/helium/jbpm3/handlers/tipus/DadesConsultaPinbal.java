/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers.tipus;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Informació d'una notificació per al seu enviament.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */

public class DadesConsultaPinbal {
	
	
	private Titular titular;
	private Funcionari funcionari;
	private String documentCodi;
	private String xmlDadesEspecifiques;
	private String serveiCodi;
	private String interessatCodi;
	private String finalitat;

	public enum consentiment {
		SI,
		LLEI
	}
	private String consentiment;
	

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

	public String getServeiCodi() {
		return serveiCodi;
	}

	public void setServeiCodi(String serveiCodi) {
		this.serveiCodi = serveiCodi;
	}

	public String getDocumentCodi() {
		return documentCodi;
	}

	public void setDocumentCodi(String documentCodi) {
		this.documentCodi = documentCodi;
	}

	public String getXmlDadesEspecifiques() {
		return xmlDadesEspecifiques;
	}

	public void setXmlDadesEspecifiques(String xmlDadesEspecifiques) {
		this.xmlDadesEspecifiques = xmlDadesEspecifiques;
	}

	public String getInteressatCodi() {
		return interessatCodi;
	}

	public void setInteressatCodi(String interessatCodi) {
		this.interessatCodi = interessatCodi;
	}

	public String getFinalitat() {
		return finalitat;
	}

	public void setFinalitat(String finalitat) {
		this.finalitat = finalitat;
	}

	public String getConsentiment() {
		return consentiment;
	}

	public void setConsentiment(String consentiment) {
		this.consentiment = consentiment;
	}


}
