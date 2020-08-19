/** HERÈNCIA
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import net.conselldemallorca.helium.v3.core.api.dto.DadaIndexadaDto;
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
	
	@RequestMapping(value = "/{expedientId}", method = RequestMethod.GET)
	public String info(
			HttpServletRequest request, 
			@PathVariable Long expedientId, 
			Model model) {
		ExpedientDto expedient = null;
		try {
			expedient = expedientService.findAmbIdAmbPermis(expedientId);
			model.addAttribute("expedient", expedient);
			//List<Map<String, DadaIndexadaDto>> dadesLucene = expedientServiceV26.luceneGetDades(String.valueOf(expedient.getProcessInstanceId()));
			List<Map<String, DadaIndexadaDto>> dadesLucene = expedientService.luceneGetDades(expedient.getId());
			if (dadesLucene.size() > 0) {
				if (dadesLucene.size() > 1) {
					MissatgesHelper.error(request, getMessage(request, "expedient.lucene.error.varis", new Object[] {dadesLucene.size(), expedientId}));
				}
				// Construeix la taula de dades pel llistat
				Map<String, DadaIndexadaDto> dadesExpedient = dadesLucene.get(0);
				List<Map<String, Object>> dades = new ArrayList<Map<String, Object>>();
				int nErrors = 0;
				for (DadaIndexadaDto dadaIndexada : dadesExpedient.values()) {
					Map<String, Object> dada = new HashMap<String, Object>();
					// Tipus expedient EX, variable TE o variable DP
					String tipus;
					String caràcter$;
					if (dadaIndexada.getCampCodi().startsWith("expedient$"))
						tipus = "EX";
					else 
						tipus = dadaIndexada.isDadaExpedient() ? "TE" : "DP";
					dada.put("tipus", tipus);
					dada.put("dadaIndexada", dadaIndexada);
					
					// Errors del camp
					boolean error = dadaIndexada.getError() != null;
					dada.put("error", error);
					if (error) {
						dada.put("errorDescripcio", dadaIndexada.getError());
						nErrors++;
					}
					
					dades.add(dada);
				}
				model.addAttribute("dades", dades);
				
				if (nErrors > 0)
					MissatgesHelper.error(request, "S'han trobat " + nErrors + " camps amb error de sincronització");

			}
			return "v3/expedientLucene";
		} catch (Exception e) {
			String errMsg = "Error consultant les dades indexades de l'expedient amb id " + expedientId + ": " + e.getMessage();
			logger.error(errMsg, e);
			MissatgesHelper.error(request, errMsg);
			return "redirect:/v3/expedient" + (expedient != null ? "/" + expedient.getId() : "");
		}
	}
	
	private static final Log logger = LogFactory.getLog(ExpedientLuceneController.class);	
}
