/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.v3.core.api.dto.ParellaCodiValorDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampDto;
import net.conselldemallorca.helium.v3.core.api.service.DissenyService;
import net.conselldemallorca.helium.v3.core.api.service.TascaService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controlador per fer consultes als dominis de dins d'un expedient
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/domini")
public class ExpedientConsultaDominioController extends BaseExpedientController {
	@Autowired
	private TascaService tascaService;
	@Autowired
	private DissenyService dissenyService;

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/consultaExpedient", method = RequestMethod.GET)
	@ResponseBody
	public List<ParellaCodiValorDto> consultaCamp(
			HttpServletRequest request,
			@RequestParam(value = "taskId", required = false) String taskId,
			@RequestParam(value = "processInstanceId", required = false) String processInstanceId,
			@RequestParam(value = "campId", required = true) Long campId,
			@RequestParam(value = "q", required = false) String textInicial,
			@RequestParam(value = "valors", required = false) String valors,
			ModelMap model) {
		List<ParellaCodiValorDto> resultat = new ArrayList<ParellaCodiValorDto>();
		CampDto camp = tascaService.findCampTasca(campId);
		try {
			resultat  = (List<ParellaCodiValorDto>) dissenyService.getResultatConsultaCamp(
					taskId,
					processInstanceId,
					camp,
					textInicial,
					getMapDelsValors(valors));
			for (ParellaCodiValorDto codiValor: resultat) {
				if (codiValor.getValor() instanceof String) {
					String valor = (String)codiValor.getValor();
					// Per a evitar problemes amb caràcters estranys al codi (EXSANCI)
					codiValor.setValor(valor.replaceAll("\\p{Cntrl}", "").trim());
				}
			}
		} catch (Exception ex) {
			logger.error("Error en la consulta de domini pel camp " + campId, ex);
		}
		return resultat;
	}

	private Map<String, Object> getMapDelsValors(String valors) {
		if (valors == null)
			return null;
		Map<String, Object> resposta = new HashMap<String, Object>();
		String[] parelles = valors.split(",");
		for (int i = 0; i < parelles.length; i++) {
			String[] parts = parelles[i].split(":");
			if (parts.length == 2)
				resposta.put(parts[0], parts[1]);
		}
		return resposta;
	}

	private static final Log logger = LogFactory.getLog(ExpedientConsultaDominioController.class);

}
