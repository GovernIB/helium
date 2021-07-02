package net.conselldemallorca.helium.webapp.v3.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.helium.logic.intf.dto.CarrecJbpmIdDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto;
import es.caib.helium.logic.intf.dto.SexeDto;
import es.caib.helium.logic.intf.dto.PersonaDto.Sexe;
import es.caib.helium.logic.intf.service.CarrecService;
import net.conselldemallorca.helium.webapp.v3.command.CarrecCommand;
import net.conselldemallorca.helium.webapp.v3.command.CarrecCommand.Creacio;
import net.conselldemallorca.helium.webapp.v3.command.CarrecCommand.Modificacio;
import net.conselldemallorca.helium.webapp.v3.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper.DatatablesResponse;

/**
 * Controlador per a la gestió de càrrecs
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */

@Controller(value = "carrecControllerV3")
@RequestMapping("/v3/carrec")
public class CarrecController extends BaseController {
	
	@Autowired
	private CarrecService carrecService;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	
	@RequestMapping(method = RequestMethod.GET)
	public String llistat(
			HttpServletRequest request,
			Model model) {
		return "v3/carrecPipelles";
	}
	
	@RequestMapping(value = "/configurats", method = RequestMethod.GET)
	public String configurats(
			HttpServletRequest request,
			Model model) {
		return "v3/carrecsConfigurats";
	}
	
	@RequestMapping(value="configurats/datatable", method = RequestMethod.GET)
	@ResponseBody
	DatatablesResponse datatable(
			HttpServletRequest request,
			Model model) {
		PaginacioParamsDto paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request);
		return DatatablesHelper.getDatatableResponse(
				request,
				null,
				carrecService.findConfigurats(paginacioParams));
	}
	

	@RequestMapping(value = "/sense/configurar", method = RequestMethod.GET)
	public String senseConfigurar(
			HttpServletRequest request,
			Model model) {
		return "v3/carrecsSenseConfigurar";
	}

	@RequestMapping(value = "/sense/configurar/datatable", method = RequestMethod.GET)
	@ResponseBody
	DatatablesResponse datatableSenseConfigurar(
			HttpServletRequest request,
			Model model) {
		PaginacioParamsDto paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request);
		return DatatablesHelper.getDatatableResponse(
				request,
				null,
				carrecService.findSenseConfigurar(paginacioParams));
	}
	
	private void prepararSexe(Model model) {
		List<SexeDto> sexes = new ArrayList<SexeDto>();
		SexeDto sexe = new SexeDto();
		sexe.setId(0);
		sexe.setNom(Sexe.SEXE_HOME.name());
		sexes.add(sexe);
		sexe = new SexeDto();
		sexe.setId(1);
		sexe.setNom(Sexe.SEXE_DONA.name());
		sexes.add(sexe);
		model.addAttribute("sexes", sexes);
	}
 	
	@RequestMapping(value = "{codi}/{grup}/new", method = RequestMethod.GET)
	public String newGet(HttpServletRequest request, 
			@PathVariable String codi,
			@PathVariable String grup,
			Model model) {
		
		prepararSexe(model);
		CarrecCommand command = new CarrecCommand();
		command.setCodi(codi);
		command.setGrup(grup);
		model.addAttribute("carrecCommand", command);
		return "v3/carrecConfigurarForm";
	}
	
	@RequestMapping(value = "{codi}/{grup}/new", method = RequestMethod.POST)
	public String newPost(HttpServletRequest request, @Validated(Creacio.class) CarrecCommand command,
			BindingResult bindingResult, Model model) {
		
		if (bindingResult.hasErrors()) {
			return "v3/carrecConfigurarForm";
		}
		
		carrecService.create(conversioTipusHelper.convertir(command, CarrecJbpmIdDto.class));
		return getModalControllerReturnValueSuccess(request, "redirect:/v3/carrec", "carrec.controller.creat");
	}
	
	@RequestMapping(value = "/{id}/update", method = RequestMethod.GET)
	public String modificar(HttpServletRequest request, @PathVariable Long id, Model model) {

		CarrecJbpmIdDto dto = carrecService.findAmbId(id);
		CarrecCommand command = conversioTipusHelper.convertir(dto, CarrecCommand.class);
		command.setPersonaSexeId(dto.getPersonaSexe().ordinal() == 0 ? 0l : 1l);
		prepararSexe(model);
		model.addAttribute("carrecCommand", command);
		return "v3/carrecConfigurarForm";
	}
	
	@RequestMapping(value = "/{id}/update", method = RequestMethod.POST)
	public String modificarPost(
			HttpServletRequest request,
			@PathVariable Long id,
			@Validated(Modificacio.class) CarrecCommand command,
			BindingResult bindingResult,
			Model model) {
        if (bindingResult.hasErrors()) {
        	return "v3/carrecConfigurarForm";
        } 
        
    	carrecService.update(conversioTipusHelper.convertir(command, CarrecJbpmIdDto.class));
		return getModalControllerReturnValueSuccess(request, "redirect:/v3/carrec", "carrec.controller.modificat");
	}
	
	
	@RequestMapping(value ="{carrecId}/delete", method = RequestMethod.GET)
	public String delete(HttpServletRequest request,
			@PathVariable Long carrecId,
			Model model) {
		carrecService.delete(carrecId);
		return this.getAjaxControllerReturnValueSuccess(
				request,
				"redirect:/v3/carrec",
				"carrec.controller.esborrat");
	}
}
