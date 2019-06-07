/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import net.conselldemallorca.helium.v3.core.api.dto.InteressatDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientInteressatService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.webapp.v3.command.InteressatCommand;
import net.conselldemallorca.helium.webapp.v3.command.InteressatCommand.Creacio;
import net.conselldemallorca.helium.webapp.v3.command.InteressatCommand.Modificacio;
import net.conselldemallorca.helium.webapp.v3.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper.DatatablesResponse;

/**
 * Controlador per a la pàgina d'informació de l'termini.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedient")
public class ExpedientInteressatV3Controller extends BaseExpedientController {


	@Autowired
	private ExpedientInteressatService expedientInteressatService;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;


	@RequestMapping(value="/{expedientId}/interessat", method = RequestMethod.GET)
	public String llistat(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {
		
		model.addAttribute("expedientId", expedientId);	
		return "v3/interessatLlistat";
	}

	@RequestMapping(value="/{expedientId}/interessat/datatable", method = RequestMethod.GET)
	@ResponseBody
	DatatablesResponse datatable(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {
		PaginacioParamsDto paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request);
		
		return DatatablesHelper.getDatatableResponse(
				request,
				null,
				expedientInteressatService.findPerDatatable(
						expedientId,
						paginacioParams.getFiltre(),
						paginacioParams));
	}

	@RequestMapping(value = "/{expedientId}/interessat/new", method = RequestMethod.GET)
	public String newGet(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {
		model.addAttribute(new InteressatCommand());
		model.addAttribute("expedientId", expedientId);
		return "v3/interessatForm";
	}
	@RequestMapping(value = "/{expedientId}/interessat/new", method = RequestMethod.POST)
	public String newPost(
			HttpServletRequest request,
			@Validated(Creacio.class) InteressatCommand command,
			BindingResult bindingResult,
			Model model) {
        if (bindingResult.hasErrors()) {
        	return "v3/interessatForm";
        } else {
        	expedientInteressatService.create(
    				conversioTipusHelper.convertir(
    						command,
    						InteressatDto.class));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:/v3/interessat",
					"interessat.controller.creat");
        }
	}

	@RequestMapping(value = "/{expedientId}/interessat/{interessatId}/update", method = RequestMethod.GET)
	public String updateGet(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable Long interessatId,
			Model model) {
		InteressatDto dto = expedientInteressatService.findOne(
				interessatId);
		model.addAttribute(
				conversioTipusHelper.convertir(
						dto,
						InteressatCommand.class));
		return "v3/interessatForm";
	}
	@RequestMapping(value = "/{expedientId}/interessat/{interessatId}/update", method = RequestMethod.POST)
	public String updatePost(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable Long interessatId,
			@Validated(Modificacio.class) InteressatCommand command,
			BindingResult bindingResult,
			Model model) {
        if (bindingResult.hasErrors()) {
        	return "v3/interessatForm";
        } else {
        	expedientInteressatService.update(
        			conversioTipusHelper.convertir(
    						command,
    						InteressatDto.class));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:/v3/expedient/"+expedientId+"/interessat",
					"interessat.controller.modificat");
        }
	}

	@RequestMapping(value = "/{expedientId}/interessat/{interessatId}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable Long interessatId,
			Model model) {
		expedientInteressatService.delete(interessatId);
		return this.getAjaxControllerReturnValueSuccess(
				request,
				"redirect:/v3/expedient/"+expedientId,
				"interessat.controller.esborrat");
	}
	
	private static final Logger logger = LoggerFactory.getLogger(ExpedientInteressatV3Controller.class);

}
