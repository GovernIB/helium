/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.signatura.afirma;


/**
 * Resposta a una petició d'informació d'un certificat
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ObtenirDadesCertificatResponse extends BaseResponse {

	private DadesCertificat dadesCertificat;



	public DadesCertificat getDadesCertificat() {
		return dadesCertificat;
	}
	public void setDadesCertificat(DadesCertificat dadesCertificat) {
		this.dadesCertificat = dadesCertificat;
	}

}
