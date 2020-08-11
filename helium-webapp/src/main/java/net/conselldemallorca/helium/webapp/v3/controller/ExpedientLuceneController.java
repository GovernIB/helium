/** HERÈNCIA
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import net.conselldemallorca.helium.core.model.dto.DadaIndexadaDto;
import net.conselldemallorca.helium.core.model.service.ExpedientService;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;

/**
 * Controlador per a la pàgina d'informació de l'expedient amb les dades de Lucene.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller("expedientLuceneV3")
@RequestMapping("/v3/expedient/lucene")
public class ExpedientLuceneController extends BaseExpedientController {
	
	@Autowired
	private ExpedientService expedientServiceV26;

	@RequestMapping(value = "/{expedientId}", method = RequestMethod.GET)
	public String info(
			HttpServletRequest request, 
			@PathVariable Long expedientId, 
			Model model) {
		try {
			ExpedientDto expedient = expedientService.findAmbIdAmbPermis(expedientId);
			model.addAttribute("expedient", expedient);
			List<Map<String, DadaIndexadaDto>> dadesLucene = expedientServiceV26.luceneGetDades(String.valueOf(expedient.getProcessInstanceId()));
			if (dadesLucene.size() > 0) {
				Map<String, DadaIndexadaDto> dadesExpedient = dadesLucene.get(0);
				List<DadaIndexadaDto> llistaDadesExpedient = new ArrayList<DadaIndexadaDto>();
				for (String clau: dadesExpedient.keySet()) {
					if (dadesExpedient.get(clau).isDadaExpedient())
						llistaDadesExpedient.add(dadesExpedient.get(clau));
				}
				model.addAttribute(
						"dadesExpedient",
						llistaDadesExpedient);
				List<DadaIndexadaDto> llistaDadesCamps = new ArrayList<DadaIndexadaDto>();
				for (String clau: dadesExpedient.keySet()) {
					if (!dadesExpedient.get(clau).isDadaExpedient())
						llistaDadesCamps.add(dadesExpedient.get(clau));
				}
				model.addAttribute(
						"dadesCamps",
						llistaDadesCamps);
			}
			return "v3/expedientLucene";
		} catch (Exception e) {
			MissatgesHelper.error(request, "Error consultant l'expedient amb id " + expedientId + ": " + e.getMessage());
			return "redirect:/v3/expedient";
		}
	}
	
	
}
