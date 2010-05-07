/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.custodia;

/**
 * Informació sobre la signatura d'un document.
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class SignaturaInfo {

	private static final String TOKEN_CN = "CN=";
	private static final String TOKEN_GIVENNAME = "GIVENNAME=";
	private static final String TOKEN_SURNAME = "SURNAME=";
	private static final String TOKEN_SERIALNUMBER = "SERIALNUMBER=";
	private static final String TOKEN_EMAIL = ",E=";

	private int nivell;
	private int ordre;
	private boolean verificada;
	private String certNumSerie;
	private String certSubject;
	private boolean certVerificat;



	public int getNivell() {
		return nivell;
	}
	public void setNivell(int nivell) {
		this.nivell = nivell;
	}
	public int getOrdre() {
		return ordre;
	}
	public void setOrdre(int ordre) {
		this.ordre = ordre;
	}
	public boolean isVerificada() {
		return verificada;
	}
	public void setVerificada(boolean verificada) {
		this.verificada = verificada;
	}
	public String getCertNumSerie() {
		return certNumSerie;
	}
	public void setCertNumSerie(String certNumSerie) {
		this.certNumSerie = certNumSerie;
	}
	public String getCertSubject() {
		return certSubject;
	}
	public void setCertSubject(String certSubject) {
		this.certSubject = certSubject;
	}
	public boolean isCertVerificat() {
		return certVerificat;
	}
	public void setCertVerificat(boolean certVerificat) {
		this.certVerificat = certVerificat;
	}

	public String getCertPersona() {
		int indexInici = getCertSubject().indexOf(TOKEN_CN);
		int indexFi = getCertSubject().indexOf(",", indexInici);
		indexInici += TOKEN_CN.length();
		return getCertSubject().substring(indexInici, indexFi);
	}
	public String getCertNom() {
		int indexInici = getCertSubject().indexOf(TOKEN_GIVENNAME);
		int indexFi = getCertSubject().indexOf(",", indexInici);
		indexInici += TOKEN_GIVENNAME.length();
		return getCertSubject().substring(indexInici, indexFi);
	}
	public String getCertCognoms() {
		int indexInici = getCertSubject().indexOf(TOKEN_SURNAME);
		int indexFi = getCertSubject().indexOf(",", indexInici);
		indexInici += TOKEN_SURNAME.length();
		return getCertSubject().substring(indexInici, indexFi);
	}
	public String getCertNif() {
		int indexInici = getCertSubject().indexOf(TOKEN_SERIALNUMBER);
		int indexFi = getCertSubject().indexOf(",", indexInici);
		indexInici += TOKEN_SERIALNUMBER.length();
		return getCertSubject().substring(indexInici, indexFi);
	}
	public String getCertEmail() {
		int indexInici = getCertSubject().indexOf(TOKEN_EMAIL);
		int indexFi = getCertSubject().indexOf(",", indexInici + 1);
		indexInici += TOKEN_EMAIL.length();
		return getCertSubject().substring(indexInici, indexFi);
	}

}
