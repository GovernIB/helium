/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.MapeigSistra;
import net.conselldemallorca.helium.core.model.hibernate.MapeigSistra.TipusMapeig;
import net.conselldemallorca.helium.core.model.service.DissenyService;
import net.conselldemallorca.helium.core.model.service.PermissionService;
import net.conselldemallorca.helium.core.security.ExtendedPermission;
import net.conselldemallorca.helium.webapp.mvc.util.BaseController;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.Permission;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
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
public class ExpedientTipusSistraController extends BaseController {

	private DissenyService dissenyService;
	private PermissionService permissionService;

	@Autowired
	public ExpedientTipusSistraController(
			DissenyService dissenyService,
			PermissionService permissionService) {
		this.dissenyService = dissenyService;
		this.permissionService = permissionService;
	}
	
	@ModelAttribute("tipusMapeig")
	public TipusMapeig[] populateTipusMapeig() {
		return MapeigSistra.TipusMapeig.values();
	}
	
	@ModelAttribute("command")
	public ExpedientTipusSistraCommand populateCommand(
			HttpServletRequest request,
			@RequestParam(value = "expedientTipusId", required = true) Long expedientTipusId) {
		ExpedientTipusSistraCommand command = new ExpedientTipusSistraCommand();
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientTipus expedientTipus = dissenyService.getExpedientTipusById(expedientTipusId);
			command.setExpedientTipusId(expedientTipusId);
			if (expedientTipus.getSistraTramitCodi()!=null || expedientTipus.getSistraTramitCodi()!=null) 
				command.setCodiTramit(expedientTipus.getSistraTramitCodi());
			
			if (expedientTipus.isSistraActiu())
				command.setActiu(true);

			else
				command.setActiu(false);
			
			
			command.setNotificacionsActivades(expedientTipus.isNotificacionsActivades());
			command.setNotificacioOrganCodi(expedientTipus.getNotificacioOrganCodi());
			command.setNotificacioOficinaCodi(expedientTipus.getNotificacioOficinaCodi());
			command.setNotificacioUnitatAdministrativa(expedientTipus.getNotificacioUnitatAdministrativa());
			command.setNotificacioCodiProcediment(expedientTipus.getNotificacioCodiProcediment());
			command.setNotificacioAvisTitol(expedientTipus.getNotificacioAvisTitol());
			command.setNotificacioAvisText(expedientTipus.getNotificacioAvisText());
			command.setNotificacioAvisTextSms(expedientTipus.getNotificacioAvisTextSms());
			command.setNotificacioOficiTitol(expedientTipus.getNotificacioOficiTitol());
			command.setNotificacioOficiText(expedientTipus.getNotificacioOficiText());
		}
		return command;
	}
	
	@ModelAttribute("expedientTipus")
	public ExpedientTipus populateExpedientTipus(
			@RequestParam(value = "expedientTipusId", required = true) Long expedientTipusId) {
		return dissenyService.getExpedientTipusById(expedientTipusId);
	}

	@RequestMapping(value = "/expedientTipus/sistra", method = RequestMethod.GET)
	public String formGet(
			HttpServletRequest request,
			@RequestParam(value = "expedientTipusId", required = true) Long expedientTipusId,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientTipus expedientTipus = dissenyService.getExpedientTipusById(expedientTipusId);
			if (potDissenyarExpedientTipus(entorn, expedientTipus)) {
				model.addAttribute("numMapeigVariables", dissenyService.findMapeigSistraVariablesAmbExpedientTipus(expedientTipusId).size());
				model.addAttribute("numMapeigDocuments", dissenyService.findMapeigSistraDocumentsAmbExpedientTipus(expedientTipusId).size());
				model.addAttribute("numMapeigAdjunts",   dissenyService.findMapeigSistraAdjuntsAmbExpedientTipus(expedientTipusId).size());
				
				return "expedientTipus/sistra";
			} else {
				missatgeError(request, getMessage("error.permisos.disseny.tipus.exp"));
				return "redirect:/index.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/expedientTipus/sistra", method = RequestMethod.POST)
	public String formPost(
			HttpServletRequest request,
			@RequestParam(value = "submit", required = false) String submit,
			@RequestParam(value = "expedientTipusId", required = true) Long expedientTipusId,
			@ModelAttribute("command") ExpedientTipusSistraCommand command,
			BindingResult result,
			SessionStatus status,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientTipus expedientTipus = dissenyService.getExpedientTipusById(expedientTipusId);
			if (potDissenyarExpedientTipus(entorn, expedientTipus)) {
				if ("submit".equals(submit) || submit.length() == 0) {
					new ExpedientTipusSistraValidator(dissenyService).validate(command, result);
			        if (result.hasErrors()) {
			        	model.addAttribute("numMapeigVariables", dissenyService.findMapeigSistraVariablesAmbExpedientTipus(expedientTipusId).size());
						model.addAttribute("numMapeigDocuments", dissenyService.findMapeigSistraDocumentsAmbExpedientTipus(expedientTipusId).size());
						model.addAttribute("numMapeigAdjunts",   dissenyService.findMapeigSistraAdjuntsAmbExpedientTipus(expedientTipusId).size());
						
			        	return "expedientTipus/sistra";
			        }
			        try {
			        	if (command.isActiu()) {
			        		expedientTipus.setSistraTramitCodi(command.getCodiTramit());
			        	} else {
			        		expedientTipus.setSistraTramitCodi(null);
			        	}
			        	expedientTipus.setNotificacionsActivades(command.isNotificacionsActivades());
			        	expedientTipus.setNotificacioOrganCodi(command.getNotificacioOrganCodi());
			        	expedientTipus.setNotificacioOficinaCodi(command.getNotificacioOficinaCodi());
			        	expedientTipus.setNotificacioUnitatAdministrativa(command.getNotificacioUnitatAdministrativa());
			        	expedientTipus.setNotificacioCodiProcediment(command.getNotificacioCodiProcediment());
			        	expedientTipus.setNotificacioAvisTitol(command.getNotificacioAvisTitol());
			        	expedientTipus.setNotificacioAvisText(command.getNotificacioAvisText());
						expedientTipus.setNotificacioAvisTextSms(command.getNotificacioAvisTextSms());
						expedientTipus.setNotificacioOficiTitol(command.getNotificacioOficiTitol());
						expedientTipus.setNotificacioOficiText(command.getNotificacioOficiText());
						
		        		dissenyService.updateExpedientTipus(expedientTipus);
			        	missatgeInfo(request, getMessage("info.informacio.guardat"));
			        	status.setComplete();
			        } catch (Exception ex) {
			        	missatgeError(request, getMessage("error.proces.peticio"), ex.getLocalizedMessage());
			        	logger.error("No s'ha pogut guardar el registre", ex);
			        }
				}
				return "redirect:/expedientTipus/sistra.html?expedientTipusId=" + expedientTipusId;
			} else {
				missatgeError(request, getMessage("error.permisos.disseny.tipus.exp"));
				return "redirect:/index.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	/*@RequestMapping(value = "/expedientTipus/mapeigSistraEsborrar.html")
	public String deleteAction(
			HttpServletRequest request,
			@RequestParam(value = "expedientTipusId", required = true) Long expedientTipusId,
			@RequestParam(value = "id", required = true) Long id) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientTipus expedientTipus = dissenyService.getExpedientTipusById(expedientTipusId);
			if (potDissenyarExpedientTipus(entorn, expedientTipus)) {
				dissenyService.deleteMapeigSistra(id);
				missatgeInfo(request, getMessage("info.mapeigSistra.esborrat") );
				return "redirect:/expedientTipus/sistra.html?expedientTipusId=" + expedientTipusId;
			} else {
				missatgeError(request, getMessage("error.permisos.disseny.tipus.exp"));
				return "redirect:/index.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}*/

	@RequestMapping(value = "/expedientTipus/canviVersioMapeig.html")
	public String mapeigSistraCanviVersio(
			HttpServletRequest request) {
		
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
			
			missatgeInfo(request, getMessage("info.mapeigSistra.esborrat") );
			return "redirect:/expedientTipus/canviVersioMapeig.html";
			
		} catch (Exception e) {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/expedientTipus/canviVersioMapeig.html";
		}
		
    	/*missatgeInfo(request, getMessage("info.informacio.guardat"));
    	status.setComplete();
    	
    	
    	Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientTipus expedientTipus = dissenyService.getExpedientTipusById(expedientTipusId);
			if (potDissenyarExpedientTipus(entorn, expedientTipus)) {
				dissenyService.deleteMapeigSistra(id);
				missatgeInfo(request, getMessage("info.mapeigSistra.esborrat") );
				return "redirect:/expedientTipus/variables.html?expedientTipusId=" + expedientTipusId;
			} else {
				missatgeError(request, getMessage("error.permisos.disseny.tipus.exp"));
				return "redirect:/index.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}*/
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
	
		
	private class ExpedientTipusSistraValidator implements Validator {
		private DissenyService dissenyService;
		public ExpedientTipusSistraValidator(DissenyService dissenyService) {
			this.dissenyService = dissenyService;
		}
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public boolean supports(Class clazz) {
			return clazz.isAssignableFrom(ExpedientTipusSistraCommand.class);
		}
		public void validate(Object target, Errors errors) {
			ExpedientTipusSistraCommand command = (ExpedientTipusSistraCommand)target;
			if (command.isActiu()) {
				ValidationUtils.rejectIfEmpty(errors, "codiTramit", "not.blank");
				if (command.getCodiTramit() != null) {
					List<ExpedientTipus> repetits = dissenyService.findExpedientTipusAmbSistraTramitCodi(command.getCodiTramit());
					if (repetits.size() > 0) {
						if (repetits.get(0).getId().longValue() != command.getExpedientTipusId().longValue())
							errors.rejectValue("codiTramit", "error.expedienttipus.sistra.repetit");
					}
				}
			}
		}
	}

	private boolean potDissenyarExpedientTipus(Entorn entorn, ExpedientTipus expedientTipus) {
		if (potDissenyarEntorn(entorn))
			return true;
		return permissionService.filterAllowed(
				expedientTipus,
				ExpedientTipus.class,
				new Permission[] {
					ExtendedPermission.ADMINISTRATION,
					ExtendedPermission.DESIGN}) != null;
	}
	private boolean potDissenyarEntorn(Entorn entorn) {
		return permissionService.filterAllowed(
				entorn,
				Entorn.class,
				new Permission[] {
					ExtendedPermission.ADMINISTRATION,
					ExtendedPermission.DESIGN}) != null;
	}

	private static final Log logger = LogFactory.getLog(ExpedientTipusSistraController.class);

}
