/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.v3.core.api.service.PluginService;
import net.conselldemallorca.helium.webapp.mvc.ArxiuView;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientEditarCommand;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;

import org.json.simple.JSONValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controlador per a la pàgina d'informació de l'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedient")
public class ExpedientV3Controller extends BaseExpedientController {

	@Autowired
	private ExpedientService expedientService;

	@Autowired
	private PluginService pluginService;

	@RequestMapping(value = "/{expedientId}", method = RequestMethod.GET)
	public String info(
			HttpServletRequest request, 
			@PathVariable Long expedientId, 
			Model model) {
		return mostrarInformacioExpedientPerPipella(request, expedientId, model, null, expedientService);
	}

	@RequestMapping(value = "/{expedientId}/modificar", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			ModelMap model)  {
		ExpedientDto expedient = expedientService.findAmbId(expedientId);
		model.addAttribute("expedient", expedient);
		model.addAttribute(
				"estats",
				dissenyService.findEstatByExpedientTipus(
						expedient.getTipus().getId()));
		model.addAttribute(getCommandModificar(expedient));
		return "v3/expedient/modificarInformacio";
	}

	@RequestMapping(value = "/{expedientId}/modificar", method = RequestMethod.POST)
	public String modificar(
			HttpServletRequest request, 
			@PathVariable Long expedientId, 
			@Valid ExpedientEditarCommand command,
			BindingResult bindingResult,
			Model model) {
		new ExpedientEditarValidator().validate(command, bindingResult);
		if (bindingResult.hasErrors()) {
			return "v3/expedient/modificar";
		}
		expedientService.update(
				command.getExpedientId(),
				command.getNumero(),
				command.getTitol(),
				command.getResponsableCodi(),
				command.getDataInici(),
				command.getComentari(),
				command.getEstatId(),
				command.getGeoPosX(),
				command.getGeoPosY(),
				command.getGeoReferencia(),
				command.getGrupCodi(),
				false);
		MissatgesHelper.info(
				request,
				getMessage(
						request,
						"info.informacio.modificat"));
		return modalUrlTancar();
	}

	@RequestMapping(value = "/{expedientId}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable Long expedientId) {
		expedientService.delete(expedientId);
		MissatgesHelper.info(
				request,
				getMessage(
						request,
						"info.expedient.esborrat"));
		return "redirect:/v3/expedient";
	}

	@RequestMapping(value = "/{expedientId}/imatgeDefProces", method = RequestMethod.GET)
	public String imatgeProces(
			HttpServletRequest request,
			@PathVariable(value = "expedientId") Long expedientId, 
			Model model) {
		ArxiuDto imatge = expedientService.getImatgeDefinicioProces(
				expedientId,
				null);
		model.addAttribute(
				ArxiuView.MODEL_ATTRIBUTE_FILENAME,
				imatge.getNom());
		model.addAttribute(
				ArxiuView.MODEL_ATTRIBUTE_DATA,
				imatge.getContingut());
		return "arxiuView";
	}
	
	@RequestMapping(value = "/{expedientId}/updateDefinicioProces/{versio}", method = RequestMethod.GET)
	@ResponseBody
	public String changeDefProc(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable int versio,
			ModelMap model) {
		String nom = null;
		try {
			nom = expedientService.canviVersioDefinicioProces(
					expedientId,
					versio);
			MissatgesHelper.info(request, getMessage(request, "info.canvi.versio.realitzat") );
		} catch (Exception ex) {
			MissatgesHelper.error(request, getMessage(request, "error.canviar.versio.proces"));
		}
	        	
		return JSONValue.toJSONString(nom);
	}

	private ExpedientEditarCommand getCommandModificar(ExpedientDto expedient) {		
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

	private class ExpedientEditarValidator implements Validator {
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public boolean supports(Class clazz) {
			return clazz.isAssignableFrom(ExpedientEditarCommand.class);
		}

		public void validate(Object target, Errors errors) {
			ExpedientEditarCommand command = (ExpedientEditarCommand) target;
			ExpedientDto expedient = expedientService.findAmbId(command.getExpedientId());
			if (expedient.getTipus().isTeTitol())
				ValidationUtils.rejectIfEmpty(errors, "titol", "not.blank");
			if (expedient.getTipus().isTeNumero())
				ValidationUtils.rejectIfEmpty(errors, "numero", "not.blank");
			ValidationUtils.rejectIfEmpty(errors, "dataInici", "not.blank");
		}
	}
}
