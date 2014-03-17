/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.v3.core.api.service.PluginService;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientEditarCommand;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.NoDecorarHelper;
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

	@RequestMapping(value = "/{expedientId}/modificar", method = RequestMethod.GET)
	public String modificarInformacio(
			HttpServletRequest request, 
			@PathVariable Long expedientId, 
			Model model) {
		NoDecorarHelper.marcarNoCapsaleraNiPeu(request);
		model.addAttribute("expedientId", expedientId);		
		
		EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
		if (entorn != null) {
			ExpedientDto expedient = expedientService.findById(expedientId);
				if (potModificarExpedient(expedient)) {
				model.addAttribute(
						"command",
						initCommand(expedient));
				dadesPaginaEditar(expedient, model);				
			}else {
				MissatgesHelper.info(request, getMessage(request, "error.permisos.modificar.expedient"));
			}
		} else {
			MissatgesHelper.error(request, getMessage(request, "error.no.entorn.selec"));
		}
		return "v3/expedient/modificarInformacio";
	}
	
	private ExpedientEditarCommand initCommand(ExpedientDto expedient) {
		ExpedientEditarCommand command = new ExpedientEditarCommand();
		command.setExpedientId(expedient.getId());
		command.setNumero(expedient.getNumero());
		command.setTitol(expedient.getTitol());
		command.setComentari(expedient.getComentari());
		command.setDataInici(expedient.getDataInici());
		command.setIniciadorCodi(expedient.getIniciadorCodi());
		command.setResponsableCodi(expedient.getResponsableCodi());
		if (expedient.getEstat() != null)
			command.setEstatId(expedient.getEstat().getId());
		command.setGeoPosX(expedient.getGeoPosX());
		command.setGeoPosY(expedient.getGeoPosY());
		command.setGeoReferencia(expedient.getGeoReferencia());
		command.setGrupCodi(expedient.getGrupCodi());
		return command;
	}

	@RequestMapping(value = "/{expedientId}/modificarInformacio", method = RequestMethod.POST)
	public String modificar(
			HttpServletRequest request, 
			@PathVariable Long expedientId, 
			Model model, 
			@ModelAttribute("aturarExpedient") ExpedientEditarCommand command, 
			BindingResult result, 
			SessionStatus status) {
		EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
		if (entorn != null) {
			ExpedientDto expedient = expedientService.findById(expedientId);
				if (potModificarExpedient(expedient)) {
					new ExpedientEditarValidator().validate(command, result);
			        if (!result.hasErrors()) {
						try {
							expedientService.editar(
									entorn.getId(),
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
									command.getGrupCodi());
							MissatgesHelper.info(request, getMessage(request, "info.informacio.modificat"));
						} catch (Exception ex) {
							Long entornId = entorn.getId();
							String numeroExpedient = expedient.getIdentificador();
							logger.error("ENTORNID:"+entornId+" NUMEROEXPEDIENT:"+numeroExpedient+" No s'han pogut modificar les dades de l'expedient", ex);
							MissatgesHelper.error(request, getMessage(request, "error.modificar.dades.exp"));
				        	dadesPaginaEditar(expedient, model);
				        	return "expedient/editar";
						}
			        }
				} else {
					MissatgesHelper.info(request, getMessage(request, "error.permisos.modificar.expedient"));
				}
		} else {
			MissatgesHelper.error(request, getMessage(request, "error.no.entorn.selec"));
		}
		mostrarInformacioExpedientPerPipella(request, expedientId, model, null, expedientService);
		return "v3/utils/modalTancar";
	}

	private class ExpedientEditarValidator implements Validator {
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public boolean supports(Class clazz) {
			return clazz.isAssignableFrom(ExpedientEditarCommand.class);
		}
		public void validate(Object target, Errors errors) {
			ExpedientEditarCommand command = (ExpedientEditarCommand)target;
			ExpedientDto expedient = expedientService.findById(command.getExpedientId());
			if (expedient.getTipus().isTeTitol())
				ValidationUtils.rejectIfEmpty(errors, "titol", "not.blank");
			if (expedient.getTipus().isTeNumero())
				ValidationUtils.rejectIfEmpty(errors, "numero", "not.blank");
			ValidationUtils.rejectIfEmpty(errors, "dataInici", "not.blank");
		}
	}

	private void dadesPaginaEditar(
			ExpedientDto expedient,
			Model model) {
		ExpedientEditarCommand command = (ExpedientEditarCommand)model.asMap().get("command");
		Long procesInstanceId = Long.valueOf(expedient.getProcessInstanceId());
		PersonaDto persona = pluginService.findPersonaAmbCodi(command.getResponsableCodi());
		expedient.setResponsablePersona(persona);
		model.addAttribute(
				"expedient",
				expedient);
		model.addAttribute(
				"arbreProcessos",
				expedientService.getArbreInstanciesProces(procesInstanceId));
		InstanciaProcesDto instanciaProces = expedientService.getInstanciaProcesById(expedient.getProcessInstanceId());
		model.addAttribute(
				"instanciaProces",
				instanciaProces);
		if (command.getIniciadorCodi() != null)
			model.addAttribute(
					"iniciador",
					pluginService.findPersonaAmbCodi(command.getIniciadorCodi()));
		if (command.getResponsableCodi() != null)
			model.addAttribute(
					"responsable",
					persona);
	}

	private static final Logger logger = LoggerFactory.getLogger(ExpedientInformacioController.class);

}
