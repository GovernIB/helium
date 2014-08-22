/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.v3.core.api.service.PluginService;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientEditarCommand;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;

/**
 * Controlador per a la pàgina d'informació de l'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedient")
public class ExpedientInformacioController extends BaseExpedientController {

	@Autowired
	private ExpedientService expedientService;

	@Autowired
	private PluginService pluginService;

	@RequestMapping(value = "/{expedientId}/persona/suggest", method = RequestMethod.GET)
	public String suggestAction(
			@RequestParam(value = "q", required = true) String text, 
			ModelMap model) {
		model.addAttribute("persones", pluginService.findPersonaLikeNomSencer(text));
		return "persona/suggest";
	}
	
	public ExpedientEditarCommand populateCommand(ExpedientDto expedient) {		
		ExpedientEditarCommand expedientEditarCommand = new ExpedientEditarCommand();
		expedientEditarCommand.setNumero(expedient.getNumero());
		expedientEditarCommand.setTitol(expedient.getTitol());
		expedientEditarCommand.setComentari(expedient.getComentari());
		expedientEditarCommand.setDataInici(expedient.getDataInici());
		expedientEditarCommand.setEstatId(expedient.getEstat() != null ? expedient.getEstat().getId() : null);
		expedientEditarCommand.setExpedientId(expedient.getId());
		expedientEditarCommand.setGeoPosX(expedient.getGeoPosX());
		expedientEditarCommand.setGeoPosY(expedient.getGeoPosY());
		expedientEditarCommand.setGeoReferencia(expedient.getGeoReferencia());
		expedientEditarCommand.setGrupCodi(expedient.getGrupCodi());
		PersonaDto personaIniciador = pluginService.findPersonaAmbCodi(expedient.getIniciadorCodi());
		expedientEditarCommand.setIniciadorCodi(personaIniciador.getCodi());
		PersonaDto personaResponsable = pluginService.findPersonaAmbCodi(expedient.getResponsableCodi());
		expedient.setResponsablePersona(personaResponsable);
		expedientEditarCommand.setResponsableCodi(personaResponsable.getCodi());
		expedientEditarCommand.setResponsableNomSencer(personaResponsable.getNomSencer());
		
		return expedientEditarCommand;
	}

	@RequestMapping(value = "/{expedientId}/modificar", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@ModelAttribute("expedientEditarCommand") ExpedientEditarCommand filtreCommand,
			BindingResult result,
			SessionStatus status,
			ModelMap model)  {
		EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
		if (entorn != null) {
			ExpedientDto expedient = expedientService.findById(expedientId);
			if (potModificarExpedient(expedient)) {
				model.addAttribute("estats", dissenyService.findEstatByExpedientTipus(expedient.getTipus().getId()));
				model.addAttribute("expedient", expedient); 
				model.addAttribute(populateCommand(expedient));
			} else {
				MissatgesHelper.info(request, getMessage(request, "error.permisos.modificar.expedient"));
			}
		} else {
			MissatgesHelper.error(request, getMessage(request, "error.no.entorn.selec"));
		}
		return "v3/expedient/modificarInformacio";
	}

	@RequestMapping(value = "/{expedientId}/modificarInformacio", method = RequestMethod.POST)
	public String modificar(HttpServletRequest request, 
			@PathVariable Long expedientId, 
			Model model, 
			@ModelAttribute("expedientEditarCommand") ExpedientEditarCommand command, 
			BindingResult result, SessionStatus status) {
		EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
		if (entorn != null) {
			ExpedientDto expedient = expedientService.findById(expedientId);
			if (potModificarExpedient(expedient)) {
				new ExpedientEditarValidator().validate(command, result);
				if (result.hasErrors()) {
					model.addAttribute("estats", dissenyService.findEstatByExpedientTipus(expedient.getTipus().getId()));
					model.addAttribute("expedient", expedient); 
					model.addAttribute(command);
					return "v3/expedient/modificarInformacio";
				}
				try {
					expedientService.editar(entorn.getId(), command.getExpedientId(), command.getNumero(), command.getTitol(), command.getResponsableCodi(), command.getDataInici(), command.getComentari(), command.getEstatId(), command.getGeoPosX(), command.getGeoPosY(), command.getGeoReferencia(), command.getGrupCodi());
					MissatgesHelper.info(request, getMessage(request, "info.informacio.modificat"));
				} catch (Exception ex) {
					Long entornId = entorn.getId();
					String numeroExpedient = expedient.getIdentificador();
					logger.error("ENTORNID:" + entornId + " NUMEROEXPEDIENT:" + numeroExpedient + " No s'han pogut modificar les dades de l'expedient", ex);
					MissatgesHelper.error(request, getMessage(request, "error.modificar.dades.exp"));
					return "expedient/editar";
				}
			} else {
				MissatgesHelper.info(request, getMessage(request, "error.permisos.modificar.expedient"));
			}
		} else {
			MissatgesHelper.error(request, getMessage(request, "error.no.entorn.selec"));
		}
		return modalUrlTancar();
	}

	private class ExpedientEditarValidator implements Validator {
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public boolean supports(Class clazz) {
			return clazz.isAssignableFrom(ExpedientEditarCommand.class);
		}

		public void validate(Object target, Errors errors) {
			ExpedientEditarCommand command = (ExpedientEditarCommand) target;
			ExpedientDto expedient = expedientService.findById(command.getExpedientId());
			if (expedient.getTipus().isTeTitol())
				ValidationUtils.rejectIfEmpty(errors, "titol", "not.blank");
			if (expedient.getTipus().isTeNumero())
				ValidationUtils.rejectIfEmpty(errors, "numero", "not.blank");
			ValidationUtils.rejectIfEmpty(errors, "dataInici", "not.blank");
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(ExpedientInformacioController.class);
}
