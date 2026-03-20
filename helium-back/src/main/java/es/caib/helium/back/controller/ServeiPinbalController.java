package es.caib.helium.back.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.helium.back.helper.DatatablesHelper;
import es.caib.helium.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.helium.back.helper.MissatgesHelper;
import es.caib.helium.commons.dto.PaginaDto;
import es.caib.helium.commons.dto.PaginacioParamsDto;
import es.caib.helium.commons.dto.ParellaCodiValorDto;
import es.caib.helium.commons.dto.ServeiPinbalDto;
import es.caib.helium.logic.intf.service.ConsultaPinbalService;
import es.caib.helium.service.helper.UsuariActualHelper;

@Controller
@RequestMapping("/v3/serveisPinbal")
public class ServeiPinbalController extends BaseController {

	@Autowired
	private ConsultaPinbalService consultaPinbalService;
	
	@RequestMapping(method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			Model model) {
		if (UsuariActualHelper.isAdministrador(SecurityContextHolder.getContext().getAuthentication())) {
			return "v3/serveiPinbalList";
		} else {
			MissatgesHelper.error(request, "Es requereix el rol d'administrador per consultar el llistat de serveis de PINBAL.");
			return "redirect:/";
		}
	}
	
	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(HttpServletRequest request) {
		
		Map<String, String[]> mapeigOrdenacions = new HashMap<String, String[]>();
		mapeigOrdenacions.put("documentsRestringits_str", new String[] {"pinbalServeiDocPermesDni", "pinbalServeiDocPermesNif", "pinbalServeiDocPermesCif", "pinbalServeiDocPermesNie", "pinbalServeiDocPermesPas"});
		
		PaginacioParamsDto paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request, null, mapeigOrdenacions);
		PaginaDto<ServeiPinbalDto> resultat = consultaPinbalService.findServeisPinbalAmbFiltrePaginat(paginacioParams);		
		return DatatablesHelper.getDatatableResponse(request, resultat);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public String editarGet(
			HttpServletRequest request, 
			@PathVariable Long id,
			Model model) {
		model.addAttribute("serveiPinbalDto", consultaPinbalService.findServeiPinbalById(id));
		List<ParellaCodiValorDto> llistaDocsPermesos = new ArrayList<ParellaCodiValorDto>();
		ParellaCodiValorDto dni = new ParellaCodiValorDto("DNI", "DNI");
		llistaDocsPermesos.add(dni);
		ParellaCodiValorDto nif = new ParellaCodiValorDto("NIF", "NIF");
		llistaDocsPermesos.add(nif);
		ParellaCodiValorDto nie = new ParellaCodiValorDto("NIE", "NIE");
		llistaDocsPermesos.add(nie);
		ParellaCodiValorDto cif = new ParellaCodiValorDto("CIF", "CIF");
		llistaDocsPermesos.add(cif);
		ParellaCodiValorDto pas = new ParellaCodiValorDto("Passaport", "Passaport");
		llistaDocsPermesos.add(pas);
		model.addAttribute("tipusDocsList", llistaDocsPermesos);
		return "v3/serveiPinbalForm";
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.POST)
	public String editarPost(
			HttpServletRequest request, 
			ServeiPinbalDto serveiPinbalDto,
			Model model) {
		try {
			consultaPinbalService.updateServeiPinbal(serveiPinbalDto);
			MissatgesHelper.success(request, getMessage(request, "serveisPinbal.form.modificar.ok", new Object[] {serveiPinbalDto.getNom()}));
		} catch (Exception ex) {
			MissatgesHelper.error(request, getMessage(
					request,
					"serveisPinbal.form.modificar.ko",
					new Object[] {serveiPinbalDto.getNom(), ex.getMessage()}),
					ex);
		}
		return modalUrlTancar(false);
	}
}
