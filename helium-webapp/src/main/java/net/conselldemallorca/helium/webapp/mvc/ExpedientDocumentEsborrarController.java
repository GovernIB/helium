/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.core.model.dto.DocumentDto;
import net.conselldemallorca.helium.core.model.dto.ExpedientDto;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.service.DocumentService;
import net.conselldemallorca.helium.core.model.service.ExpedientService;
import net.conselldemallorca.helium.core.model.service.PermissionService;
import net.conselldemallorca.helium.core.security.ExtendedPermission;
import net.conselldemallorca.helium.webapp.mvc.util.BaseController;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.Permission;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;



/**
 * Controlador per a esborrar els documents dels expedients
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
public class ExpedientDocumentEsborrarController extends BaseController {

	private ExpedientService expedientService;
	private DocumentService documentService;
	private PermissionService permissionService;



	@Autowired
	public ExpedientDocumentEsborrarController(
			ExpedientService expedientService,
			DocumentService documentService,
			PermissionService permissionService) {
		this.expedientService = expedientService;
		this.documentService = documentService;
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
					DocumentDto doc = documentService.documentInfo(docId);
					if (!doc.isSignat() && !doc.isRegistrat()) {
						documentService.esborrarDocument(
								null,
								id,
								docId.toString());
						if(doc.isAdjunt()){
							documentService.esborrarVariableInstance(id, doc.getAdjuntId());
						}
						missatgeInfo(request, getMessage("info.doc.proces.esborrat") );
					} else if (doc.isSignat()) {
						missatgeError(request, getMessage("error.esborrar.doc.signat") );
					} else if (doc.isRegistrat()) {
						missatgeError(request, getMessage("error.esborrar.doc.registrat") );
					}
				} catch (Exception ex) {
					Long entornId = entorn.getId();
					String numeroExpedient = id;
					logger.error("ENTORNID:"+entornId+" NUMEROEXPEDIENT:"+numeroExpedient+" No s'ha pogut esborrar el document del procés", ex);
					missatgeError(request, getMessage("error.esborrar.doc.proces"), ex.getLocalizedMessage());
				}
				return "redirect:/expedient/documents.html?id=" + id;
			} else {
				missatgeError(request, getMessage("error.permisos.modificar.expedient"));
			}
			return "redirect:/expedient/consulta.html";
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
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
				DocumentDto document = documentService.documentInfo(docId);
				if (document != null) {
					if (document.isSignat()) {
						try {
							expedientService.deleteSignatura(processInstanceId, docId);
							missatgeInfo(request, getMessage("info.signatura.esborrat") );
						} catch (Exception ex) {
							missatgeError(request, getMessage("error.esborrar.signatura"), ex.getLocalizedMessage());
				        	logger.error("No s'ha pogut esborrar la signatura", ex);
						}
					} else {
						missatgeError(request, "Aquest document no està signat");
					}
				}
			} else {
				missatgeError(request, getMessage("error.permisos.modificar.expedient"));
			}
			return "redirect:/expedient/documents.html?id=" + processInstanceId;
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
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
