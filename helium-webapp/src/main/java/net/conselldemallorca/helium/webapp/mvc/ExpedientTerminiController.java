/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import java.util.Date;

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
 * Controlador per la gestió dels terminis als expedients
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
public class ExpedientTerminiController extends BaseController {

	private TerminiService terminiService;
	private DissenyService dissenyService;
	private ExpedientService expedientService;
	private PermissionService permissionService;



	@Autowired
	public ExpedientTerminiController(
			TerminiService terminiService,
			DissenyService dissenyService,
			ExpedientService expedientService,
			PermissionService permissionService) {
		this.terminiService = terminiService;
		this.dissenyService = dissenyService;
		this.expedientService = expedientService;
		this.permissionService = permissionService;
	}

	

	@RequestMapping(value = "/expedient/terminis")
	public String terminis(
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
				InstanciaProcesDto instanciaProces = expedientService.getInstanciaProcesById(id, true);
				model.addAttribute(
						"instanciaProces",
						instanciaProces);
				model.addAttribute(
						"terminis",
						dissenyService.findTerminisAmbDefinicioProces(instanciaProces.getDefinicioProces().getId()));
				model.addAttribute(
						"iniciats",
						terminiService.findIniciatsAmbProcessInstanceId(id));
				return "expedient/terminis";
			} else {
				missatgeError(request, getMessage("error.permisos.consultar.expedient"));
				return "redirect:/expedient/consulta.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/expedient/terminiIniciar")
	public String terminiIniciar(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "terminiId", required = true) Long terminiId,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			try {
				terminiService.iniciar(
						terminiId,
						id,
						new Date(),
						true);
				missatgeInfo(request, getMessage("info.termini.iniciat") );
			} catch (Exception ex) {
				missatgeError(request, getMessage("error.iniciar.termini"), ex.getLocalizedMessage());
	        	logger.error("No s'ha pogut iniciar el termini", ex);
			}
			return "redirect:/expedient/terminis.html?id=" + id;
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}
	@RequestMapping(value = "/expedient/terminiPausar")
	public String terminiPausar(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "terminiId", required = true) Long terminiId,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			try {
				terminiService.pausar(terminiId, new Date());
				missatgeInfo(request, getMessage("info.termini.aturat") );
			} catch (Exception ex) {
				missatgeError(request, getMessage("error.aturar.termini"), ex.getLocalizedMessage());
	        	logger.error("No s'ha pogut aturar el termini", ex);
			}
			return "redirect:/expedient/terminis.html?id=" + id;
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}
	@RequestMapping(value = "/expedient/terminiContinuar")
	public String terminiContinuar(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "terminiId", required = true) Long terminiId,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			try {
				terminiService.continuar(terminiId, new Date());
				missatgeInfo(request, getMessage("info.termini.continuat") );
			} catch (Exception ex) {
				missatgeError(request, getMessage("error.continuar.termini"), ex.getLocalizedMessage());
	        	logger.error("No s'ha pogut continuar el termini", ex);
			}
			return "redirect:/expedient/terminis.html?id=" + id;
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}
	@RequestMapping(value = "/expedient/terminiCancelar")
	public String terminiCancelar(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "terminiId", required = true) Long terminiId,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			try {
				terminiService.cancelar(terminiId, new Date());
				missatgeInfo(request, getMessage("info.termini.cancelat") );
			} catch (Exception ex) {
				missatgeError(request, getMessage("error.cancelar.termini"), ex.getLocalizedMessage());
	        	logger.error("No s'ha pogut cancel·lar el termini", ex);
			}
			return "redirect:/expedient/terminis.html?id=" + id;
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

	private static final Log logger = LogFactory.getLog(ExpedientTerminiController.class);

}
