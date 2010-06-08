/**
 * 
 */
package net.conselldemallorca.helium.presentacio.mvc;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.model.dto.DocumentDto;
import net.conselldemallorca.helium.model.dto.ExpedientDto;
import net.conselldemallorca.helium.model.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.model.hibernate.Entorn;
import net.conselldemallorca.helium.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.model.service.DissenyService;
import net.conselldemallorca.helium.model.service.ExpedientService;
import net.conselldemallorca.helium.model.service.PermissionService;
import net.conselldemallorca.helium.model.service.TerminiService;
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
 * Controlador per la gestió d'expedients
 * 
 * @author Josep Gayà <josepg@limit.es>
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
			missatgeError(request, "No hi ha cap entorn seleccionat");
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
					missatgeInfo(request, "L'expedient s'ha esborrat correctament");
				} catch (Exception ex) {
					missatgeError(request, "No s'ha pogut esborrar l'expedient", ex.getLocalizedMessage());
		        	logger.error("No s'ha pogut esborrar el registre", ex);
				}
				return "redirect:/expedient/consulta.html";
			} else {
				missatgeError(request, "No té permisos per esborrar aquest expedient");
				return "redirect:/expedient/consulta.html";
			}
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
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
				InstanciaProcesDto instanciaProces = expedientService.getInstanciaProcesById(id, true);
				model.addAttribute(
						"instanciaProces",
						instanciaProces);
				model.addAttribute(
						"arbreProcessos",
						expedientService.getArbreInstanciesProces(id, false));
				if (instanciaProces.isImatgeDisponible()) {
					model.addAttribute(
							"activeTokens",
							expedientService.getActiveTokens(id, true));
				}
				return "expedient/info";
			} else {
				missatgeError(request, "No té permisos per consultar aquest expedient");
				return "redirect:/expedient/consulta.html";
			}
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
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
						expedientService.getArbreInstanciesProces(id, false));
				model.addAttribute(
						"instanciaProces",
						expedientService.getInstanciaProcesById(id, true));
				model.addAttribute(
						"tasques",
						expedientService.findTasquesPerInstanciaProces(id));
				return "expedient/dades";
			} else {
				missatgeError(request, "No té permisos per consultar aquest expedient");
				return "redirect:/expedient/consulta.html";
			}
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
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
						expedientService.getArbreInstanciesProces(id, false));
				model.addAttribute(
						"instanciaProces",
						expedientService.getInstanciaProcesById(id, true));
				model.addAttribute(
						"tasques",
						expedientService.findTasquesPerInstanciaProces(id));
				return "expedient/documents";
			} else {
				missatgeError(request, "No té permisos per consultar aquest expedient");
				return "redirect:/expedient/consulta.html";
			}
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
			return "redirect:/index.html";
		}
	}
	@RequestMapping(value = "documentDescarregar")
	public String documentDescarregar(
			HttpServletRequest request,
			@RequestParam(value = "processInstanceId", required = false) String processInstanceId,
			@RequestParam(value = "docId", required = false) Long docId,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			DocumentDto document = expedientService.getDocument(docId);
			if (document != null) {
				model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_FILENAME, document.getArxiuNom());
				model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_DATA, document.getArxiuContingut());
			}
			return "arxiuView";
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
			return "redirect:/index.html";
		}
	}
	@RequestMapping(value = "documentConsultar")
	public String documentConsultar(
			HttpServletRequest request,
			@RequestParam(value = "processInstanceId", required = false) String processInstanceId,
			@RequestParam(value = "docId", required = false) Long docId,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			DocumentDto document = expedientService.getDocument(docId);
			if (document != null) {
				/*if (!document.isSignat()) {
					model.addAttribute(
							ArxiuConvertirView.MODEL_ATTRIBUTE_FILENAME,
							document.getArxiuNom());
					model.addAttribute(
							ArxiuConvertirView.MODEL_ATTRIBUTE_DATA,
							document.getArxiuContingut());
					boolean conversionEnabled = (document.getExtensioConsulta() != null);
					model.addAttribute(
							ArxiuConvertirView.MODEL_ATTRIBUTE_CONVERSIONENABLED,
							conversionEnabled);
					model.addAttribute(
							ArxiuConvertirView.MODEL_ATTRIBUTE_OUTEXTENSION,
							document.getExtensioConsulta());
					return "arxiuConvertirView";
				} else {
					model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_FILENAME, document.getArxiuNom());
					model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_DATA, document.getArxiuContingut());
					return "arxiuView";
				}*/
				String nomArxiu = document.getArxiuNom().substring(0, document.getArxiuNom().lastIndexOf("."));
				String extensioArxiu = document.getArxiuNom().substring(document.getArxiuNom().lastIndexOf(".") + 1);
				/*if (!extensioArxiu.equals("pdf")) {
					extensioArxiu = "pdf";
				}*/
				model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_FILENAME, nomArxiu + "." + extensioArxiu);
				model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_DATA, document.getArxiuContingut());
				return "arxiuView";
			}
			return "arxiuView";
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
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
						expedientService.getArbreInstanciesProces(id, false));
				model.addAttribute(
						"instanciaProces",
						expedientService.getInstanciaProcesById(id, true));
				return "expedient/timeline";
			} else {
				missatgeError(request, "No té permisos per consultar aquest expedient");
				return "redirect:/expedient/consulta.html";
			}
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
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
				missatgeError(request, "No té permisos per consultar aquest expedient");
				return "redirect:/expedient/consulta.html";
			}
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
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
						expedientService.getArbreInstanciesProces(id, false));
				model.addAttribute(
						"instanciaProces",
						expedientService.getInstanciaProcesById(id, true));
				model.addAttribute(
						"tasques",
						expedientService.findTasquesPerInstanciaProces(id));
				return "expedient/tasques";
			} else {
				missatgeError(request, "No té permisos per consultar aquest expedient");
				return "redirect:/expedient/consulta.html";
			}
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
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
				InstanciaProcesDto instanciaProces = expedientService.getInstanciaProcesById(id, true);
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
				missatgeError(request, "No té permisos per consultar aquest expedient");
				return "redirect:/expedient/consulta.html";
			}
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
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
				missatgeError(request, "No té permisos per consultar aquest expedient");
				return "redirect:/expedient/consulta.html";
			}
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
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
	private boolean potEsborrarExpedient(ExpedientDto expedient) {
		return permissionService.filterAllowed(
				expedient.getTipus(),
				ExpedientTipus.class,
				new Permission[] {
					ExtendedPermission.ADMINISTRATION,
					ExtendedPermission.DELETE}) != null;
	}

	private static final Log logger = LogFactory.getLog(ExpedientController.class);

}
