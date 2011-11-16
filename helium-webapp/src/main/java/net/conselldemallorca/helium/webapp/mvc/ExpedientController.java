/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.core.model.dto.ExpedientDto;
import net.conselldemallorca.helium.core.model.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.service.DissenyService;
import net.conselldemallorca.helium.core.model.service.ExpedientService;
import net.conselldemallorca.helium.core.model.service.PermissionService;
import net.conselldemallorca.helium.core.model.service.TerminiService;
import net.conselldemallorca.helium.core.security.permission.ExtendedPermission;
import net.conselldemallorca.helium.webapp.mvc.util.BaseController;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.Permission;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;



/**
 * Controlador per la gestió d'expedients
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/expedient/*.html")
public class ExpedientController extends BaseController {

	private DissenyService dissenyService;
	private ExpedientService expedientService;
	private TerminiService terminiService;
	private PermissionService permissionService;



	@Autowired
	public ExpedientController(
			DissenyService dissenyService,
			ExpedientService expedientService,
			TerminiService terminiService,
			PermissionService permissionService) {
		this.dissenyService = dissenyService;
		this.expedientService = expedientService;
		this.terminiService = terminiService;
		this.permissionService = permissionService;
	}

	@RequestMapping(value = "llistat")
	public String llistat(
			HttpServletRequest request,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			List<ExpedientTipus> tipus = dissenyService.findExpedientTipusAmbEntorn(entorn.getId());
			permissionService.filterAllowed(
					tipus,
					ExpedientTipus.class,
					new Permission[] {
						ExtendedPermission.ADMINISTRATION,
						ExtendedPermission.READ});
			List<ExpedientDto> expedients = expedientService.findAmbEntorn(entorn.getId());
			Iterator<ExpedientDto> it = expedients.iterator();
			while (it.hasNext()) {
				ExpedientDto expedient = it.next();
				if (!tipus.contains(expedient.getTipus()))
					it.remove();
			}
			model.addAttribute("llistat", expedients);
			return "expedient/llistat";
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "delete")
	public String deleteAction(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long id) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientDto expedient = expedientService.getById(id);
			if (potEsborrarExpedient(expedient)) {
				try {
					expedientService.delete(entorn.getId(), id);
					missatgeInfo(request, getMessage("info.expedient.esborrat") );
				} catch (Exception ex) {
					missatgeError(request, getMessage("error.esborrar.expedient"), ex.getLocalizedMessage());
		        	logger.error("No s'ha pogut esborrar el registre", ex);
				}
				return "redirect:/expedient/consulta.html";
			} else {
				missatgeError(request, getMessage("error.permisos.esborrar.expedient"));
				return "redirect:/expedient/consulta.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "anular")
	public String anular(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long id) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientDto expedient = expedientService.getById(id);
			if (potModificarExpedient(expedient)) {
				try {
					expedientService.anular(entorn.getId(), id);
					missatgeInfo(request, getMessage("info.expedient.anulat") );
				} catch (Exception ex) {
					missatgeError(request, getMessage("error.anular.expedient"), ex.getLocalizedMessage());
		        	logger.error("No s'ha pogut esborrar el registre", ex);
				}
				return "redirect:/expedient/consulta.html";
			} else {
				missatgeError(request, getMessage("error.permisos.anular.expedient"));
				return "redirect:/expedient/consulta.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "info")
	public String info(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientDto expedient = expedientService.findExpedientAmbProcessInstanceId(id);
			if (potConsultarExpedient(expedient)) {
				model.addAttribute(
						"expedient",
						expedient);
				InstanciaProcesDto instanciaProces = expedientService.getInstanciaProcesById(id, false);
				model.addAttribute(
						"instanciaProces",
						instanciaProces);
				model.addAttribute(
						"arbreProcessos",
						expedientService.getArbreInstanciesProces(id));
				if (instanciaProces.isImatgeDisponible()) {
					model.addAttribute(
							"activeTokens",
							expedientService.getActiveTokens(id, true));
				}
				return "expedient/info";
			} else {
				missatgeError(request, getMessage("error.permisos.consultar.expedient"));
				return "redirect:/expedient/consulta.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "dades")
	public String dades(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientDto expedient = expedientService.findExpedientAmbProcessInstanceId(id);
			if (potConsultarExpedient(expedient)) {
				model.addAttribute(
						"expedient",
						expedient);
				model.addAttribute(
						"arbreProcessos",
						expedientService.getArbreInstanciesProces(id));
				model.addAttribute(
						"instanciaProces",
						expedientService.getInstanciaProcesById(id, true));
				model.addAttribute(
						"tasques",
						expedientService.findTasquesPerInstanciaProces(id));
				return "expedient/dades";
			} else {
				missatgeError(request, getMessage("error.permisos.consultar.expedient"));
				return "redirect:/expedient/consulta.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "documents")
	public String documents(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientDto expedient = expedientService.findExpedientAmbProcessInstanceId(id);
			if (potConsultarExpedient(expedient)) {
				model.addAttribute(
						"expedient",
						expedient);
				model.addAttribute(
						"arbreProcessos",
						expedientService.getArbreInstanciesProces(id));
				model.addAttribute(
						"instanciaProces",
						expedientService.getInstanciaProcesById(id, true));
				model.addAttribute(
						"tasques",
						expedientService.findTasquesPerInstanciaProces(id));
				return "expedient/documents";
			} else {
				missatgeError(request, getMessage("error.permisos.consultar.expedient"));
				return "redirect:/expedient/consulta.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "timeline")
	public String timeline(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientDto expedient = expedientService.findExpedientAmbProcessInstanceId(id);
			if (potConsultarExpedient(expedient)) {
				model.addAttribute(
						"expedient",
						expedient);
				model.addAttribute(
						"arbreProcessos",
						expedientService.getArbreInstanciesProces(id));
				model.addAttribute(
						"instanciaProces",
						expedientService.getInstanciaProcesById(id, true));
				return "expedient/timeline";
			} else {
				missatgeError(request, getMessage("error.permisos.consultar.expedient"));
				return "redirect:/expedient/consulta.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "timelineXml")
	public String timelineXml(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientDto expedient = expedientService.findExpedientAmbProcessInstanceId(id);
			if (potConsultarExpedient(expedient)) {
				model.addAttribute(
						"instanciaProces",
						expedientService.getInstanciaProcesById(id, true));
				model.addAttribute(
						"terminisIniciats",
						terminiService.findIniciatsAmbProcessInstanceId(id));
				return "expedient/timelineXml";
			} else {
				missatgeError(request, getMessage("error.permisos.consultar.expedient"));
				return "redirect:/expedient/consulta.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "tasques")
	public String tasques(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientDto expedient = expedientService.findExpedientAmbProcessInstanceId(id);
			if (potConsultarExpedient(expedient)) {
				model.addAttribute(
						"expedient",
						expedient);
				model.addAttribute(
						"arbreProcessos",
						expedientService.getArbreInstanciesProces(id));
				model.addAttribute(
						"instanciaProces",
						expedientService.getInstanciaProcesById(id, false));
				model.addAttribute(
						"tasques",
						expedientService.findTasquesPerInstanciaProces(id));
				return "expedient/tasques";
			} else {
				missatgeError(request, getMessage("error.permisos.consultar.expedient"));
				return "redirect:/expedient/consulta.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "imatgeProces")
	public String imatgeProces(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientDto expedient = expedientService.findExpedientAmbProcessInstanceId(id);
			if (potConsultarExpedient(expedient)) {
				InstanciaProcesDto instanciaProces = expedientService.getInstanciaProcesById(id, false);
				String resourceName = "processimage.jpg";
				model.addAttribute(
						ArxiuView.MODEL_ATTRIBUTE_FILENAME,
						resourceName);
				model.addAttribute(
						ArxiuView.MODEL_ATTRIBUTE_DATA,
						dissenyService.getImatgeDefinicioProces(
								instanciaProces.getDefinicioProces().getId()));
				return "arxiuView";
			} else {
				missatgeError(request, getMessage("error.permisos.consultar.expedient"));
				return "redirect:/expedient/consulta.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "registre")
	public String registre(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientDto expedient = expedientService.findExpedientAmbProcessInstanceId(id);
			if (potConsultarExpedient(expedient)) {
				model.addAttribute(
						"registre",
						expedientService.getRegistrePerExpedient(expedient.getId()));
				return "expedient/registre";
			} else {
				missatgeError(request, getMessage("error.permisos.consultar.expedient"));
				return "redirect:/expedient/consulta.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "accio")
	public String accio(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "submit", required = true) String jbpmAction,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientDto expedient = expedientService.findExpedientAmbProcessInstanceId(id);
			if (potModificarExpedient(expedient)) {
				expedientService.executarAccio(id, jbpmAction);
				missatgeInfo(request, getMessage("info.accio.executat") );
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
	private boolean potEsborrarExpedient(ExpedientDto expedient) {
		return permissionService.filterAllowed(
				expedient.getTipus(),
				ExpedientTipus.class,
				new Permission[] {
					ExtendedPermission.ADMINISTRATION,
					ExtendedPermission.DELETE}) != null;
	}

	/*private boolean isSignaturaFileAttached() {
		return "true".equalsIgnoreCase((String)GlobalProperties.getInstance().get("app.signatura.plugin.file.attached"));
	}*/

	private static final Log logger = LogFactory.getLog(ExpedientController.class);

}
