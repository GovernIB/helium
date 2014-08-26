/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTascaDto;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.v3.core.api.service.TascaService;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.NodecoHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controlador per a la pàgina d'informació de l'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedient")
public class ExpedientTascaController extends BaseExpedientController {

	@Autowired
	protected ExpedientService expedientService;
	@Autowired
	protected TascaService tascaService;

	@RequestMapping(value = "/{expedientId}/tasca", method = RequestMethod.GET)
	public String tasques(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {
		if (!NodecoHelper.isNodeco(request)) {
			return mostrarInformacioExpedientPerPipella(
					request,
					expedientId,
					model,
					"tasques",
					expedientService);
		}
		model.addAttribute("expedientId", expedientId);
		List<ExpedientTascaDto> tasques = expedientService.findTasques(
				expedientId);
		model.addAttribute("tasques", tasques);
		/*model.addAttribute(
				"expedientLogIds",
				expedientService.findLogIdTasquesById(tasques));*/
		return "v3/expedientTasca";
	}

	@RequestMapping(value = "/{expedientId}/tasquesPendents", method = RequestMethod.GET)
	public String tasquesPendents(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {
		if (!NodecoHelper.isNodeco(request)) {
			return mostrarInformacioExpedientPerPipella(
					request,
					expedientId,
					model,
					"tasques",
					expedientService);
		}
		model.addAttribute(
				"tasques",
				expedientService.findTasquesPendents(
						expedientId));
		return "v3/expedientTasquesPendents";
	}

	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/cancelar")
	public String tascaCancelar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable Long tascaId,
			ModelMap model) {
		EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();	
		if (entorn != null) {
			ExpedientDto expedient = expedientService.findById(expedientId);
			if (potModificarExpedient(expedient)) {
				try {
					// TODO
					/*expedientService.cancelarTasca(entorn.getId(), String.valueOf(tascaId));*/
				} catch (Exception ex) {
					MissatgesHelper.error(request, getMessage(request, "error.cancelar.tasca", new Object[] {String.valueOf(tascaId)} ));
		        	logger.error("No s'ha pogut cancel·lar la tasca " + String.valueOf(tascaId), ex);
				}
			} else {
				MissatgesHelper.error(request, getMessage(request, "error.permisos.modificar.expedient"));
			}
		} else {
			MissatgesHelper.error(request, getMessage(request, "error.no.entorn.selec"));			
		}
		return "redirect:/v3/expedient/" + expedientId;
	}

	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/suspendre")
	public String tascaSuspendre(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable Long tascaId,
			ModelMap model) {
		EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();	
		if (entorn != null) {
			ExpedientDto expedient = expedientService.findById(expedientId);
			if (potModificarExpedient(expedient)) {
				try {
					// TODO
					/*expedientService.suspendreTasca(entorn.getId(), String.valueOf(tascaId));*/
				} catch (Exception ex) {
					MissatgesHelper.error(request, getMessage(request, "error.suspendre.tasca", new Object[] {tascaId} ));
		        	logger.error("No s'ha pogut suspendre la tasca " + tascaId, ex);
				}
			} else {
				MissatgesHelper.error(request, getMessage(request, "error.permisos.modificar.expedient"));
			}
		} else {
			MissatgesHelper.error(request, getMessage(request, "error.no.entorn.selec"));			
		}
		return "redirect:/v3/expedient/" + expedientId;
	}

	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/reprendre")
	public String tascaReprendre(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable Long tascaId,
			ModelMap model) {
		EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();	
		if (entorn != null) {
			ExpedientDto expedient = expedientService.findById(expedientId);
			if (potModificarExpedient(expedient)) {
				try {
					// TODO
					/*expedientService.reprendreTasca(entorn.getId(), String.valueOf(tascaId));*/
				} catch (Exception ex) {
					MissatgesHelper.error(request, getMessage(request, "error.reprendre.tasca", new Object[] {tascaId} ));
		        	logger.error("No s'ha pogut reprendre la tasca " + tascaId, ex);
				}
			} else {
				MissatgesHelper.error(request, getMessage(request, "error.permisos.modificar.expedient"));
			}
		} else {
			MissatgesHelper.error(request, getMessage(request, "error.no.entorn.selec"));			
		}
		
		return "redirect:/v3/expedient/"+expedientId;
	}

	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/delegacioCancelar", method = RequestMethod.POST)
	public String cancelar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable Long tascaId,
			ModelMap model) {
		EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();	
		if (entorn != null) {
			try {
				tascaService.delegacioCancelar(entorn.getId(), String.valueOf(tascaId));
				MissatgesHelper.info(request, getMessage(request, "info.delegacio.cancelat") );
			} catch (Exception ex) {
				MissatgesHelper.error(request, getMessage(request, "error.proces.peticio"));
	        	logger.error("No s'ha pogut cancel·lar la delegació de la tasca " + tascaId, ex);
			}
		} else {
			MissatgesHelper.error(request, getMessage(request, "error.no.entorn.selec"));			
		}
		
		return "redirect:/v3/expedient/"+expedientId;
	}
	
	private static final Logger logger = LoggerFactory.getLogger(ExpedientTascaController.class);
}
