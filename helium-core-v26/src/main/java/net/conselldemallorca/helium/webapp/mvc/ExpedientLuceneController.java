/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.core.model.dto.DadaIndexadaDto;
import net.conselldemallorca.helium.core.model.dto.ExpedientDto;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;



/**
 * Controlador per a l'execució d'scripts dins un expedient
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
public class ExpedientLuceneController extends BaseController {

	private ExpedientService expedientService;
	private PermissionService permissionService;



	@Autowired
	public ExpedientLuceneController(
			ExpedientService expedientService,
			PermissionService permissionService) {
		this.expedientService = expedientService;
		this.permissionService = permissionService;
	}

	@RequestMapping(value = "/expedient/lucene", method = RequestMethod.GET)
	public String lucene(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientDto expedient = expedientService.findExpedientAmbProcessInstanceId(id);
			if (potConsultarExpedient(expedient)) {
				List<Map<String, DadaIndexadaDto>> dadesLucene = expedientService.luceneGetDades(id);
				if (dadesLucene.size() > 0) {
					Map<String, DadaIndexadaDto> dadesExpedient = dadesLucene.get(0);
					List<DadaIndexadaDto> llistaDadesExpedient = new ArrayList<DadaIndexadaDto>();
					for (String clau: dadesExpedient.keySet()) {
						if (dadesExpedient.get(clau).isDadaExpedient())
							llistaDadesExpedient.add(dadesExpedient.get(clau));
					}
					model.addAttribute(
							"dadesExpedient",
							llistaDadesExpedient);
					List<DadaIndexadaDto> llistaDadesCamps = new ArrayList<DadaIndexadaDto>();
					for (String clau: dadesExpedient.keySet()) {
						if (!dadesExpedient.get(clau).isDadaExpedient())
							llistaDadesCamps.add(dadesExpedient.get(clau));
					}
					model.addAttribute(
							"dadesCamps",
							llistaDadesCamps);
				}
				return "expedient/lucene";
			} else {
				missatgeError(request, getMessage("error.permisos.consultar.expedient"));
				return "redirect:/expedient/consulta.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/expedient/reindexar", method = RequestMethod.GET)
	public String reindexar(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientDto expedient = expedientService.findExpedientAmbProcessInstanceId(id);
			if (potModificarExpedient(expedient)) {
				try {
					expedientService.luceneReindexarExpedient(id);
					missatgeInfo(request, getMessage("info.expedient.reindexat"));
				} catch (Exception ex) {
					missatgeError(request, getMessage("error.reindexar.expedient"), ex.getLocalizedMessage());
		        	logger.error("No s'ha pogut reindexar l'expedient", ex);
				}
				return "redirect:/expedient/lucene.html?id=" + id;
			} else {
				missatgeError(request, getMessage("error.permisos.modificar.expedient"));
				return "redirect:/expedient/lucene.html?id=" + id;
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}



	private boolean potConsultarExpedient(ExpedientDto expedient) {
		return permissionService.filterAllowed(
				expedient.getTipus(),
				ExpedientTipus.class,
				new Permission[] {
					ExtendedPermission.ADMINISTRATION,
					ExtendedPermission.READ}) != null;
	}
	private boolean potModificarExpedient(ExpedientDto expedient) {
		return permissionService.filterAllowed(
				expedient.getTipus(),
				ExpedientTipus.class,
				new Permission[] {
					ExtendedPermission.ADMINISTRATION,
					ExtendedPermission.WRITE}) != null;
	}

	private static final Log logger = LogFactory.getLog(ExpedientLuceneController.class);

}
