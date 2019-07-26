/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import net.conselldemallorca.helium.v3.core.api.dto.CampDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDadaDto;
import net.conselldemallorca.helium.v3.core.api.service.CampService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientDadaService;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.TascaFormHelper;

/**
 * Controlador per poder consultar informació sobre els camps de tasques.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/camptasca")
public class CampTascaController extends BaseExpedientController {

	@Autowired
	private ExpedientDadaService expedientDadaService;
	@Autowired
	private CampService campService;

	/** Mètode per retornar l'HTML d'una nova fila per una variable de tipus registre múltiple en el cas que es vulgui afegir una nova
	 * fila a un registre múltiple en la modificació de la variable o la tramitació del formulari inicial o de la tasca.
	 * 
	 */
	@RequestMapping(value = "/{campId}/afegir", method = RequestMethod.GET)
	public String registreAfegir(
			HttpServletRequest request,
			@PathVariable(value = "campId") Long campId,
			ModelMap model) {
		try {
			CampDto camp = campService.findAmbId(null, campId);
			String varCodi = camp.getCodi();
			Map<String, Object> campsAddicionals = new HashMap<String, Object>();
			Map<String, Class<?>> campsAddicionalsClasses = new HashMap<String, Class<?>>();
			List<TascaDadaDto> llistTasca = new ArrayList<TascaDadaDto>();
			TascaDadaDto tascaDada = TascaFormHelper.getTascaDadaDtoFromExpedientDadaDto(
					//expedientDadaService.findOnePerInstanciaProces(expedientId, procesId, varCodi)
					expedientDadaService.getDadaBuida(campId)
			);
			if (tascaDada.getError() != null)
				MissatgesHelper.error(request, tascaDada.getError());
			llistTasca.add(tascaDada);
			model.addAttribute("varCodi", varCodi);
			model.addAttribute("dada", tascaDada);
			Object command = TascaFormHelper.getCommandForCamps(llistTasca, null, campsAddicionals,
					campsAddicionalsClasses, false);
			model.addAttribute("modificarVariableCommand", command);
		} catch (Exception ex) {
			MissatgesHelper.error(request, ex.getMessage());
			logger.error("No s'ha pogut obtenir la informació del camp amb id " + campId + ": " + ex.getMessage(), ex);
		}
		return "v3/campsTascaRegistreRow";
	}
				
				
	private static final Logger logger = LoggerFactory.getLogger(CampTascaController.class);
}