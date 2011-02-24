/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.signatura.afirma;


/**
 * Resposta a una petició de validació d'un certificat
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ValidarCertificatResponse extends BaseResponse {

	private DadesCertificat dadesCertificat;



	public DadesCertificat getDadesCertificat() {
		return dadesCertificat;
	}
	public void setDadesCertificat(DadesCertificat dadesCertificat) {
		this.dadesCertificat = dadesCertificat;
	}

}
