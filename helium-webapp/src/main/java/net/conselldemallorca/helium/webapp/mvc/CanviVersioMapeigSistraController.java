/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.MapeigSistra;
import net.conselldemallorca.helium.core.model.service.DissenyService;
import net.conselldemallorca.helium.webapp.mvc.util.BaseController;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;



/**
 * Controlador per la gestió de la integració entre el tipus
 * d'expedient i Sistra
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
public class CanviVersioMapeigSistraController extends BaseController {

	private DissenyService dissenyService;

	@Autowired
	public CanviVersioMapeigSistraController(
			DissenyService dissenyService) {
		this.dissenyService = dissenyService;
	}
	
	@RequestMapping(value = "/canviVersioMapeig", method = RequestMethod.GET)
	public String formGet(
			HttpServletRequest request,
			ModelMap model) {
		
		model.addAttribute("mapeigSistraBuit", dissenyService.findMapeigSistraTots().size() == 0);
		
		return "canviVersioMapeig";
	}

	@RequestMapping(value = "/canviVersioMapeig", method = RequestMethod.POST)
	public String formPost(
			HttpServletRequest request,
			@RequestParam(value = "submit", required = false) String submit,
			@ModelAttribute("command") ExpedientTipusSistraCommand command,
			BindingResult result,
			SessionStatus status,
			ModelMap model) {

		if ("submit".equals(submit) || submit.length() == 0) {
			List<ExpedientTipus> expedientsTipus = dissenyService.findExpedientTipusTots();
			
			try{
				for (ExpedientTipus expedientTipus : expedientsTipus){
					if (expedientTipus.getSistraTramitMapeigCamps() != null){
						mapeigSistraCanviVersioVariables(expedientTipus);
					}
					if (expedientTipus.getSistraTramitMapeigDocuments() != null){
						mapeigSistraCanviVersioDocuments(expedientTipus);
					}
					if (expedientTipus.getSistraTramitMapeigAdjunts() != null){
						mapeigSistraCanviVersioAdjunts(expedientTipus);
					}			
				}
				
				missatgeInfo(request, getMessage("info.mapeigSistra.canviVersio.guardat") );
				status.setComplete();
				
			} catch (Exception ex) {
				missatgeError(request, getMessage("error.mapeigSistra.canviVersio") );
				logger.error("No s'ha pogut realitzar el canvi de versió del Mapeig de sistra", ex);
			}
			
		}
		return "redirect:/canviVersioMapeig.html";
	}
			
	private void mapeigSistraCanviVersioVariables(ExpedientTipus expedientTipus){
		String[] parts = expedientTipus.getSistraTramitMapeigCamps().split(";");
		for (int i = 0; i < parts.length; i++) {
			String[] parella = parts[i].split(":");
			if (parella.length > 1) {
				String varSistra = parella[0];
				String varHelium = parella[1];
				if (varHelium != null && (!"".equalsIgnoreCase(varHelium))){
					if (dissenyService.findMapeigSistraAmbExpedientTipusICodi(expedientTipus.getId(), varHelium) == null){
						dissenyService.createMapeigSistra(varHelium, varSistra, MapeigSistra.TipusMapeig.Variable, expedientTipus);
					}
				}
			}
		}
	}
	
	private void mapeigSistraCanviVersioDocuments(ExpedientTipus expedientTipus){
		String[] parts = expedientTipus.getSistraTramitMapeigDocuments().split(";");
		for (int i = 0; i < parts.length; i++) {
			String[] parella = parts[i].split(":");
			if (parella.length > 1) {
				String varSistra = parella[0];
				String varHelium = parella[1];
				if (varHelium != null && (!"".equalsIgnoreCase(varHelium))){
					if (dissenyService.findMapeigSistraAmbExpedientTipusICodi(expedientTipus.getId(), varHelium) == null){
						dissenyService.createMapeigSistra(varHelium, varSistra, MapeigSistra.TipusMapeig.Document, expedientTipus);
					}
				}
			}
		}
	}
	
	private void mapeigSistraCanviVersioAdjunts(ExpedientTipus expedientTipus){
		String[] parts = expedientTipus.getSistraTramitMapeigAdjunts().split(";");
		for (int i = 0; i < parts.length; i++) {
			String varSistra = parts[i];
				
			if (varSistra != null && (!"".equalsIgnoreCase(varSistra))){
				if (dissenyService.findMapeigSistraAmbExpedientTipusICodi(expedientTipus.getId(), varSistra) == null){
					dissenyService.createMapeigSistra(varSistra, varSistra, MapeigSistra.TipusMapeig.Adjunt, expedientTipus);
				}
			}
		}
	}
	
		
	private static final Log logger = LogFactory.getLog(CanviVersioMapeigSistraController.class);

}
