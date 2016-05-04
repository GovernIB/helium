/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDadaDto;
import net.conselldemallorca.helium.v3.core.api.service.DissenyService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.v3.core.api.service.ReproService;
import net.conselldemallorca.helium.v3.core.api.service.TascaService;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;
import net.conselldemallorca.helium.webapp.v3.helper.TascaFormHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;

/**
 * Controlador per Repros
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/repro")
public class ReproController extends BaseController {

	@Autowired
	private ReproService reproService;
	@Autowired
	protected ExpedientService expedientService;
	@Autowired
	protected DissenyService dissenyService;
	@Autowired
	protected TascaService tascaService;

	@RequestMapping(value = "/guardarRepro/{expedientTipusId}/{definicioProcesId}", method = RequestMethod.POST)
	public String iniciarFormPost(
			HttpServletRequest request, 
			@RequestParam(value = "nom", required = true) String nom, 
			@PathVariable Long expedientTipusId,
			@PathVariable Long definicioProcesId,
			@Valid @ModelAttribute("command") Object command,
			BindingResult result, 
			SessionStatus status, 
			Model model) {
		EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
		ExpedientTipusDto expedientTipus = dissenyService.getExpedientTipusById(expedientTipusId);
		ExpedientTascaDto tasca = obtenirTascaInicial(entorn.getId(), expedientTipusId, definicioProcesId, new HashMap<String, Object>(), request);
		List<TascaDadaDto> tascaDades = tascaService.findDadesPerTascaDto(tasca);
		
		Map<String, Object> valors = TascaFormHelper.getValorsFromCommand(
				tascaDades,
				command,
				false,
				true);
		
		reproService.create(expedientTipus.getId(), nom, valors);
		
		return modalUrlTancar();
	}
	
	private ExpedientTascaDto obtenirTascaInicial(Long entornId, Long expedientTipusId, Long definicioProcesId, Map<String, Object> valors, HttpServletRequest request) {
		ExpedientTascaDto tasca = expedientService.getStartTask(entornId, expedientTipusId, definicioProcesId, valors);
		tasca.setId((String) request.getSession().getAttribute(ExpedientIniciController.CLAU_SESSIO_TASKID));
		Object validat = request.getSession().getAttribute(ExpedientIniciController.CLAU_SESSIO_FORM_VALIDAT);
		tasca.setValidada(validat != null);
		return tasca;
	}

	private static final Log logger = LogFactory.getLog(ReproController.class);
}
