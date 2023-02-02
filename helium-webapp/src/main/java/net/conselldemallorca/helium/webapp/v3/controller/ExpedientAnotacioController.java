/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import net.conselldemallorca.helium.v3.core.api.dto.AnotacioEstatEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.AnotacioFiltreDto;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.service.AnotacioService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper.DatatablesResponse;

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
	@Autowired
	private ExpedientService expedientService;

	@RequestMapping(value="/{expedientId}/anotacio", method = RequestMethod.GET)
	public String llistat(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {
		model.addAttribute("expedient",expedientService.findAmbId(expedientId));
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
	

	/** Mètode per retornar les dades pel datatable d'anotacions dins de la gestió de l'expedient. Filtra
	 * per expedientId i estat PROCESSADA.
	 * @param request
	 * @param expedientId
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/{expedientId}/anotacio/{anotacioId}", method = RequestMethod.GET)
	String reprocessar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable Long anotacioId,
			Model model) {
		
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		
		// cridar al servei d'anotacions per reprocessar mapeig
		try {
			anotacioService.reprocessarMapeigAnotacioExpedient(expedientId, anotacioId);
			MissatgesHelper.success(
					request, 
					getMessage(
							request, 
							"expedient.anotacio.llistat.processar.mapeig.ok"));        			

		
		} catch(Exception e) {
			String errMsg = this.getMessage(request, "expedient.anotacio.llistat.processar.mapeig.ko", new Object[] {e.getMessage()});
			logger.error(errMsg, e);
			MissatgesHelper.error(request, errMsg);
		}
 
		return "redirect:/v3/expedient/" + expedientId;
	}
	
	private static final Log logger = LogFactory.getLog(ExpedientAnotacioController.class);

}
