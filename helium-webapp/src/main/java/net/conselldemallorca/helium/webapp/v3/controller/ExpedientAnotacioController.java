/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import net.conselldemallorca.helium.v3.core.api.dto.AnotacioEstatEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.AnotacioFiltreDto;
import net.conselldemallorca.helium.v3.core.api.dto.AnotacioMapeigResultatDto;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.ReprocessarMapeigAnotacioDto;
import net.conselldemallorca.helium.v3.core.api.service.AnotacioService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper.DatatablesResponse;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;

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
		List<ExpedientTipusDto> expedientTipusDtoAccessibles = (List<ExpedientTipusDto>)SessionHelper.getAttribute(
													request,
													SessionHelper.VARIABLE_EXPTIP_ACCESSIBLES);
		return DatatablesHelper.getDatatableResponse(
				request,
				null,
				anotacioService.findAmbFiltrePaginat(entornActual.getId(), expedientTipusDtoAccessibles, filtreDto, paginacioParams));
	}
	

	/** Mètode per retornar les dades pel datatable d'anotacions dins de la gestió de l'expedient. Filtra
	 * per expedientId i estat PROCESSADA.
	 * @param request
	 * @param expedientId
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/{expedientId}/anotacio/{anotacioId}/{nomesAnnexos}", method = RequestMethod.GET)
	String reprocessar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable Long anotacioId,
			@PathVariable boolean nomesAnnexos,
			Model model) {
		
		SessionHelper.getSessionManager(request).getEntornActual();
		
		// cridar al servei d'anotacions per reprocessar mapeig
		try {
			AnotacioMapeigResultatDto resultatMapeig = new AnotacioMapeigResultatDto();
			if(!nomesAnnexos) {
				resultatMapeig = anotacioService.reprocessarMapeigAnotacioExpedient(expedientId, anotacioId);
			} else {
				anotacioService.reintentarTraspasAnotacio(anotacioId);
				MissatgesHelper.success(
						request, 
						getMessage(
								request, 
								"expedient.anotacio.llistat.processar.traspas.ok"));      
			}
				
			// Si hi ha errors posa alertes, afegeix elements span i title per abreujar el missatge sense perdre informació.
			if (resultatMapeig.isError()) {
				this.missatgeErrorResultatMapeig(request, resultatMapeig);
			} else {
				MissatgesHelper.success(
						request, 
						getMessage(
								request, 
								"expedient.anotacio.llistat.processar.mapeig.ok"));        			
			}
		} catch(Exception e) {
			String errMsg = null;
			if(!nomesAnnexos)
				errMsg = this.getMessage(request, "expedient.anotacio.llistat.processar.mapeig.ko", new Object[] {e.getMessage()});
			else
				errMsg = this.getMessage(request, "expedient.anotacio.llistat.processar.traspas.ko", new Object[] {e.getMessage()});
			logger.error(errMsg, e);
			MissatgesHelper.error(request, errMsg);
		}
 
		return "redirect:/v3/expedient/" + expedientId;
	}
	
	/** Mostra els missatges d'error dels mapejos en un missatge. */
	private void missatgeErrorResultatMapeig(
			HttpServletRequest request, 
			AnotacioMapeigResultatDto resultatMapeig) 
	{
		StringBuilder errMsg = new StringBuilder();
		errMsg.append(resultatMapeig.getMissatgeAlerta()).append("<ul>");
		if (!resultatMapeig.getErrorsDades().isEmpty()) {
			errMsg.append("<li>Variables : <ul>");
			for (String clau : resultatMapeig.getErrorsDades().keySet()) {
				errMsg.append("<li>").append(clau).append(": ").append(resultatMapeig.getErrorsDades().get(clau)).append("</li>");
			}
			errMsg.append("<ul> </li>"); 
		}
		if (!resultatMapeig.getErrorsDocuments().isEmpty()) {
			errMsg.append("<li>Documents : <ul>");
			for (String clau : resultatMapeig.getErrorsDocuments().keySet()) {
				errMsg.append("<li>").append(clau).append(": ").append(resultatMapeig.getErrorsDocuments().get(clau)).append("</li>");
			}
			errMsg.append("<ul> </li>");
		}
		if (!resultatMapeig.getErrorsAdjunts().isEmpty()) {
			errMsg.append("<li>Adjunts : <ul>");
			for (String clau : resultatMapeig.getErrorsAdjunts().keySet()) {
				errMsg.append("<li>").append(clau).append(": ").append(resultatMapeig.getErrorsAdjunts().get(clau)).append("</li>");
			}
			errMsg.append("<ul> </li>");
		}
		errMsg.append("</ul>");
		MissatgesHelper.error(request, errMsg.toString());
	}

	/** Acció del menú desplegable d'Accions massives d'anotacions, per iniciar una tasca en segon pla per reprocessar el mapeig de les
	 * anotacions seleccionades a le taula d'anotacions (les que tenen un expedient associat es tornaria a aplicar el mapeig)
	 */
	@RequestMapping(value = "/{expedientId}/anotacio/{anotacioId}/{nomesAnnexos}/reprocessarMapeig", method = RequestMethod.GET)
	public String reprocessarMapeig(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable Long anotacioId,
			@PathVariable boolean nomesAnnexos,
			Model model) {
		ReprocessarMapeigAnotacioDto reprocessarMapeigAnotacioDto = new ReprocessarMapeigAnotacioDto();
		reprocessarMapeigAnotacioDto.setIdsAnotacions(new ArrayList<Long>(Arrays.asList(anotacioId)));
		model.addAttribute(reprocessarMapeigAnotacioDto);
		return "v3/reprocessarMapeigForm";
	}
	
	@RequestMapping(value = "/{expedientId}/anotacio/{anotacioId}/{nomesAnnexos}/reprocessarMapeig", method = RequestMethod.POST)
	public String reprocessarMapeigPost(
			HttpServletRequest request,
			@PathVariable Long anotacioId,
			@PathVariable Long expedientId,
			@PathVariable boolean nomesAnnexos,
			@ModelAttribute("reprocessarMapeigAnotacioDto") ReprocessarMapeigAnotacioDto reprocessarMapeigAnotacioDto,
			Model model) {
		
		try {
			AnotacioMapeigResultatDto resultatMapeig = 
				anotacioService.reprocessarMapeigAnotacioExpedient(
					expedientId,
					anotacioId,
					reprocessarMapeigAnotacioDto.isReprocessarMapeigVariables(),
					reprocessarMapeigAnotacioDto.isReprocessarMapeigDocuments(),
					reprocessarMapeigAnotacioDto.isReprocessarMapeigAdjunts(),
					reprocessarMapeigAnotacioDto.isReprocessarMapeigInteressats());
			if (resultatMapeig.isError()) {
				this.missatgeErrorResultatMapeig(request, resultatMapeig);
			} else {
				MissatgesHelper.success(
						request, 
						getMessage(
								request, 
								"expedient.anotacio.llistat.processar.mapeig.ok"));
				
			}
		}  catch(Exception e) {
			String errMsg = getMessage(
					request,
					"expedient.anotacio.llistat.processar.mapeig.ko",
					new Object[] {e.getMessage()});
			MissatgesHelper.error( request, errMsg);
			logger.error(errMsg, e);
		}		
		return modalUrlTancar(false);
	}
	
	private static final Log logger = LogFactory.getLog(ExpedientAnotacioController.class);

}
