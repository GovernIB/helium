package net.conselldemallorca.helium.webapp.v3.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientTipusService;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper.DatatablesResponse;

/**
 * Controlador per al llistat de consultes per tipus d'expedient
 * des del menú principal.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller(value = "consultaControllerV3")
@RequestMapping("/v3/consulta")
public class ConsultaController extends BaseExpedientTipusController {

	@Autowired
	private ExpedientTipusService expedientTipusService;

	@RequestMapping(method = RequestMethod.GET)
	public String llistat(
			HttpServletRequest request,
			Model model) {
		return "v3/consultaLlistat";
	}

	@RequestMapping(value="/datatable", method = RequestMethod.GET)
	@ResponseBody
	DatatablesResponse datatable(
			HttpServletRequest request,
			Model model) {
		PaginacioParamsDto paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request);
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		return DatatablesHelper.getDatatableResponse(
				request,
				null,
				expedientTipusService.consultaFindPerDatatable(
						entornActual.getId(),
						null, //expedientTipusId
						paginacioParams.getFiltre(),
						paginacioParams),
				"id");
	}	

	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
	}
	
}
