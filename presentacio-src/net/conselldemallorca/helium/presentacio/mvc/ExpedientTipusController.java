/**
 * 
 */
package net.conselldemallorca.helium.presentacio.mvc;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.integracio.plugins.persones.Persona;
import net.conselldemallorca.helium.model.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.model.dto.ExpedientDto;
import net.conselldemallorca.helium.model.hibernate.Entorn;
import net.conselldemallorca.helium.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.model.service.DissenyService;
import net.conselldemallorca.helium.model.service.ExpedientService;
import net.conselldemallorca.helium.model.service.PermissionService;
import net.conselldemallorca.helium.model.service.PluginService;
import net.conselldemallorca.helium.presentacio.mvc.util.BaseController;
import net.conselldemallorca.helium.security.permission.ExtendedPermission;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.Permission;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;



/**
 * Controlador per la gestió de tipus d'expedient
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@Controller
public class ExpedientTipusController extends BaseController {

	private DissenyService dissenyService;
	private ExpedientService expedientService;
	private PermissionService permissionService;
	private PluginService pluginService;



	@Autowired
	public ExpedientTipusController(
			DissenyService dissenyService,
			ExpedientService expedientService,
			PermissionService permissionService,
			PluginService pluginService) {
		this.dissenyService = dissenyService;
		this.expedientService = expedientService;
		this.permissionService = permissionService;
		this.pluginService = pluginService;
	}

	@ModelAttribute("expedientTipus")
	public ExpedientTipus populateExpedientTipus(
			@RequestParam(value = "expedientTipusId", required = false) Long id) {
		if (id != null) {
			return dissenyService.getExpedientTipusById(id);
		}
		return null;
	}

	@RequestMapping(value = "/expedientTipus/llistat")
	public String llistat(
			HttpServletRequest request,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			model.addAttribute("llistat", llistatExpedientTipusAmbPermisos(entorn));
			return "expedientTipus/llistat";
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/expedientTipus/delete")
	public String delete(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long id) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientTipus expedientTipus = dissenyService.getExpedientTipusById(id);
			if (potDissenyarExpedientTipus(entorn, expedientTipus)) {
				try {
					dissenyService.deleteExpedientTipus(id);
					missatgeInfo(request, "El tipus d'expedient s'ha esborrat correctament");
				} catch (Exception ex) {
					missatgeError(request, "No s'ha pogut esborrar el tipus d'expedient", ex.getLocalizedMessage());
		        	logger.error("No s'ha pogut esborrar el tipus d'expedient", ex);
				}
				return "redirect:/expedientTipus/llistat.html";
			} else {
				missatgeError(request, "No té permisos de disseny sobre aquest tipus d'expedient");
				return "redirect:/index.html";
			}
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/expedientTipus/info")
	public String info(
			HttpServletRequest request,
			@RequestParam(value = "expedientTipusId", required = true) Long id,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientTipus expedientTipus = dissenyService.getExpedientTipusById(id);
			if (potDissenyarExpedientTipus(entorn, expedientTipus)) {
				model.addAttribute(
						"responsableDefecte",
						getResponsableDefecte(expedientTipus.getResponsableDefecteCodi()));
				model.addAttribute(
						"definicioProcesInicial",
						dissenyService.findDarreraDefinicioProcesForExpedientTipus(id, false));
				return "expedientTipus/info";
			} else {
				missatgeError(request, "No té permisos de disseny sobre aquest tipus d'expedient");
				return "redirect:/index.html";
			}
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/expedientTipus/definicioProcesLlistat")
	public String definicioProcesLlistat(
			HttpServletRequest request,
			@RequestParam(value = "expedientTipusId", required = true) Long id,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientTipus expedientTipus = dissenyService.getExpedientTipusById(id);
			if (potDissenyarExpedientTipus(entorn, expedientTipus)) {
				model.addAttribute(
						"llistat",
						dissenyService.findDarreresAmbExpedientTipusIGlobalsEntorn(entorn.getId(), id));
				return "expedientTipus/definicioProcesLlistat";
			} else {
				missatgeError(request, "No té permisos de disseny sobre aquest tipus d'expedient");
				return "redirect:/index.html";
			}
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/expedientTipus/definicioProcesInicial")
	public String definicioProcesInicial(
			HttpServletRequest request,
			@RequestParam(value = "expedientTipusId", required = true) Long id,
			@RequestParam(value = "jbpmKey", required = true) String jbpmKey,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientTipus expedientTipus = dissenyService.getExpedientTipusById(id);
			if (potDissenyarExpedientTipus(entorn, expedientTipus)) {
				try {
					dissenyService.setDefinicioProcesInicialPerExpedientTipus(id, jbpmKey);
					missatgeInfo(request, "La definició de procés '" + jbpmKey + "' s'ha marcat com a inicial");
				} catch (Exception ex) {
					missatgeError(request, "No s'ha pogut configurar la definició de procés inicial", ex.getLocalizedMessage());
		        	logger.error("No s'ha pogut configurar la definició de procés inicial", ex);
				}
				return "redirect:/expedientTipus/definicioProcesLlistat.html?expedientTipusId=" + id;
			} else {
				missatgeError(request, "No té permisos de disseny sobre aquest tipus d'expedient");
				return "redirect:/index.html";
			}
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/expedientTipus/definicioProcesEsborrar")
	public String definicioProcesEsborrar(
			HttpServletRequest request,
			@RequestParam(value = "expedientTipusId", required = true) Long id,
			@RequestParam(value = "definicioProcesId", required = true) Long definicioProcesId,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientTipus expedientTipus = dissenyService.getExpedientTipusById(id);
			if (potDissenyarExpedientTipus(entorn, expedientTipus)) {
				DefinicioProcesDto definicioProces = dissenyService.getByIdAmbComprovacio(entorn.getId(), definicioProcesId);
				if (potDissenyarDefinicioProces(entorn, definicioProces)) {
					try {
						List<ExpedientDto> expedients = expedientService.findAmbDefinicioProcesId(definicioProcesId);
						if (expedients.size() == 0) {
							dissenyService.undeploy(entorn.getId(), null, definicioProcesId);
				        	missatgeInfo(request, "La definició de procés s'ha esborrat correctament");
						} else {
							missatgeError(request, "Existeixen expedients amb aquesta definició de procés");
						}
			        } catch (Exception ex) {
			        	missatgeError(request, "S'ha produït un error processant la seva petició", ex.getLocalizedMessage());
			        	logger.error("No s'ha pogut esborrar la definició de procés", ex);
			        	return "redirect:/definicioProces/info.html?definicioProcesId=" + definicioProcesId;
			        }
					return "redirect:/expedientTipus/definicioProcesLlistat.html?expedientTipusId=" + id;
				} else {
					missatgeError(request, "No té permisos de disseny sobre aquesta definició de procés");
					return "redirect:/index.html";
				}
			} else {
				missatgeError(request, "No té permisos de disseny sobre aquest tipus d'expedient");
				return "redirect:/index.html";
			}
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
			return "redirect:/index.html";
		}
	}



	private Persona getResponsableDefecte(String codi) {
		if (codi == null)
			return null;
		return pluginService.findPersonaAmbCodi(codi);
	}

	private List<ExpedientTipus> llistatExpedientTipusAmbPermisos(Entorn entorn) {
		List<ExpedientTipus> resposta = new ArrayList<ExpedientTipus>();
		List<ExpedientTipus> llistat = dissenyService.findExpedientTipusAmbEntorn(entorn.getId());
		for (ExpedientTipus expedientTipus: llistat) {
			if (potDissenyarExpedientTipus(entorn, expedientTipus))
				resposta.add(expedientTipus);
		}
		return resposta;
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
	private boolean potDissenyarDefinicioProces(Entorn entorn, DefinicioProcesDto definicioProces) {
		if (potDissenyarEntorn(entorn))
			return true;
		if (definicioProces.getExpedientTipus() != null) {
			return permissionService.filterAllowed(
					definicioProces.getExpedientTipus(),
					ExpedientTipus.class,
					new Permission[] {
						ExtendedPermission.ADMINISTRATION,
						ExtendedPermission.DESIGN}) != null;
		}
		return false;
	}
	private boolean potDissenyarEntorn(Entorn entorn) {
		return permissionService.filterAllowed(
				entorn,
				Entorn.class,
				new Permission[] {
					ExtendedPermission.ADMINISTRATION,
					ExtendedPermission.DESIGN}) != null;
	}

	private static final Log logger = LogFactory.getLog(ExpedientTipusController.class);

}
