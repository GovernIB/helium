/**
 * 
 */
package net.conselldemallorca.helium.presentacio.dwr;

import java.text.SimpleDateFormat;

import net.conselldemallorca.helium.model.hibernate.Festiu;
import net.conselldemallorca.helium.model.service.TerminiService;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * Servei DWR per a la gestió de festius
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class FestiuDwrService {

	private TerminiService terminiService;



	@Autowired
	public FestiuDwrService(
			TerminiService terminiService) {
		this.terminiService = terminiService;
	}

	public boolean crear(String data) {
		try {
			Festiu festiu = new Festiu();
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			festiu.setData(sdf.parse(data));
			terminiService.createFestiu(festiu);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	public boolean esborrar(String data) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			Festiu festiu = terminiService.findFestiuAmbData(sdf.parse(data));
			if (festiu != null) {
				terminiService.deleteFestiu(festiu.getId());
				return true;
			}
			return false;
		} catch (Exception ex) {
			return false;
		}
	}

}
