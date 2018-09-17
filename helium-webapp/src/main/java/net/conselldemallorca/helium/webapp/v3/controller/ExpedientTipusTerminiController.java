/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.ParellaCodiValorDto;
import net.conselldemallorca.helium.v3.core.api.dto.TerminiDto;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusTerminiCommand;
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
public class ExpedientTipusTerminiController extends BaseExpedientTipusController {

	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	
	@ModelAttribute("listTerminis")
	public List<ParellaCodiValorDto> populateValorTerminis() {
		List<ParellaCodiValorDto> resposta = new ArrayList<ParellaCodiValorDto>();
		for (int i = 0; i <= 12; i++) {
			resposta.add(new ParellaCodiValorDto(Integer.toString(i), i));
		}
		return resposta;
	}
	
	@RequestMapping(value = "/{expedientTipusId}/terminis")
	public String terminis(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			Model model) {
		if (!NodecoHelper.isNodeco(request)) {
			return mostrarInformacioExpedientTipusPerPipelles(
					request,
					expedientTipusId,
					model,
					"terminis");
		}
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		if (entornActual != null) {
			ExpedientTipusDto expedientTipus = expedientTipusService.findAmbIdPermisDissenyar(
					entornActual.getId(),
					expedientTipusId);
			model.addAttribute("expedientTipus", expedientTipus);
			model.addAttribute("baseUrl", expedientTipus.getId());
		}

		return "v3/expedientTipusTermini";
	}

	@RequestMapping(value="/{expedientTipusId}/termini/datatable", method = RequestMethod.GET)
	@ResponseBody
	DatatablesResponse datatable(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			Model model) {
		Map<String, String[]> mapeigFiltres = new HashMap<String, String[]>();
		mapeigFiltres.put("durada", new String[] {"anys", "mesos", "dies"});
		PaginacioParamsDto paginacioParams = 
				DatatablesHelper.getPaginacioDtoFromRequest(
						request,
						null,
						mapeigFiltres);
		return DatatablesHelper.getDatatableResponse(
				request,
				null,
				terminiService.findPerDatatable(
						expedientTipusId,
						null,
						paginacioParams.getFiltre(),
						paginacioParams));		
	}
	
	@RequestMapping(value = "/{expedientTipusId}/termini/new", method = RequestMethod.GET)
	public String nou(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@RequestParam(required = false) Long agrupacioId,
			Model model) {
		ExpedientTipusTerminiCommand command = new ExpedientTipusTerminiCommand();
		model.addAttribute("expedientTipusTerminiCommand", command);
		return "v3/expedientTipusTerminiForm";
	}
	
	@RequestMapping(value = "/{expedientTipusId}/termini/new", method = RequestMethod.POST)
	public String nouPost(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@Validated(ExpedientTipusTerminiCommand.Creacio.class) ExpedientTipusTerminiCommand command,
			BindingResult bindingResult,
			Model model) {
        if (bindingResult.hasErrors()) {
        	model.addAttribute("expedientTipusId", expedientTipusId);
        	return "v3/expedientTipusTerminiForm";
        } else {
        	// Verificar permisos
    		terminiService.create(
    				expedientTipusId,
    				null,
    				conversioTipusHelper.convertir(
    						command,
    						TerminiDto.class));    		
    		MissatgesHelper.success(
					request, 
					getMessage(
							request, 
							"expedient.tipus.termini.controller.creat"));
			return modalUrlTancar(false);	
			
        }
	}

	@RequestMapping(value = "/{expedientTipusId}/termini/{id}/update", method = RequestMethod.GET)
	public String modificar(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long id,
			Model model) {
		TerminiDto dto = terminiService.findAmbId(expedientTipusId, id);
		ExpedientTipusTerminiCommand command = conversioTipusHelper.convertir(
				dto,
				ExpedientTipusTerminiCommand.class);
		model.addAttribute("expedientTipusTerminiCommand", command);
		model.addAttribute("expedientTipusId", expedientTipusId);
		model.addAttribute("heretat", dto.isHeretat());
		return "v3/expedientTipusTerminiForm";
	}
	@RequestMapping(value = "/{expedientTipusId}/termini/{id}/update", method = RequestMethod.POST)
	public String modificarPost(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long id,
			@Validated(ExpedientTipusTerminiCommand.Modificacio.class) ExpedientTipusTerminiCommand command,
			BindingResult bindingResult,
			Model model) {
        if (bindingResult.hasErrors()) {
        	model.addAttribute("expedientTipusId", expedientTipusId);
    		model.addAttribute("heretat", terminiService.findAmbId(expedientTipusId, id).isHeretat());
        	return "v3/expedientTipusTerminiForm";
        } else {
        	terminiService.update(
        			conversioTipusHelper.convertir(
    						command,
    						TerminiDto.class));
        	MissatgesHelper.success(
					request, 
					getMessage(
							request, 
							"expedient.tipus.termini.controller.modificat"));
			return modalUrlTancar(false);
        }
	}
	
	@RequestMapping(value = "/{expedientTipusId}/termini/{id}/delete", method = RequestMethod.GET)
	@ResponseBody
	public boolean borrar(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long id,
			Model model) {
		try {
			terminiService.delete(id);
			
			MissatgesHelper.success(
					request,
					getMessage(
							request,
							"expedient.tipus.termini.controller.eliminat"));
			return true;
		} catch (Exception e) {
			MissatgesHelper.error(
					request, 
					getMessage(request, "expedient.tipus.termini.controller.eliminat"));
			return false;
		}
	}
}
