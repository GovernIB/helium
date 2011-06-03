/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.core.model.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.MapeigSistra;
import net.conselldemallorca.helium.core.model.service.DissenyService;
import net.conselldemallorca.helium.core.model.service.PermissionService;
import net.conselldemallorca.helium.core.security.permission.ExtendedPermission;
import net.conselldemallorca.helium.webapp.mvc.util.BaseController;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.Permission;
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
public class ExpedientTipusVariablesController extends BaseController {

	private DissenyService dissenyService;
	private PermissionService permissionService;

	@Autowired
	public ExpedientTipusVariablesController(
			DissenyService dissenyService,
			PermissionService permissionService) {
		this.dissenyService = dissenyService;
		this.permissionService = permissionService;
	}
	
	/*@ModelAttribute("tipusMapeig")
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
			if (expedientTipus.getSistraTramitCodi() != null) {
				command.setCodiTramit(expedientTipus.getSistraTramitCodi());
				//command.setInfoMapeigCamps(expedientTipus.getSistraTramitMapeigCamps());
				command.setMapeigSistras(expedientTipus.getMapeigSistras());
				//command.setInfoMapeigDocuments(expedientTipus.getSistraTramitMapeigDocuments());
				//command.setInfoMapeigAdjunts(expedientTipus.getSistraTramitMapeigAdjunts());
				command.setActiu(true);
				
			} else {
				command.setActiu(false);
			}
		}
		return command;
	}*/
	
	@ModelAttribute("command")
	public MapeigSistra populateCommand() {
		return new MapeigSistra();
	}
	
	@ModelAttribute("expedientTipus")
	public ExpedientTipus populateExpedientTipus(
			@RequestParam(value = "expedientTipusId", required = true) Long expedientTipusId) {
		return dissenyService.getExpedientTipusById(expedientTipusId);
	}

	@RequestMapping(value = "/expedientTipus/variables", method = RequestMethod.GET)
	public String formGet(
			HttpServletRequest request,
			@RequestParam(value = "expedientTipusId", required = true) Long expedientTipusId,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientTipus expedientTipus = dissenyService.getExpedientTipusById(expedientTipusId);
			if (potDissenyarExpedientTipus(entorn, expedientTipus)) {
				model.addAttribute("mapeigSistras", dissenyService.findMapeigSistraVariablesAmbExpedientTipus(expedientTipusId));
				
				List<DefinicioProcesDto> processos = dissenyService.findDarreresAmbExpedientTipusIGlobalsEntorn(entorn.getId(), expedientTipusId);
				for (DefinicioProcesDto proces : processos){
					if (proces.getJbpmKey().equalsIgnoreCase(expedientTipus.getJbpmProcessDefinitionKey()))
						model.addAttribute("codisProces", dissenyService.findCampsAmbDefinicioProcesOrdenatsPerCodi(proces.getId()));
				}
				
				return "expedientTipus/variables";
			} else {
				missatgeError(request, getMessage("error.permisos.disseny.tipus.exp"));
				return "redirect:/index.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/expedientTipus/variables", method = RequestMethod.POST)
//@RequestMapping({"/profile.htm", "/perfilEF.htm", "perfilCategoriaEF.htm"})
	public String formPost(
			HttpServletRequest request,
			@RequestParam(value = "submit", required = false) String submit,
			@RequestParam(value = "expedientTipusId", required = true) Long expedientTipusId,
			@ModelAttribute("command") MapeigSistra command,
			BindingResult result,
			SessionStatus status,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientTipus expedientTipus = dissenyService.getExpedientTipusById(expedientTipusId);
			if (potDissenyarExpedientTipus(entorn, expedientTipus)) {
				if ("submit".equals(submit) || submit.length() == 0) {
					command.setExpedientTipus(expedientTipus);
					command.setTipus(MapeigSistra.TipusMapeig.Variable);
					//annotationValidator.validate(command, result);
					new ExpedientTipusSistraVariableValidator(dissenyService).validate(command, result);
			        if (result.hasErrors()) {
						model.addAttribute("mapeigSistras", dissenyService.findMapeigSistraVariablesAmbExpedientTipus(expedientTipusId));
						
						List<DefinicioProcesDto> processos = dissenyService.findDarreresAmbExpedientTipusIGlobalsEntorn(entorn.getId(), expedientTipusId);
						for (DefinicioProcesDto proces : processos){
							if (proces.getJbpmKey().equalsIgnoreCase(expedientTipus.getJbpmProcessDefinitionKey()))
								model.addAttribute("codisProces", dissenyService.findCampsAmbDefinicioProcesOrdenatsPerCodi(proces.getId()));
						}
						
			        	return "expedientTipus/variables";
			        }
			        try {
			        	dissenyService.createMapeigSistra(command.getCodiHelium(), command.getCodiSistra(), command.getTipus(), expedientTipus);
			        	
		        		//dissenyService.updateExpedientTipus(expedientTipus);
			        	missatgeInfo(request, getMessage("info.informacio.guardat"));
			        	status.setComplete();
			        } catch (Exception ex) {
			        	missatgeError(request, getMessage("error.proces.peticio"), ex.getLocalizedMessage());
			        	logger.error("No s'ha pogut guardar el registre", ex);
			        }
				}
				return "redirect:/expedientTipus/variables.html?expedientTipusId=" + expedientTipusId;
			} else {
				missatgeError(request, getMessage("error.permisos.disseny.tipus.exp"));
				return "redirect:/index.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/expedientTipus/mapeigSistraEsborrarVariable.html")
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
				return "redirect:/expedientTipus/variables.html?expedientTipusId=" + expedientTipusId;
			} else {
				missatgeError(request, getMessage("error.permisos.disseny.tipus.exp"));
				return "redirect:/index.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	private class ExpedientTipusSistraVariableValidator implements Validator {
		private DissenyService dissenyService;
		public ExpedientTipusSistraVariableValidator(DissenyService dissenyService) {
			this.dissenyService = dissenyService;
		}
		@SuppressWarnings("unchecked")
		public boolean supports(Class clazz) {
			return clazz.isAssignableFrom(MapeigSistra.class);
		}
		public void validate(Object target, Errors errors) {
			MapeigSistra command = (MapeigSistra)target;
			ValidationUtils.rejectIfEmpty(errors, "codiHelium", "not.blank");
			ValidationUtils.rejectIfEmpty(errors, "codiSistra", "not.blank");
			ValidationUtils.rejectIfEmpty(errors, "tipus", "not.blank");
			ValidationUtils.rejectIfEmpty(errors, "expedientTipus", "not.blank");
			
			
			if (command.getCodiHelium() != null && !("".equalsIgnoreCase(command.getCodiHelium())) && command.getExpedientTipus() != null) {	
				MapeigSistra repetits = dissenyService.findMapeigSistraAmbExpedientTipusICodi(command.getExpedientTipus().getId(), command.getCodiHelium());
				if (repetits != null) {
					errors.rejectValue("codiHelium", "error.expedienttipus.sistra.mapeig.repetit");
				}
			}
			if (command.getCodiHelium().length() > 255) 
				errors.rejectValue("codiHelium", "max.length");
			if (command.getCodiSistra().length() > 255) 
				errors.rejectValue("codiSistra", "max.length");
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

	private static final Log logger = LogFactory.getLog(ExpedientTipusVariablesController.class);

}
