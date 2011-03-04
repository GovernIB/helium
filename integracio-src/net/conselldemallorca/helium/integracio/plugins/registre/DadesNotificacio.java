/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.registre;

/**
 * Informació sobre la notificació en una anotació de sortida
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DadesNotificacio extends DadesAnotacio {

	private boolean justificantRecepcio;
	private String avisTitol;
	private String avisText;
	private String avisTextSms;
	private String oficiTitol;
	private String oficiText;
	private TramitSubsanacio oficiTramitSubsanacio;
	


	public boolean isJustificantRecepcio() {
		return justificantRecepcio;
	}
	public void setJustificantRecepcio(boolean justificantRecepcio) {
		this.justificantRecepcio = justificantRecepcio;
	}
	public String getAvisTitol() {
		return avisTitol;
	}
	public void setAvisTitol(String avisTitol) {
		this.avisTitol = avisTitol;
	}
	public String getAvisText() {
		return avisText;
	}
	public void setAvisText(String avisText) {
		this.avisText = avisText;
	}
	public String getAvisTextSms() {
		return avisTextSms;
	}
	public void setAvisTextSms(String avisTextSms) {
		this.avisTextSms = avisTextSms;
	}
	public String getOficiTitol() {
		return oficiTitol;
	}
	public void setOficiTitol(String oficiTitol) {
		this.oficiTitol = oficiTitol;
	}
	public String getOficiText() {
		return oficiText;
	}
	public void setOficiText(String oficiText) {
		this.oficiText = oficiText;
	}
	public TramitSubsanacio getOficiTramitSubsanacio() {
		return oficiTramitSubsanacio;
	}
	public void setOficiTramitSubsanacio(TramitSubsanacio oficiTramitSubsanacio) {
		this.oficiTramitSubsanacio = oficiTramitSubsanacio;
	}

}
