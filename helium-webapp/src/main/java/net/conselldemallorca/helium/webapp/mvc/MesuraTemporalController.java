/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import java.text.DecimalFormat;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.core.model.dto.IntervalEventDto;
import net.conselldemallorca.helium.core.model.dto.MesuraTemporalDto;
import net.conselldemallorca.helium.core.model.service.AdminService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controlador per a la pàgina inicial (index).
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
public class MesuraTemporalController {

	@Resource
	private AdminService adminService;

	@RequestMapping(value = "/mesura/mesuresTemps", method = RequestMethod.GET)
	@ResponseBody
	public String mesuresTemps(HttpServletRequest request) {
		List<MesuraTemporalDto> mesures = adminService.findMesuresTemporals();
		if (mesures.isEmpty())
			return "{}";

		DecimalFormat df = new DecimalFormat( "####0.00" );
		
		String json = "";
		String claus = "";
		String darrers = "";
		String mitjes = "";
		String minimes = "";
		String maximes = "";
		String numMesures = "";
		String periodes = "";
		String series = "";
		
		for (MesuraTemporalDto mesura: mesures) {
			claus += "\"" + mesura.getClau() + "\",";
			darrers += "\"" + mesura.getDarrera() + " ms\",";
			mitjes += "\"" + df.format(mesura.getMitja()) + " ms\",";
			minimes += "\"" + mesura.getMinima() + " ms\",";
			maximes += "\"" + mesura.getMaxima() + " ms\",";
			numMesures += "\"" + mesura.getNumMesures() + "\",";
			periodes += "\"" + mesura.getPeriode() + " ms\",";
			
			series += "\"" + mesura.getClau() + "\": {\"label\": \"" + mesura.getClau() + "\", \"data\": [";
			for (IntervalEventDto event: mesura.getEvents()) {
				series += "[" + event.getDate().getTime() + ", " + event.getDuracio() + "],";
			}
			if (!mesura.getEvents().isEmpty())
				series = series.substring(0, series.length() -1);
			series += "]},";
		}
		
		json += "{\"clau\": [" + claus.substring(0, claus.length() -1) + "],";
		json += "\"darrera\": [" + darrers.substring(0, darrers.length() -1) + "],";
		json += "\"mitja\": [" + mitjes.substring(0, mitjes.length() -1) + "],";
		json += "\"minima\": [" + minimes.substring(0, minimes.length() -1) + "],";
		json += "\"maxima\": [" + maximes.substring(0, maximes.length() -1) + "],";
		json += "\"numMesures\": [" + numMesures.substring(0, numMesures.length() -1) + "],";
		json += "\"periode\": [" + periodes.substring(0, periodes.length() -1) + "],";
		json += "\"series\": {" + series.substring(0, series.length() -1) + "}}";
		return json;
	}

}
