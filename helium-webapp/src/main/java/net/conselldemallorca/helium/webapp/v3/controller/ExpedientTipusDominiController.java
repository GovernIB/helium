/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

import net.conselldemallorca.helium.v3.core.api.dto.DominiDto;
import net.conselldemallorca.helium.v3.core.api.dto.DominiDto.OrigenCredencials;
import net.conselldemallorca.helium.v3.core.api.dto.DominiDto.TipusAuthDomini;
import net.conselldemallorca.helium.v3.core.api.dto.DominiDto.TipusDomini;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.ParellaCodiValorDto;
import net.conselldemallorca.helium.v3.core.api.service.DominiService;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusDominiCommand;
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
public class ExpedientTipusDominiController extends BaseExpedientTipusController {

	@Autowired
	protected DominiService dominiService;

	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	
	@ModelAttribute("tipusDominis")
	public List<ParellaCodiValorDto> populateTipusDominis() {
		List<ParellaCodiValorDto> resposta = new ArrayList<ParellaCodiValorDto>();
		for (TipusDomini tipus: TipusDomini.values()) {
			resposta.add(new ParellaCodiValorDto(tipus.name(), "expedient.tipus.domini.tipus." + tipus.name()));
		}
		
		return resposta;
	}
	
	@ModelAttribute("tipusAutenticacio")
	public List<ParellaCodiValorDto> populateTipusAutenticacio() {
		List<ParellaCodiValorDto> resposta = new ArrayList<ParellaCodiValorDto>();
		for (TipusAuthDomini tipus: TipusAuthDomini.values()) {
			resposta.add(new ParellaCodiValorDto(tipus.name(), "expedient.tipus.domini.tipus.auth." + tipus.name()));
		}
		return resposta;
	}
	
	@ModelAttribute("credencialsOrigen")
	public List<ParellaCodiValorDto> populateOrigenCredencials() {
		List<ParellaCodiValorDto> resposta = new ArrayList<ParellaCodiValorDto>();
		for (OrigenCredencials tipus: OrigenCredencials.values()) {
			resposta.add(new ParellaCodiValorDto(tipus.name(), "expedient.tipus.domini.origen." + tipus.name()));
		}
		return resposta;
	}
	
	@RequestMapping(value = "/{expedientTipusId}/dominis")
	public String dominis(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			Model model) {
		if (!NodecoHelper.isNodeco(request)) {
			return mostrarInformacioExpedientTipusPerPipelles(
					request,
					expedientTipusId,
					model,
					"dominis");
		}
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		if (entornActual != null) {
			ExpedientTipusDto expedientTipus = expedientTipusService.findAmbIdPermisDissenyar(
					entornActual.getId(),
					expedientTipusId);
			model.addAttribute("expedientTipus", expedientTipus);
		}
		return "v3/expedientTipusDomini";
	}

	@RequestMapping(value="/{expedientTipusId}/dominis/datatable", method = RequestMethod.GET)
	@ResponseBody
	DatatablesResponse datatable(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			Model model) {
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		PaginacioParamsDto paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request);
		return DatatablesHelper.getDatatableResponse(
				request,
				null,
				dominiService.findPerDatatable(
						entornActual.getId(),
						expedientTipusId,
						false, // incloure globals
						paginacioParams.getFiltre(),
						paginacioParams));		
	}
	
	@RequestMapping(value = "/{expedientTipusId}/domini/new", method = RequestMethod.GET)
	public String nou(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@RequestParam(required = false) Long agrupacioId,
			Model model) {
		ExpedientTipusDominiCommand command = new ExpedientTipusDominiCommand();
		command.setExpedientTipusId(expedientTipusId);
		model.addAttribute("expedientTipusId", expedientTipusId);
		model.addAttribute("expedientTipusDominiCommand", command);
		return "v3/expedientTipusDominiForm";
	}
	
	@RequestMapping(value = "/{expedientTipusId}/domini/new", method = RequestMethod.POST)
	public String nouPost(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@Validated(ExpedientTipusDominiCommand.Creacio.class) ExpedientTipusDominiCommand command,
			BindingResult bindingResult,
			Model model) {
        if (bindingResult.hasErrors()) {
        	model.addAttribute("expedientTipusId", expedientTipusId);
        	return "v3/expedientTipusDominiForm";
        } else {
        	// Verificar permisos
        	EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
        	dominiService.create(
        			entornActual.getId(),
    				expedientTipusId,
    				conversioTipusHelper.convertir(
    						command,
    						DominiDto.class));    		
    		MissatgesHelper.success(
					request, 
					getMessage(
							request, 
							"expedient.tipus.domini.controller.creat"));
			return modalUrlTancar(false);	
			
        }
	}

	@RequestMapping(value = "/{expedientTipusId}/domini/{id}/update", method = RequestMethod.GET)
	public String modificar(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long id,
			Model model) {
		DominiDto dto = dominiService.findAmbId(expedientTipusId, id);
		ExpedientTipusDominiCommand command = conversioTipusHelper.convertir(
				dto,
				ExpedientTipusDominiCommand.class);
		command.setExpedientTipusId(expedientTipusId);
		model.addAttribute("expedientTipusDominiCommand", command);
		model.addAttribute("expedientTipusId", expedientTipusId);
		model.addAttribute("heretat", dto.isHeretat());
		return "v3/expedientTipusDominiForm";
	}
	@RequestMapping(value = "/{expedientTipusId}/domini/{id}/update", method = RequestMethod.POST)
	public String modificarPost(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long id,
			@Validated(ExpedientTipusDominiCommand.Modificacio.class) ExpedientTipusDominiCommand command,
			BindingResult bindingResult,
			Model model) {
        if (bindingResult.hasErrors()) {
        	model.addAttribute("expedientTipusId", expedientTipusId);
    		model.addAttribute("heretat", dominiService.findAmbId(expedientTipusId, id).isHeretat());
       	return "v3/expedientTipusDominiForm";
        } else {
        	dominiService.update(
        			conversioTipusHelper.convertir(
    						command,
    						DominiDto.class));
        	MissatgesHelper.success(
					request, 
					getMessage(
							request, 
							"expedient.tipus.domini.controller.modificat"));
			return modalUrlTancar(false);
        }
	}
	
	@RequestMapping(value = "/{expedientTipusId}/domini/{id}/delete", method = RequestMethod.GET)
	@ResponseBody
	public boolean borrar(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long id,
			Model model) {
		try {
			dominiService.delete(id);
			
			MissatgesHelper.success(
					request,
					getMessage(
							request,
							"expedient.tipus.domini.controller.eliminat"));
			return true;
		} catch (Exception e) {
			logger.error("No s'ha pogut eliminar el nomini", e);
			MissatgesHelper.error(
					request, 
					getMessage(request, "expedient.tipus.domini.controller.eliminat.no"));
			return false;
		}
	}
	
	private static final Log logger = LogFactory.getLog(ExpedientTipusDominiController.class);
}
