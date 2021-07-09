/**
 * 
 */
package es.caib.helium.back.controller;

import es.caib.helium.back.helper.DatatablesHelper;
import es.caib.helium.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.helium.back.helper.SessionHelper;
import es.caib.helium.logic.intf.dto.AnotacioEstatEnumDto;
import es.caib.helium.logic.intf.dto.AnotacioFiltreDto;
import es.caib.helium.logic.intf.dto.EntornDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto;
import es.caib.helium.logic.intf.service.AnotacioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Controlador per a la pestanya d'anotacions de registre en la gestió dels expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedient")
public class ExpedientAnotacioController extends BaseExpedientController {


	@Autowired
	private AnotacioService anotacioService;

	@RequestMapping(value="/{expedientId}/anotacio", method = RequestMethod.GET)
	public String llistat(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {
		
		model.addAttribute("expedientId", expedientId);	
		return "v3/expedientAnotacioLlistat";
	}

	/** Mètode per retornar les dades pel datatable d'anotacions dins de la gestió de l'expedient. Filtra
	 * per expedientId i estat PROCESSADA.
	 * @param request
	 * @param expedientId
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/{expedientId}/anotacio/datatable", method = RequestMethod.GET)
	@ResponseBody
	DatatablesResponse datatable(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {
		
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		PaginacioParamsDto paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request);
		
		AnotacioFiltreDto filtreDto = new AnotacioFiltreDto();
		filtreDto.setExpedientId(expedientId);
		filtreDto.setEstat(AnotacioEstatEnumDto.PROCESSADA);
		
		return DatatablesHelper.getDatatableResponse(
				request,
				null,
				anotacioService.findAmbFiltrePaginat(entornActual.getId(), filtreDto, paginacioParams));
	}
	
}
