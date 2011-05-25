/**
 * 
 */
package net.conselldemallorca.helium.core.model.dto;

import net.conselldemallorca.helium.integracio.plugins.signatura.DadesCertificat;



/**
 * DTO amb informació d'un arxiu per descarregar
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DadesCertificatDto extends DadesCertificat {

	private boolean valida;

	public boolean isValida() {
		return valida;
	}
	public void setValida(boolean valida) {
		this.valida = valida;
	}

}
