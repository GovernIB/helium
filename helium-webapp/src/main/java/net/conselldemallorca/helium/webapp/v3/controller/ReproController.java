/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDadaDto;
import net.conselldemallorca.helium.v3.core.api.exception.TascaNotFoundException;
import net.conselldemallorca.helium.v3.core.api.service.DissenyService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.v3.core.api.service.ReproService;
import net.conselldemallorca.helium.v3.core.api.service.TascaService;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
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
	@Autowired
	private net.conselldemallorca.helium.core.model.service.TascaService tascaInicialService;
	
	@SuppressWarnings("unchecked")
	@ModelAttribute("command")
	protected Object populateCommand(
			HttpServletRequest request, 
			@PathVariable Long expedientTipusId,
			@PathVariable Long definicioProcesId,
			Model model) {
		try {
			Map<String, Object> campsAddicionals = new HashMap<String, Object>();
			Map<String, Class<?>> campsAddicionalsClasses = new HashMap<String, Class<?>>();
			EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
			ExpedientTascaDto tasca = obtenirTascaInicial(
					entorn.getId(),
					expedientTipusId,
					definicioProcesId,
					new HashMap<String, Object>(),
					request);
			campsAddicionals.put("id", tasca.getId());
			campsAddicionals.put("entornId", entorn.getId());
			campsAddicionals.put("expedientTipusId", expedientTipusId);
			campsAddicionals.put("definicioProcesId", definicioProcesId);
			campsAddicionalsClasses.put("id", String.class);
			campsAddicionalsClasses.put("entornId", Long.class);
			campsAddicionalsClasses.put("expedientTipusId", Long.class);
			campsAddicionalsClasses.put("definicioProcesId", Long.class);
			Map<String, Object> valorsFormulariExtern = null;
			if (tasca.isFormExtern()) {
				valorsFormulariExtern = tascaInicialService.obtenirValorsFormulariExternInicial(tasca.getId());
				if (valorsFormulariExtern != null) {
					request.getSession().setAttribute(
							ExpedientIniciController.CLAU_SESSIO_FORM_VALORS,
							valorsFormulariExtern);
				} else {
					valorsFormulariExtern = (Map<String, Object>)request.getSession().getAttribute(
							ExpedientIniciController.CLAU_SESSIO_FORM_VALORS);
				}
			}
			return TascaFormHelper.getCommandForCamps(
					tascaService.findDadesPerTascaDto(tasca),
					valorsFormulariExtern,
					campsAddicionals,
					campsAddicionalsClasses,
					false);
		} catch (TascaNotFoundException ex) {
			MissatgesHelper.error(request, ex.getMessage());
			logger.error("No s'han pogut encontrar la tasca: " + ex.getMessage(), ex);
		}
		return null;
	}

	@RequestMapping(value = "/{expedientTipusId}/guardarRepro/{definicioProcesId}", method = RequestMethod.POST)
	public String guardarRepro(
			HttpServletRequest request, 
			@PathVariable Long expedientTipusId,
			@PathVariable Long definicioProcesId,
			@ModelAttribute("command") Object command,
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
		
//		reproService.create(expedientTipus.getId(), nom, valors);
		
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
