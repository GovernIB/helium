package net.conselldemallorca.helium.webapp.v3.controller;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import net.conselldemallorca.helium.v3.core.api.dto.EstatDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;
import net.conselldemallorca.helium.v3.core.api.service.AplicacioService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientEditarCommand;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientEditarCommand.Editar;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.ObjectTypeEditorHelper;

/**
 * Controlador per a la pàgina d'informació de l'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedient")
public class ExpedientInformacioController extends BaseExpedientController {

	/** Constant per indicar l'id de l'estat finalitzat. */
	private static final Long ESTAT_FINALITZAT_ID = -1L;
	
	@Autowired
	private ExpedientService expedientService;
	@Autowired
	private AplicacioService aplicacioService;

	@RequestMapping(value = "/{expedientId}/modificar", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			ModelMap model)  {
		ExpedientDto expedient = expedientService.findAmbIdAmbPermis(expedientId);
		model.addAttribute("expedient", expedient);
		List<EstatDto> estats = expedientTipusService.estatFindAll(
				expedient.getTipus().getId(),
				true);
//		estats.add(0, new EstatDto(0L, "0", getMessage(request, "expedient.consulta.iniciat")));
		estats.add(new EstatDto(ESTAT_FINALITZAT_ID, "-1", getMessage(request, "expedient.consulta.finalitzat")));
		model.addAttribute("estats", estats);
		model.addAttribute(getCommandModificar(expedient));
		return "v3/expedient/modificarInformacio";
	}

	@RequestMapping(value = "/{expedientId}/modificar", method = RequestMethod.POST)
	public String modificar(
			HttpServletRequest request, 
			@PathVariable Long expedientId, 
			@Validated(Editar.class) ExpedientEditarCommand command,
			BindingResult bindingResult,
			Model model) {
		new ExpedientEditarValidator().validate(command, bindingResult);
		if (bindingResult.hasErrors()) {
			ExpedientDto expedient = expedientService.findAmbIdAmbPermis(expedientId);
			model.addAttribute("expedient", expedient);
			List<EstatDto> estats = expedientTipusService.estatFindAll(expedient.getTipus().getId(), true);
			estats.add(new EstatDto(ESTAT_FINALITZAT_ID, "-1", getMessage(request, "expedient.consulta.finalitzat")));
			model.addAttribute("estats", estats);
			return "v3/expedient/modificarInformacio";
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
		MissatgesHelper.success(
				request,
				getMessage(
						request,
						"info.informacio.modificat"));
		return modalUrlTancar();
	}
	
	private class ExpedientEditarValidator implements Validator {
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public boolean supports(Class clazz) {
			return clazz.isAssignableFrom(ExpedientEditarCommand.class);
		}

		public void validate(Object target, Errors errors) {
			ExpedientEditarCommand command = (ExpedientEditarCommand) target;
			ExpedientDto expedient = expedientService.findAmbIdAmbPermis(command.getExpedientId());
			if (expedient.getTipus().isTeTitol())
				ValidationUtils.rejectIfEmpty(errors, "titol", "not.blank");
			if (expedient.getTipus().isTeNumero())
				ValidationUtils.rejectIfEmpty(errors, "numero", "not.blank");
			ValidationUtils.rejectIfEmpty(errors, "dataInici", "not.blank");
			// No es pot modificar l'estat finalitzat d'un estat amb data de fi
			if (expedient.getDataFi() != null && command.getEstatId() == null ) {
				errors.rejectValue("estatId", "expedient.info.validacio.estat.finalitzat");
				command.setEstatId(ESTAT_FINALITZAT_ID);
			}
		}
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
		expedientEditarCommand.setIniciadorCodi(expedient.getIniciadorCodi());
		PersonaDto personaResponsable = aplicacioService.findPersonaAmbCodi(expedient.getResponsableCodi());
		expedient.setResponsablePersona(personaResponsable);
		if (personaResponsable != null) {
			expedientEditarCommand.setResponsableCodi(personaResponsable.getCodi());
			expedientEditarCommand.setResponsableNomSencer(personaResponsable.getNomSencer());
		}
		// Estat finalitzat
		if (expedient.getEstat() == null && expedient.getDataFi() != null) {
			expedientEditarCommand.setEstatId(ESTAT_FINALITZAT_ID);
		}
		return expedientEditarCommand;
	}

	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(
				Long.class,
				new CustomNumberEditor(Long.class, true));
		binder.registerCustomEditor(
				Double.class,
				new CustomNumberEditor(Double.class, true));
		binder.registerCustomEditor(
				BigDecimal.class,
				new CustomNumberEditor(
						BigDecimal.class,
						new DecimalFormat("#,##0.00"),
						true));
		binder.registerCustomEditor(
				Boolean.class,
				new CustomBooleanEditor(true));
		binder.registerCustomEditor(
				Date.class,
				new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true));
		binder.registerCustomEditor(
				Object.class,
				new ObjectTypeEditorHelper());
	}
}
