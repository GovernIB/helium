/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.v3.core.service.PluginServiceImpl;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientEditarCommand;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientEinesAturarCommand;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.NoDecorarHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;

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
public class ExpedientPipellesController extends BaseExpedientController {

	@Resource(name = "expedientServiceV3")
	private ExpedientService expedientService;
	
	@Resource(name = "pluginServiceV3")
	private PluginServiceImpl pluginService;

	@RequestMapping(value = "/{expedientId}", method = RequestMethod.GET)
	public String info(HttpServletRequest request, @PathVariable Long expedientId, Model model) {
		return mostrarInformacioExpedientPerPipella(request, expedientId, model, null, expedientService);
	}

	@RequestMapping(value = "/{expedientId}/stop", method = RequestMethod.GET)
	public String aturarForm(HttpServletRequest request, @PathVariable Long expedientId, Model model) {
		NoDecorarHelper.marcarNoCapsaleraNiPeu(request);
		model.addAttribute("expedientId", expedientId);
		return "v3/expedientFormAturar";
	}
	
	@RequestMapping(value = "/{expedientId}/persona/suggest", method = RequestMethod.GET)
	public String suggestAction(
			@RequestParam(value = "q", required = true) String text,
			ModelMap model) {
		model.addAttribute("persones", pluginService.findPersonaLikeNomSencer(text));
		return "persona/suggest";
	}

	@RequestMapping(value = "/{expedientId}/modificar", method = RequestMethod.GET)
	public String modificarInformacio(HttpServletRequest request, @PathVariable Long expedientId, Model model) {
		NoDecorarHelper.marcarNoCapsaleraNiPeu(request);
		model.addAttribute("expedientId", expedientId);
		ExpedientDto expedient = expedientService.findById(expedientId);
		model.addAttribute("expedient", expedient);
		return "v3/expedientFormModificarInformacio";
	}

	@RequestMapping(value = "/{expedientId}/modificarInformacio", method = RequestMethod.POST)
	public String modificar(HttpServletRequest request, @PathVariable Long expedientId, Model model, @ModelAttribute("aturarExpedient") ExpedientEditarCommand command, BindingResult result, SessionStatus status) {
		EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
		if (entorn != null) {
			ExpedientDto expedient = expedientService.findById(expedientId);
				if (potModificarExpedient(expedient)) {
					new ExpedientEditarValidator().validate(command, result);
			        if (result.hasErrors()) {
			        	dadesPaginaEditar(expedientId, expedient, model);
			        	return "expedient/editar";
			        }
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
						MissatgesHelper.info(request, "info.informacio.modificat" );
					} catch (Exception ex) {
						Long entornId = entorn.getId();
						String numeroExpedient = expedient.getIdentificador();
//							logger.error("ENTORNID:"+entornId+" NUMEROEXPEDIENT:"+numeroExpedient+" No s'han pogut modificar les dades de l'expedient", ex);
						MissatgesHelper.error(request, "error.modificar.dades.exp");
			        	dadesPaginaEditar(expedientId, expedient, model);
			        	return "expedient/editar";
					}
				} else {
					MissatgesHelper.info(request, "error.permisos.modificar.expedient");
				}
		} else {
			MissatgesHelper.error(request, "error.no.entorn.selec");
		}
		mostrarInformacioExpedientPerPipella(request, expedientId, model, null, expedientService);
		return "v3/utils/modalTancar";
	}

	@RequestMapping(value = "/{expedientId}/aturarExpedient", method = RequestMethod.POST)
	public String aturar(HttpServletRequest request, @PathVariable Long expedientId, Model model, @ModelAttribute("aturarExpedient") ExpedientEinesAturarCommand aturarExpedient, BindingResult result, SessionStatus status) {
		EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
		if (entorn != null) {
			ExpedientDto expedient = expedientService.findById(expedientId);
			if (potModificarExpedient(expedient)) {
				if (!expedient.isAturat()) {
					new ExpedientAturarValidator().validate(aturarExpedient, result);
					if (!result.hasErrors()) {
						try {
							expedientService.aturar(expedientId, aturarExpedient.getMotiu());
							MissatgesHelper.info(request, "info.expedient.aturat");
						} catch (Exception ex) {
							// Long entornId = entorn.getId();
							// log.error("ENTORNID:"+entornId+" NUMEROEXPEDIENT:"+expedientId+" No s'ha pogut aturar l'expedient",ex);
							MissatgesHelper.error(request, "error.aturar.expedient");
							ex.getLocalizedMessage();
						}
					}
				} else {
					MissatgesHelper.error(request, "error.expedient.ja.aturat");
				}
			} else {
				MissatgesHelper.error(request, "error.permisos.modificar.expedient");
			}
		} else {
			MissatgesHelper.error(request, "error.no.entorn.selec");
		}
		mostrarInformacioExpedientPerPipella(request, expedientId, model, null, expedientService);
		return "v3/utils/modalTancar";
	}

	private class ExpedientAturarValidator implements Validator {
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public boolean supports(Class clazz) {
			return clazz.isAssignableFrom(ExpedientEinesAturarCommand.class);
		}

		public void validate(Object target, Errors errors) {
			ValidationUtils.rejectIfEmpty(errors, "motiu", "not.blank");
		}
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
			Long id,
			ExpedientDto expedient,
			Model model) {
		ExpedientEditarCommand command = (ExpedientEditarCommand)model.asMap().get("command");
		model.addAttribute(
				"expedient",
				expedient);
		model.addAttribute(
				"arbreProcessos",
				expedientService.getArbreInstanciesProces(id));
		InstanciaProcesDto instanciaProces = expedientService.getInstanciaProcesById(id, false, false, false);
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
					pluginService.findPersonaAmbCodi(command.getResponsableCodi()));
	}
}
