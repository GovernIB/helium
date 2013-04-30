/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.service.DissenyService;
import net.conselldemallorca.helium.core.model.service.PermissionService;
import net.conselldemallorca.helium.core.security.permission.PermissionUtil;
import net.conselldemallorca.helium.webapp.mvc.util.BaseController;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.AccessControlEntry;
import org.springframework.security.acls.Permission;
import org.springframework.security.acls.sid.Sid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;

/**
 * Controlador pels permisos
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
public class PermisosExpedientTipusController extends BaseController {

	private DissenyService dissenyService;
	private PermissionService permissionService;



	@Autowired
	public PermisosExpedientTipusController(
			DissenyService dissenyService,
			PermissionService permissionService) {
		this.dissenyService = dissenyService;
		this.permissionService = permissionService;
	}

	@ModelAttribute("expedientTipus")
	public ExpedientTipus populateExpedientTipus(
			@RequestParam(value = "id", required = true) Long id) {
		return dissenyService.getExpedientTipusById(id);
	}
	@ModelAttribute("acesGroupedBySid")
	public Map<Sid, List<AccessControlEntry>> populateAclEntries(
			@RequestParam(value = "id", required = true) Long id) {
		return permissionService.getAclEntriesGroupedBySid(id, ExpedientTipus.class);
	}
	@ModelAttribute("permisos")
	public Map<String, Permission> populatePermisos() {
		Map<String, Permission> permisos = new HashMap<String, Permission>();
		Map<String, Permission> permisosAll = PermissionUtil.permissionMap;
		for (String clau: permisosAll.keySet()) {
			if (clau.equals("ADMINISTRATION") || clau.equals("READ") || clau.equals("WRITE") || clau.equals("CREATE") || clau.equals("DELETE") || clau.equals("SUPERVISION") || clau.equals("DESIGN") || clau.equals("MANAGE") || clau.equals("REASSIGNMENT"))
				permisos.put(clau, permisosAll.get(clau));
		}
		return permisos;
	}
	@ModelAttribute("command")
	public PermisosObjecteCommand populateCommand(
			@RequestParam(value = "id", required = true) Long id) {
		PermisosObjecteCommand command = new PermisosObjecteCommand();
		command.setId(id);
		command.setUsuari(true);
		return command;
	}

	@RequestMapping(value = "/permisos/expedientTipus", method = RequestMethod.GET)
	public String permisosGet(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long id,
			ModelMap model) {
		return "/permisos/expedientTipus";
	}
	@RequestMapping(value = "/permisos/expedientTipus", method = RequestMethod.POST)
	public String permisosPost(
			HttpServletRequest request,
			@RequestParam(value = "submit", required = false) String submit,
			@ModelAttribute("command") PermisosObjecteCommand command,
			BindingResult result,
			SessionStatus status,
			ModelMap model) {
		if ("submit".equals(submit) || submit.length() == 0) {
	        try {
	        	new PermisosObjecteValidator().validate(command, result);
		        if (result.hasErrors()) {
		        	return "permisos/expedientTipus";
		        }
	        	permissionService.addPermissions(
	        			command.getNom(),
	        			command.isUsuari(),
	        			command.getPermisos(),
	        			command.getId(),
	        			ExpedientTipus.class,
	        			true);
	        	missatgeInfo(request, getMessage("info.permisos.tipusexp.afegit") );
	        	status.setComplete();
	        } catch (Exception ex) {
	        	missatgeError(request, getMessage("error.afegir.permisos.tipusexp"), ex.getLocalizedMessage());
	        	logger.error("No s'han pogut afegir els permisos al tipus d'expedient", ex);
	        }
	        return "redirect:/permisos/expedientTipus.html?id=" + command.getId();
		}
		return "redirect:/expedientTipus/llistat.html";
	}

	@RequestMapping(value = "/permisos/expedientTipusEsborrar")
	public String permisosEsborrar(
			HttpServletRequest request,
			@ModelAttribute("command") PermisosObjecteCommand command) {
		try {
			permissionService.deleteAllPermissionsForSid(
					command.getNom(),
					command.isUsuari(),
					command.getId(),
					ExpedientTipus.class);
        	missatgeInfo(request, getMessage("info.permisos.tipusexp.esborrat") );
        } catch (Exception ex) {
        	missatgeError(request, getMessage("error.esborrar.permisos.tipusexp"), ex.getLocalizedMessage());
        	logger.error("No s'han pogut esborrar els permisos", ex);
        }
        return "redirect:/permisos/expedientTipus.html?id=" + command.getId();
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(
				Permission.class,
				new PermissionTypeEditor());
	}



	private static final Log logger = LogFactory.getLog(PermisosExpedientTipusController.class);

}
