/**
 * 
 */
package net.conselldemallorca.helium.presentacio.mvc;

/**
 * Command per iniciar un expedient
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class ExpedientTipusFormextCommand {

	private Long expedientTipusId;
	private boolean actiu;
	private String url;
	private String usuari;
	private String contrasenya;



	public ExpedientTipusFormextCommand() {}

	public Long getExpedientTipusId() {
		return expedientTipusId;
	}
	public void setExpedientTipusId(Long expedientTipusId) {
		this.expedientTipusId = expedientTipusId;
	}

	public boolean isActiu() {
		return actiu;
	}
	public void setActiu(boolean actiu) {
		this.actiu = actiu;
	}

	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsuari() {
		return usuari;
	}
	public void setUsuari(String usuari) {
		this.usuari = usuari;
	}

	public String getContrasenya() {
		return contrasenya;
	}
	public void setContrasenya(String contrasenya) {
		this.contrasenya = contrasenya;
	}

}
