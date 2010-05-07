/**
 * 
 */
package net.conselldemallorca.helium.presentacio.mvc;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.model.dto.ExpedientDto;
import net.conselldemallorca.helium.model.hibernate.Entorn;
import net.conselldemallorca.helium.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.model.service.ExpedientService;
import net.conselldemallorca.helium.model.service.PermissionService;
import net.conselldemallorca.helium.presentacio.mvc.util.BaseController;
import net.conselldemallorca.helium.security.permission.ExtendedPermission;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.Permission;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;



/**
 * Controlador per la reassignació de tasques dels expedients
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@Controller
public class ExpedientTascaSuspendreController extends BaseController {

	private ExpedientService expedientService;
	private PermissionService permissionService;



	@Autowired
	public ExpedientTascaSuspendreController(
			ExpedientService expedientService,
			PermissionService permissionService) {
		this.expedientService = expedientService;
		this.permissionService = permissionService;
	}

	@RequestMapping("/expedient/tascaSuspendre")
	public String tascaSuspendre(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "taskId", required = true) String taskId,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientDto expedient = expedientService.findExpedientAmbProcessInstanceId(id);
			if (potModificarExpedient(expedient)) {
				try {
					expedientService.suspendreTasca(entorn.getId(), taskId);
				} catch (Exception ex) {
					missatgeError(request, "No s'ha pogut suspendre la tasca " + taskId, ex.getLocalizedMessage());
		        	logger.error("No s'ha pogut suspendre la tasca " + taskId, ex);
				}
				return "redirect:/expedient/tasques.html?id=" + id;
			} else {
				missatgeError(request, "No té permisos per modificar aquest expedient");
				return "redirect:/expedient/consulta.html";
			}
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
			return "redirect:/index.html";
		}
	}
	@RequestMapping("/expedient/tascaReprendre")
	public String tascaReprendre(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "taskId", required = true) String taskId,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientDto expedient = expedientService.findExpedientAmbProcessInstanceId(id);
			if (potModificarExpedient(expedient)) {
				try {
					expedientService.reprendreTasca(entorn.getId(), taskId);
				} catch (Exception ex) {
					missatgeError(request, "No s'ha pogut reprendre la tasca " + taskId, ex.getLocalizedMessage());
		        	logger.error("No s'ha pogut reprendre la tasca " + taskId, ex);
				}
				return "redirect:/expedient/tasques.html?id=" + id;
			} else {
				missatgeError(request, "No té permisos per modificar aquest expedient");
				return "redirect:/expedient/consulta.html";
			}
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
			return "redirect:/index.html";
		}
	}

	@RequestMapping("/expedient/tascaCancelar")
	public String tascaCancelar(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "taskId", required = true) String taskId,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientDto expedient = expedientService.findExpedientAmbProcessInstanceId(id);
			if (potModificarExpedient(expedient)) {
				try {
					expedientService.cancelarTasca(entorn.getId(), taskId);
				} catch (Exception ex) {
					missatgeError(request, "No s'ha pogut cancel·lar la tasca " + taskId, ex.getLocalizedMessage());
		        	logger.error("No s'ha pogut cancel·lar la tasca " + taskId, ex);
				}
				return "redirect:/expedient/tasques.html?id=" + id;
			} else {
				missatgeError(request, "No té permisos per modificar aquest expedient");
				return "redirect:/expedient/consulta.html";
			}
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
			return "redirect:/index.html";
		}
	}



	private boolean potModificarExpedient(ExpedientDto expedient) {
		return permissionService.filterAllowed(
				expedient.getTipus(),
				ExpedientTipus.class,
				new Permission[] {
					ExtendedPermission.ADMINISTRATION,
					ExtendedPermission.WRITE}) != null;
	}

	private static final Log logger = LogFactory.getLog(ExpedientTascaSuspendreController.class);

}
