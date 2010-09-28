/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.signatura;

/**
 * Informació d'una signatura
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class InfoSignatura {

	private byte[] signatura;
	private InfoCertificat infoCertificat;
	private boolean valida = false;



	public InfoSignatura(byte[] signatura) {
		this.signatura = signatura;
	}

	public byte[] getSignatura() {
		return signatura;
	}
	public void setSignatura(byte[] signatura) {
		this.signatura = signatura;
	}
	public InfoCertificat getInfoCertificat() {
		return infoCertificat;
	}
	public void setInfoCertificat(InfoCertificat infoCertificat) {
		this.infoCertificat = infoCertificat;
	}
	public boolean isValida() {
		return valida;
	}
	public void setValida(boolean valida) {
		this.valida = valida;
	}

}
