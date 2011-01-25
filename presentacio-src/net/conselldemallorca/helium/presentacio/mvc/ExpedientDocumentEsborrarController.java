/**
 * 
 */
package net.conselldemallorca.helium.presentacio.mvc;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.model.dto.DocumentDto;
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
 * Controlador per a esborrar els documents dels expedients
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@Controller
public class ExpedientDocumentEsborrarController extends BaseController {

	private ExpedientService expedientService;
	private PermissionService permissionService;



	@Autowired
	public ExpedientDocumentEsborrarController(
			ExpedientService expedientService,
			PermissionService permissionService) {
		this.expedientService = expedientService;
		this.permissionService = permissionService;
	}

	@RequestMapping(value = "/expedient/documentEsborrar")
	public String documentProcesEsborrar(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "docId", required = true) Long docId,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientDto expedient = expedientService.findExpedientAmbProcessInstanceId(id);
			if (potModificarExpedient(expedient)) {
				try {
					DocumentDto doc = expedientService.getDocument(docId, false, false);
					if (!doc.isSignat()) {
						expedientService.deleteDocument(id, docId);
						missatgeInfo(request, "El document ha estat esborrat del procés");
					} else {
						missatgeError(request, "No es pot esborrar un document signat");
					}
				} catch (Exception ex) {
					missatgeError(request, "No s'ha pogut esborrar el document del procés", ex.getLocalizedMessage());
		        	logger.error("No s'ha pogut esborrar el document del procés", ex);
				}
				return "redirect:/expedient/documents.html?id=" + id;
			} else {
				missatgeError(request, "No té permisos per modificar aquest expedient");
			}
			return "redirect:/expedient/consulta.html";
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/expedient/signaturaEsborrar")
	public String signaturaEsborrar(
			HttpServletRequest request,
			@RequestParam(value = "processInstanceId", required = true) String processInstanceId,
			@RequestParam(value = "docId", required = true) Long docId,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientDto expedient = expedientService.findExpedientAmbProcessInstanceId(processInstanceId);
			if (potModificarExpedient(expedient)) {
				DocumentDto document = expedientService.getDocument(docId, false, false);
				if (document != null) {
					if (document.isSignat()) {
						try {
							expedientService.deleteSignatura(processInstanceId, docId);
							missatgeInfo(request, "La signatura s'ha esborrat correctament");
						} catch (Exception ex) {
							missatgeError(request, "No s'ha pogut esborrar la signatura", ex.getLocalizedMessage());
				        	logger.error("No s'ha pogut esborrar la signatura", ex);
						}
					} else {
						missatgeError(request, "Aquest document no està signat");
					}
				}
			} else {
				missatgeError(request, "No té permisos per modificar aquest expedient");
			}
			return "redirect:/expedient/documents.html?id=" + processInstanceId;
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



	private static final Log logger = LogFactory.getLog(ExpedientDocumentEsborrarController.class);

}
