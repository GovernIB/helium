/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import net.conselldemallorca.helium.v3.core.api.dto.CampAgrupacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.ParellaCodiValorDto;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusCampCommand;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusCampCommand.Creacio;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusCampCommand.Modificacio;
import net.conselldemallorca.helium.webapp.v3.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper.DatatablesResponse;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.NodecoHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;

/**
 * Controlador per a la pipella de variables del tipus d'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedientTipus")
public class ExpedientTipusVariableController extends BaseExpedientTipusController {

	@Autowired
	private ConversioTipusHelper conversioTipusHelper;

	@RequestMapping(value = "/{expedientTipusId}/variables")
	public String variables(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			Model model) {
		if (!NodecoHelper.isNodeco(request)) {
			return mostrarInformacioExpedientTipusPerPipelles(
					request,
					expedientTipusId,
					model,
					"variables");
		}
		omplirModelPipellaVariables(
				request,
				expedientTipusId,
				model);
		return "v3/expedientTipusVariable";
	}

	private void omplirModelPipellaVariables(
			HttpServletRequest request,
			Long expedientTipusId,
			Model model) {
		List<CampAgrupacioDto> agrupacions = null;
		// Mira si és la configuració antiga o la configuració nova
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		if (entornActual != null) {
			ExpedientTipusDto expedientTipus = expedientTipusService.findAmbIdPerDissenyar(
					entornActual.getId(),
					expedientTipusId);
			model.addAttribute("expedientTipus", expedientTipus);
			agrupacions = expedientTipusService.agrupacioFindAll(expedientTipus.getId());
		}
		model.addAttribute("agrupacions", agrupacions);		
	}
	
	
	@RequestMapping(value="/{expedientTipusId}/variable/datatable", method = RequestMethod.GET)
	@ResponseBody
	DatatablesResponse datatable(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			Model model) {
		PaginacioParamsDto paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request);
		return DatatablesHelper.getDatatableResponse(
				request,
				null,
				expedientTipusService.campFindPerDatatable(
						expedientTipusId,
						paginacioParams.getFiltre(),
						paginacioParams));
	}	
	
	@ModelAttribute("tipusCamp")
	public List<ParellaCodiValorDto> populateTipusCamp() {
		List<ParellaCodiValorDto> resposta = new ArrayList<ParellaCodiValorDto>();
		for (CampTipusDto campTipus : CampTipusDto.values()) {
			resposta.add(new ParellaCodiValorDto(campTipus.toString(), campTipus));
		}
		return resposta;

		
	}

	@RequestMapping(value = "/{expedientTipusId}/variable/new", method = RequestMethod.GET)
	public String nou(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			Model model) {
		ExpedientTipusCampCommand command = new ExpedientTipusCampCommand();
		command.setExpedientTipusId(expedientTipusId);
		model.addAttribute("expedientTipusCampCommand", command);
		return "v3/expedientTipusVariableForm";
	}
	@RequestMapping(value = "/{expedientTipusId}/variable/new", method = RequestMethod.POST)
	public String nouPost(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@Validated(Creacio.class) ExpedientTipusCampCommand command,
			BindingResult bindingResult,
			Model model) {
        if (bindingResult.hasErrors()) {
        	return "v3/expedientTipusVariableForm";
        } else {
        	// Verificar permisos
    		expedientTipusService.campCreate(
    				expedientTipusId,
    				conversioTipusHelper.convertir(
    						command,
    						CampDto.class));    		
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:/v3/expedientTipus/" + expedientTipusId + "#variables",
					"expedient.tipus.camp.controller.creat");
        }
	}

	@RequestMapping(value = "/{expedientTipusId}/variable/{id}/update", method = RequestMethod.GET)
	public String modificar(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long id,
			Model model) {
		CampDto dto = expedientTipusService.campFindAmbId(id);
		ExpedientTipusCampCommand command = conversioTipusHelper.convertir(
				dto,
				ExpedientTipusCampCommand.class);
		model.addAttribute("expedientTipusCampCommand", command);
		return "v3/expedientTipusVariableForm";
	}
	@RequestMapping(value = "/{expedientTipusId}/variable/{id}/update", method = RequestMethod.POST)
	public String modificarPost(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long id,
			@Validated(Modificacio.class) ExpedientTipusCampCommand command,
			BindingResult bindingResult,
			Model model) {
        if (bindingResult.hasErrors()) {
        	return "v3/expedientTipusVariableForm";
        } else {
        	expedientTipusService.campUpdate(
        			conversioTipusHelper.convertir(
    						command,
    						CampDto.class));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:/v3/expedientTipus/" + expedientTipusId + "#variables",
					"expedient.tipus.camp.controller.modificat");
        }
	}

	@RequestMapping(value = "/{expedientTipusId}/variable/{id}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long id,
			Model model) {
		
		expedientTipusService.campDelete(id);
		
		MissatgesHelper.success(
				request,
				getMessage(
						request,
						"expedient.tipus.camp.controller.eliminat"));
		
		return "redirect:/v3/expedientTipus/" + expedientTipusId + "#variables";
	}
	
}
