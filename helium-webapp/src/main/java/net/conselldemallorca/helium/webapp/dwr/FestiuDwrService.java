/**
 * 
 */
package net.conselldemallorca.helium.webapp.dwr;

import org.springframework.beans.factory.annotation.Autowired;

import net.conselldemallorca.helium.v3.core.api.service.ExpedientTerminiService;

/**
 * Servei DWR per a la gestió de festius
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class FestiuDwrService {

	private ExpedientTerminiService expedientTerminiService;



	@Autowired
	public FestiuDwrService(
			ExpedientTerminiService expedientTerminiService) {
		this.expedientTerminiService = expedientTerminiService;
	}

	public boolean crear(String data) {
		try {
			expedientTerminiService.festiuCreate(data);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	public boolean esborrar(String data) {
		boolean ret = false;
		try {
			expedientTerminiService.festiuDelete(data);
			ret = true;
		} catch (Exception e) {
			// si no es troba la data llença excepció
		}
		return ret;
	}

}
