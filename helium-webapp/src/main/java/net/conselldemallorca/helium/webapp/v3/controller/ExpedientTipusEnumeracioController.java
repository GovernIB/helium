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
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusEnumeracioDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.exception.ValidacioException;
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
	private ConversioTipusHelper conversioTipusHelper;
	
	@RequestMapping(value = "/{expedientTipusId}/enumeracions")
	public String enumeracions(HttpServletRequest request, @PathVariable Long expedientTipusId, Model model) {
		if (!NodecoHelper.isNodeco(request)) {
			return mostrarInformacioExpedientTipusPerPipelles(request, expedientTipusId, model, "enumeracions");
		}
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		if (entornActual != null) {
			ExpedientTipusDto expedientTipus = expedientTipusService.findAmbIdPermisDissenyar(
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
		PaginacioParamsDto paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request);
		return DatatablesHelper.getDatatableResponse(request, null, expedientTipusService
				.enumeracioFindPerDatatable(expedientTipusId, paginacioParams.getFiltre(), paginacioParams));
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
			
				ExpedientTipusEnumeracioDto dto = ExpedientTipusEnumeracioCommand.asExpedientTipusEnumeracioDto(command);
				EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
				
				expedientTipusService.enumeracioCreate(expedientTipusId, entornActual.getId(), dto);
				
	    		MissatgesHelper.success(
						request, 
						getMessage(
								request, 
								"expedient.tipus.enumeracio.controller.creat"));
				return modalUrlTancar(false);
			}
		} catch (Exception ex) {
			logger.error("No s'ha pogut guardar l'enumeració", ex);
			return "v3/expedientTipusEnumeracioForm";
	    }
	}

	@RequestMapping(value = "/{expedientTipusId}/enumeracio/{id}/update", method = RequestMethod.GET)
	public String modificar(
			HttpServletRequest request, 
			@PathVariable Long expedientTipusId, 
			@PathVariable Long id,
			Model model) {
		ExpedientTipusEnumeracioDto dto = expedientTipusService.enumeracioFindAmbId(id);
		ExpedientTipusEnumeracioCommand command = conversioTipusHelper.convertir(dto, ExpedientTipusEnumeracioCommand.class);
		model.addAttribute("expedientTipusEnumeracioCommand", command);
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
				return "v3/expedientTipusEnumeracioForm";
			} else {
				ExpedientTipusEnumeracioDto dto = ExpedientTipusEnumeracioCommand.asExpedientTipusEnumeracioDto(command);
				expedientTipusService.enumeracioUpdate(dto);
				
	    		MissatgesHelper.success(
						request, 
						getMessage(
								request, 
								"expedient.tipus.enumeracio.controller.modificat"));
				return modalUrlTancar(false);
			}
		} catch (Exception ex) {
			logger.error("No s'ha pogut guardar l'enumerat: " + id, ex);
			return "v3/expedientTipusEnumeracioForm";
	    }
	}

	@RequestMapping(value = "/{expedientTipusId}/enumeracio/{id}/delete", method = RequestMethod.GET)
	@ResponseBody
	public boolean delete(HttpServletRequest request, @PathVariable Long expedientTipusId, @PathVariable Long id,
			Model model) {

		try {
			expedientTipusService.enumeracioDelete(id);
			MissatgesHelper.success(request, getMessage(request, "expedient.tipus.enumeracio.controller.eliminat"));
			return true;
		}catch (ValidacioException ex) {
			MissatgesHelper.error(request, ex.getMessage());
			return false;
		}
	}
	
	private static final Log logger = LogFactory.getLog(ExpedientTipusDocumentController.class);
}
