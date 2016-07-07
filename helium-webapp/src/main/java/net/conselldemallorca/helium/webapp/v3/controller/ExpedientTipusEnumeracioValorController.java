package net.conselldemallorca.helium.webapp.v3.controller;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusEnumeracioDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusEnumeracioValorDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.exception.ValidacioException;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusEnumeracioValorCommand;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper.DatatablesResponse;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;

/**
 * Controlador per a la pipella de variables del tipus d'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller(value = "expedientTipusEnumeracioValorControllerV3")
@RequestMapping("/v3/expedientTipus")
public class ExpedientTipusEnumeracioValorController extends BaseExpedientTipusController {
	
	@RequestMapping(value = "/{expedientTipusId}/enumeracio/{enumeracioId}/valors", method = RequestMethod.GET)
	public String valors(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long enumeracioId,
			Model model) {
		
//		if (!NodecoHelper.isNodeco(request)) {
//			return mostrarInformacioExpedientTipusPerPipelles(request, expedientTipusId, model, "enumeracions");
//		}
		
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		
		if (entornActual != null) {
			
			ExpedientTipusDto expedientTipus = expedientTipusService.findAmbIdPermisDissenyar(entornActual.getId(),	expedientTipusId);
			model.addAttribute("expedientTipus", expedientTipus);
			
			ExpedientTipusEnumeracioDto enumeracio = expedientTipusService.enumeracioFindAmbId(enumeracioId);
			model.addAttribute("enumeracio", enumeracio);
			
			ExpedientTipusEnumeracioValorCommand command = new ExpedientTipusEnumeracioValorCommand();
			command.setExpedientTipusId(expedientTipusId);
			command.setEnumeracioId(enumeracioId);
			model.addAttribute("expedientTipusEnumeracioValorCommand", command);
		}
		return "v3/expedientTipusEnumeracioValors";
	}

	@RequestMapping(value = "/{expedientTipusId}/enumeracio/{enumeracioId}/valors/datatable", method = RequestMethod.GET)
	@ResponseBody
	DatatablesResponse datatable(
			HttpServletRequest request, 
			@PathVariable Long expedientTipusId,
			@PathVariable Long enumeracioId,
			Model model) {
		PaginacioParamsDto paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request);
		return DatatablesHelper.getDatatableResponse(request, null, expedientTipusService.enumeracioValorsFindPerDatatable(expedientTipusId, enumeracioId, paginacioParams.getFiltre(), paginacioParams));
	}

	@RequestMapping(value = "/{expedientTipusId}/enumeracio/{enumeracioId}/valor/{id}/get", method = RequestMethod.GET)
	@ResponseBody
	public String recupera(
			HttpServletRequest request, 
			@PathVariable Long expedientTipusId,
			@PathVariable Long enumeracioId,
			@PathVariable Long id,
			Model model) {
		ExpedientTipusEnumeracioValorDto dto = expedientTipusService.enumeracioValorFindAmbId(id);
		//ExpedientTipusEnumeracioValorCommand command = conversioTipusHelper.convertir(dto, ExpedientTipusEnumeracioValorCommand.class);
		return getObjectInJSON(dto);
	}
	
	@RequestMapping(value = "/{expedientTipusId}/enumeracio/{enumeracioId}/valor/{id}/delete", method = RequestMethod.GET)
	@ResponseBody
	public String delete(
			HttpServletRequest request, 
			@PathVariable Long expedientTipusId,
			@PathVariable Long enumeracioId,
			@PathVariable Long id,
			Model model) {

		try {
			expedientTipusService.enumeracioValorDelete(id);
			return "OK";
		}catch (ValidacioException ex) {
			return ex.getMessage();
		}
	}
	
	@RequestMapping(value = "/{expedientTipusId}/enumeracio/{enumeracioId}/valors/guardar", method = RequestMethod.POST)
	@ResponseBody
	public String guarda(
			HttpServletRequest request, 
			@PathVariable Long expedientTipusId,
			@PathVariable Long enumeracioId,
			Model model) {
		
		String codi = request.getParameter("codi");
		String nom  = request.getParameter("nom");
		String accio = request.getParameter("accio");
		String id  = request.getParameter("id");
		
		HashMap<String, String> errors = new HashMap<String, String>();
		
		if (codi==null || "".equals(codi) || "null".equalsIgnoreCase(codi)) {
			errors.put("codi", getMessage(request, "NotEmpty"));
		}else if (codi.length()>64){
			errors.put("codi", getMessage(request, "Size.java.lang.String", new Object[] { 64 }));
		}
		
		if (nom==null || "".equals(nom) || "null".equalsIgnoreCase(nom)) {
			errors.put("nom", getMessage(request, "NotEmpty"));
		}else if (codi.length()>255){
			errors.put("nom", getMessage(request, "Size.java.lang.String", new Object[] { 255 }));
		}
		
		if (errors.size()>0) { return getObjectInJSON(errors); }
		
		if ("nou".equals(accio)) {
			
			ExpedientTipusEnumeracioValorDto dto = new ExpedientTipusEnumeracioValorDto();
			dto.setCodi(codi);
			dto.setNom(nom);
			EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();			
			expedientTipusService.enumeracioValorsCreate(expedientTipusId, enumeracioId, entornActual.getId(), dto);
			
		}else{
			
			ExpedientTipusEnumeracioValorDto dto = expedientTipusService.enumeracioValorFindAmbId(Long.getLong(id));
			dto.setCodi(codi);
			dto.setNom(nom);
			expedientTipusService.enumeracioValorUpdate(dto);
		}
		
		return getObjectInJSON("OK");
	}
		
	private static final Log logger = LogFactory.getLog(ExpedientTipusDocumentController.class);
}
