/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.signatura.afirma;

import java.util.List;

/**
 * Resposta a una petició de validació de signatura
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ValidarSignaturaResponse extends BaseResponse {

	private List<DadesCertificat> dadesCertificat;



	public List<DadesCertificat> getDadesCertificat() {
		return dadesCertificat;
	}
	public void setDadesCertificat(List<DadesCertificat> dadesCertificat) {
		this.dadesCertificat = dadesCertificat;
	}

}
