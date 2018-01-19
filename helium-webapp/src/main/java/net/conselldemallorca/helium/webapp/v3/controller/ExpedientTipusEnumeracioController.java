package net.conselldemallorca.helium.webapp.v3.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.EnumeracioDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.service.EnumeracioService;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusEnumeracioCommand;
import net.conselldemallorca.helium.webapp.v3.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper.DatatablesResponse;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.NodecoHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;

/**
 * Controlador per a la pipella de enumeracions del tipus d'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller(value = "expedientTipusEnumeracioControllerV3")
@RequestMapping("/v3/expedientTipus")
public class ExpedientTipusEnumeracioController extends BaseExpedientTipusController {

	@Autowired
	protected EnumeracioService enumeracioService;

	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	
	@RequestMapping(value = "/{expedientTipusId}/enumeracions")
	public String enumeracions(HttpServletRequest request, @PathVariable Long expedientTipusId, Model model) {
		if (!NodecoHelper.isNodeco(request)) {
			return mostrarInformacioExpedientTipusPerPipelles(request, expedientTipusId, model, "enumeracions");
		}
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		if (entornActual != null) {
			ExpedientTipusDto expedientTipus = expedientTipusService.findAmbIdPermisDissenyarDelegat(
					entornActual.getId(),
					expedientTipusId);
			model.addAttribute("expedientTipus", expedientTipus);
			model.addAttribute("pipellaActiva", "enumeracions");
		}
		return "v3/expedientTipusEnumeracio";
	}

	@RequestMapping(value = "/{expedientTipusId}/enumeracio/datatable", method = RequestMethod.GET)
	@ResponseBody
	DatatablesResponse datatable(HttpServletRequest request, @PathVariable Long expedientTipusId, Model model) {
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		PaginacioParamsDto paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request);
		return DatatablesHelper.getDatatableResponse(request, null, enumeracioService
				.findPerDatatable(
						entornActual.getId(),
						expedientTipusId, 
						false, // incloure globals
						paginacioParams.getFiltre(), 
						paginacioParams));
	}

	@RequestMapping(value = "/{expedientTipusId}/enumeracio/new", method = RequestMethod.GET)
	public String nova(HttpServletRequest request, @PathVariable Long expedientTipusId, Model model) {
		ExpedientTipusEnumeracioCommand command = new ExpedientTipusEnumeracioCommand();
		command.setExpedientTipusId(expedientTipusId);
		model.addAttribute("expedientTipusEnumeracioCommand", command);
		return "v3/expedientTipusEnumeracioForm";
	}

	@RequestMapping(value = "/{expedientTipusId}/enumeracio/new", method = RequestMethod.POST)
	public String novaPost(
			HttpServletRequest request, 
			@PathVariable Long expedientTipusId,
			@Validated(ExpedientTipusEnumeracioCommand.Creacio.class) ExpedientTipusEnumeracioCommand command,
			BindingResult bindingResult, Model model) {
		try {
			if (bindingResult.hasErrors()) {
				return "v3/expedientTipusEnumeracioForm";
			} else {
			
				EnumeracioDto dto = ExpedientTipusEnumeracioCommand.asEnumeracioDto(command);
				EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
				
				enumeracioService.create(entornActual.getId(), expedientTipusId, dto);
				
	    		MissatgesHelper.success(
						request, 
						getMessage(
								request, 
								"expedient.tipus.enumeracio.controller.creat"));
				return modalUrlTancar(false);
			}
		} catch (Exception ex) {
			logger.error("No s'ha pogut guardar l'enumeració", ex);
    		MissatgesHelper.error(
					request, 
					getMessage(
							request, 
							"expedient.tipus.enumeracio.controller.creat.error",
							new Object[] {ex.getLocalizedMessage()}));
			return "v3/expedientTipusEnumeracioForm";
	    }
	}

	@RequestMapping(value = "/{expedientTipusId}/enumeracio/{id}/update", method = RequestMethod.GET)
	public String modificar(
			HttpServletRequest request, 
			@PathVariable Long expedientTipusId, 
			@PathVariable Long id,
			Model model) {
		EnumeracioDto dto = enumeracioService.findAmbId(expedientTipusId, id);
		ExpedientTipusEnumeracioCommand command = conversioTipusHelper.convertir(dto, ExpedientTipusEnumeracioCommand.class);
		model.addAttribute("expedientTipusEnumeracioCommand", command);
		model.addAttribute("heretat", dto.isHeretat());
		return "v3/expedientTipusEnumeracioForm";
	}

	@RequestMapping(value = "/{expedientTipusId}/enumeracio/{id}/update", method = RequestMethod.POST)
	public String modificarPost(
			HttpServletRequest request, 
			@PathVariable Long expedientTipusId, 
			@PathVariable Long id,
			@Validated(ExpedientTipusEnumeracioCommand.Modificacio.class) ExpedientTipusEnumeracioCommand command,
			BindingResult bindingResult, Model model) {
		try {
			if (bindingResult.hasErrors()) {
	    		model.addAttribute("heretat", enumeracioService.findAmbId(expedientTipusId, id).isHeretat());
				return "v3/expedientTipusEnumeracioForm";
			} else {
				EnumeracioDto dto = ExpedientTipusEnumeracioCommand.asEnumeracioDto(command);
				enumeracioService.update(dto);
				
	    		MissatgesHelper.success(
						request, 
						getMessage(
								request, 
								"expedient.tipus.enumeracio.controller.modificat"));
				return modalUrlTancar(false);
			}
		} catch (Exception ex) {
			logger.error("No s'ha pogut guardar l'enumerat: " + id, ex);
    		MissatgesHelper.error(
					request, 
					getMessage(
							request, 
							"expedient.tipus.enumeracio.controller.modificat.error",
							new Object[] {ex.getLocalizedMessage()}));
			return "v3/expedientTipusEnumeracioForm";
	    }
	}

	@RequestMapping(value = "/{expedientTipusId}/enumeracio/{id}/delete", method = RequestMethod.GET)
	@ResponseBody
	public boolean delete(HttpServletRequest request, @PathVariable Long expedientTipusId, @PathVariable Long id,
			Model model) {

		try {
			enumeracioService.delete(id);
			MissatgesHelper.success(request, getMessage(request, "expedient.tipus.enumeracio.controller.eliminat"));
			return true;
		}catch (Exception ex) {
    		MissatgesHelper.error(
					request, 
					getMessage(
							request, 
							"expedient.tipus.enumeracio.controller.eliminat.error",
							new Object[] {ex.getLocalizedMessage()}));
			return false;
		}
	}
	
	private static final Log logger = LogFactory.getLog(ExpedientTipusEnumeracioController.class);
}
