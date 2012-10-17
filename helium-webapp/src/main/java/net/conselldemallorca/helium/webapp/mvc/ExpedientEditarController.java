/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.core.model.dto.ExpedientDto;
import net.conselldemallorca.helium.core.model.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.Estat;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.service.DissenyService;
import net.conselldemallorca.helium.core.model.service.ExpedientService;
import net.conselldemallorca.helium.core.model.service.PermissionService;
import net.conselldemallorca.helium.core.model.service.PluginService;
import net.conselldemallorca.helium.core.security.permission.ExtendedPermission;
import net.conselldemallorca.helium.webapp.mvc.util.BaseController;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.security.acls.Permission;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;



/**
 * Controlador per a editar la informació d'un expedient
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
public class ExpedientEditarController extends BaseController {

	private DissenyService dissenyService;
	private ExpedientService expedientService;
	private PermissionService permissionService;
	private PluginService pluginService;



	@Autowired
	public ExpedientEditarController(
			DissenyService dissenyService,
			ExpedientService expedientService,
			PermissionService permissionService,
			PluginService pluginService) {
		this.dissenyService = dissenyService;
		this.expedientService = expedientService;
		this.permissionService = permissionService;
		this.pluginService = pluginService;
	}

	@ModelAttribute("estats")
	public List<Estat> populateEntorn(
			@RequestParam(value = "id", required = true) String id) {
		ExpedientDto expedient = expedientService.findExpedientAmbProcessInstanceId(id);
		return dissenyService.findEstatAmbExpedientTipus(expedient.getTipus().getId());
	}

	@RequestMapping(value = "/expedient/editar", method = RequestMethod.GET)
	public String editarGet(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientDto expedient = expedientService.findExpedientAmbProcessInstanceId(id);
			if (potModificarExpedient(expedient)) {
				model.addAttribute(
						"command",
						initCommand(expedient));
				dadesPaginaEditar(id, expedient, model);
				return "expedient/editar";
			} else {
				missatgeError(request, getMessage("error.permisos.modificar.expedient"));
				return "redirect:/expedient/consulta.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/expedient/editar", method = RequestMethod.POST)
	public String editarPost(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "submit", required = true) String submit,
			@ModelAttribute("command") ExpedientEditarCommand command,
			BindingResult result,
			SessionStatus status,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientDto expedient = expedientService.findExpedientAmbProcessInstanceId(id);
			if (potModificarExpedient(expedient)) {
				if ("submit".equals(submit)) {
					new ExpedientEditarValidator().validate(command, result);
			        if (result.hasErrors()) {
			        	dadesPaginaEditar(id, expedient, model);
			        	return "expedient/editar";
			        }
					try {
						expedientService.editar(
								entorn.getId(),
								command.getExpedientId(),
								command.getNumero(),
								command.getTitol(),
								command.getResponsableCodi(),
								command.getDataInici(),
								command.getComentari(),
								command.getEstatId(),
								command.getGeoPosX(),
								command.getGeoPosY(),
								command.getGeoReferencia(),
								command.getGrupCodi());
						missatgeInfo(request, getMessage("info.informacio.modificat") );
					} catch (Exception ex) {
						missatgeError(request, getMessage("error.modificar.dades.exp"), ex.getLocalizedMessage());
			        	logger.error("No s'han pogut modificar les dades de l'expedient", ex);
			        	dadesPaginaEditar(id, expedient, model);
			        	return "expedient/editar";
					}
				}
				return "redirect:/expedient/info.html?id=" + id;
			} else {
				missatgeError(request, getMessage("error.permisos.modificar.expedient"));
				return "redirect:/expedient/consulta.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(
				Date.class,
				new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true));
		binder.registerCustomEditor(
				Double.class,
				new CustomNumberEditor(Double.class, true));
	}

	private class ExpedientEditarValidator implements Validator {
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public boolean supports(Class clazz) {
			return clazz.isAssignableFrom(ExpedientEditarCommand.class);
		}
		public void validate(Object target, Errors errors) {
			ExpedientEditarCommand command = (ExpedientEditarCommand)target;
			ExpedientDto expedient = expedientService.getById(command.getExpedientId());
			if (expedient.getTipus().getTeTitol())
				ValidationUtils.rejectIfEmpty(errors, "titol", "not.blank");
			if (expedient.getTipus().getTeNumero())
				ValidationUtils.rejectIfEmpty(errors, "numero", "not.blank");
			ValidationUtils.rejectIfEmpty(errors, "dataInici", "not.blank");
		}
	}



	private ExpedientEditarCommand initCommand(ExpedientDto expedient) {
		ExpedientEditarCommand command = new ExpedientEditarCommand();
		command.setExpedientId(expedient.getId());
		command.setNumero(expedient.getNumero());
		command.setTitol(expedient.getTitol());
		command.setComentari(expedient.getComentari());
		command.setDataInici(expedient.getDataInici());
		command.setIniciadorCodi(expedient.getIniciadorCodi());
		command.setResponsableCodi(expedient.getResponsableCodi());
		if (expedient.getEstat() != null)
			command.setEstatId(expedient.getEstat().getId());
		command.setGeoPosX(expedient.getGeoPosX());
		command.setGeoPosY(expedient.getGeoPosY());
		command.setGeoReferencia(expedient.getGeoReferencia());
		command.setGrupCodi(expedient.getGrupCodi());
		return command;
	}

	private boolean potModificarExpedient(ExpedientDto expedient) {
		return permissionService.filterAllowed(
				expedient.getTipus(),
				ExpedientTipus.class,
				new Permission[] {
					ExtendedPermission.ADMINISTRATION,
					ExtendedPermission.WRITE}) != null;
	}

	private void dadesPaginaEditar(
			String id,
			ExpedientDto expedient,
			ModelMap model) {
		ExpedientEditarCommand command = (ExpedientEditarCommand)model.get("command");
		model.addAttribute(
				"expedient",
				expedient);
		model.addAttribute(
				"arbreProcessos",
				expedientService.getArbreInstanciesProces(id));
		InstanciaProcesDto instanciaProces = expedientService.getInstanciaProcesById(id, false, false, false);
		model.addAttribute(
				"instanciaProces",
				instanciaProces);
		if (command.getIniciadorCodi() != null)
			model.addAttribute(
					"iniciador",
					pluginService.findPersonaAmbCodi(command.getIniciadorCodi()));
		if (command.getResponsableCodi() != null)
			model.addAttribute(
					"responsable",
					pluginService.findPersonaAmbCodi(command.getResponsableCodi()));
	}

	private static final Log logger = LogFactory.getLog(ExpedientEditarController.class);

}
