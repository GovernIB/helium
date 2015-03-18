package net.conselldemallorca.helium.webapp.v3.controller;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto.IniciadorTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ParellaCodiValorDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDadaDto;
import net.conselldemallorca.helium.v3.core.api.exception.TascaNotFoundException;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.v3.core.api.service.TascaService;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientInicioPasTitolCommand;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.ObjectTypeEditorHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;
import net.conselldemallorca.helium.webapp.v3.helper.TascaFormHelper;
import net.conselldemallorca.helium.webapp.v3.helper.TascaFormValidatorHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;

/**
 * Controlador pel pas de formulari de l'inici d'expedient
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedient")
public class ExpedientInicioPasFormController extends BaseExpedientController {

	@Autowired
	protected TascaService tascaService;

	@Autowired
	protected ExpedientService expedientService;

	@ModelAttribute("command")
	protected Object populateCommand(
			HttpServletRequest request, 
			Long expedientTipusId,
			Long definicioProcesId,
			Model model) {
		try {
			Map<String, Object> campsAddicionals = new HashMap<String, Object>();
			Map<String, Class<?>> campsAddicionalsClasses = new HashMap<String, Class<?>>();
			EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
			ExpedientTascaDto tasca = obtenirTascaInicial(entorn.getId(), expedientTipusId, definicioProcesId, new HashMap<String, Object>(), request);
			campsAddicionals.put("id", tasca.getId());
			campsAddicionals.put("entornId", entorn.getId());
			campsAddicionals.put("expedientTipusId", expedientTipusId);
			campsAddicionals.put("definicioProcesId", definicioProcesId);
			campsAddicionalsClasses.put("id", String.class);
			campsAddicionalsClasses.put("entornId", Long.class);
			campsAddicionalsClasses.put("expedientTipusId", Long.class);
			campsAddicionalsClasses.put("definicioProcesId", Long.class);
			return TascaFormHelper.getCommandForCamps(
					tascaService.findDadesPerTascaDto(tasca),
					null,
					campsAddicionals,
					campsAddicionalsClasses,
					false);
		} catch (TascaNotFoundException ex) {
			MissatgesHelper.error(request, ex.getMessage());
			logger.error("No s'han pogut encontrar la tasca: " + ex.getMessage(), ex);
		} catch (Exception ignored) {} 
		return null;
	}

	@RequestMapping(value = "/iniciarPasForm", method = RequestMethod.POST)
	public String iniciarPasFormPost(
			HttpServletRequest request, 
			@RequestParam(value = "id", required = false) String id, 
			@RequestParam(value = "expedientTipusId", required = true) Long expedientTipusId, 
			@RequestParam(value = "definicioProcesId", required = false) Long definicioProcesId, 
			@Valid @ModelAttribute("command") Object command,
			BindingResult result, 
			SessionStatus status, 
			Model model) {
		EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
		ExpedientTipusDto expedientTipus = dissenyService.getExpedientTipusById(expedientTipusId);
		ExpedientTascaDto tasca = obtenirTascaInicial(entorn.getId(), expedientTipusId, definicioProcesId, new HashMap<String, Object>(), request);
		List<TascaDadaDto> tascaDades = tascaService.findDadesPerTascaDto(tasca);
		TascaFormValidatorHelper validator = new TascaFormValidatorHelper(
				tascaService,
				tascaDades);
		Map<String, Object> valors = TascaFormHelper.getValorsFromCommand(tascaDades, command, false, true);
		
		validator.setValidarObligatoris(true);
		validator.setValidarExpresions(true);
		validator.validate(command, result);
		DefinicioProcesDto definicioProces = null;
		if (definicioProcesId != null) {
			definicioProces = dissenyService.getById(definicioProcesId);
		} else {
			definicioProces = dissenyService.findDarreraDefinicioProcesForExpedientTipus(expedientTipusId);
		}
		model.addAttribute("definicioProces", definicioProces);
		if (result.hasErrors()) {
			MissatgesHelper.error(request, getMessage(request, "error.validacio"));
			model.addAttribute(command);
			model.addAttribute("tasca", tasca);
			model.addAttribute("dades", tascaDades);
			model.addAttribute("entornId", entorn.getId());
			model.addAttribute("expedientTipus", expedientTipus);
			model.addAttribute("responsableCodi", expedientTipus.getResponsableDefecteCodi());

			return "v3/expedient/iniciarPasForm";
		}
		// Si l'expedient ha de demanar titol i/o número redirigeix al pas per demanar aquestes dades
		if (expedientTipus.isDemanaNumero() || expedientTipus.isDemanaTitol() || expedientTipus.isSeleccionarAny()) {
			ExpedientInicioPasTitolCommand expedientInicioPasTitolCommand = new ExpedientInicioPasTitolCommand();
			expedientInicioPasTitolCommand.setAny(Calendar.getInstance().get(Calendar.YEAR));
			expedientInicioPasTitolCommand.setExpedientTipusId(expedientTipusId);
			expedientInicioPasTitolCommand.setNumero(expedientService.getNumeroExpedientActual(entorn.getId(), expedientTipusId, expedientInicioPasTitolCommand.getAny()));
			expedientInicioPasTitolCommand.setResponsableCodi(expedientTipus.getResponsableDefecteCodi());
			expedientInicioPasTitolCommand.setEntornId(entorn.getId());
			model.addAttribute(expedientInicioPasTitolCommand);
			model.addAttribute("anysSeleccionables", getAnysSeleccionables());
			model.addAttribute("expedientTipus", expedientTipus);

			request.getSession().setAttribute(ExpedientIniciController.CLAU_SESSIO_FORM_VALORS, valors);

			return "v3/expedient/iniciarPasTitol";
		} else {
			try {
				ExpedientDto iniciat = iniciarExpedient(entorn.getId(), expedientTipusId, definicioProcesId, (String) request.getSession().getAttribute(ExpedientIniciController.CLAU_SESSIO_NUMERO), (String) request.getSession().getAttribute(ExpedientIniciController.CLAU_SESSIO_TITOL), (Integer) request.getSession().getAttribute(ExpedientIniciController.CLAU_SESSIO_ANY), valors);
				MissatgesHelper.info(request, getMessage(request, "info.expedient.iniciat", new Object[] { iniciat.getIdentificador() }));
				ExpedientIniciController.netejarSessio(request);
			} catch (Exception ex) {				
				MissatgesHelper.error(request, getMessage(request, "error.iniciar.expedient") + ": " + ex);
				logger.error("No s'ha pogut iniciar l'expedient", ex);
				model.addAttribute(command);
				model.addAttribute("tasca", tasca);
				model.addAttribute("dades", tascaDades);
				model.addAttribute("entornId", entorn.getId());
				model.addAttribute("expedientTipus", expedientTipus);
				model.addAttribute("responsableCodi", expedientTipus.getResponsableDefecteCodi());

				return "v3/expedient/iniciarPasForm";
			}
		}
		return modalUrlTancar();
	}

	private synchronized ExpedientDto iniciarExpedient(Long entornId, Long expedientTipusId, Long definicioProcesId, String numero, String titol, Integer any, Map<String, Object> valors) {
		return expedientService.create(entornId, null, expedientTipusId, definicioProcesId, any, numero, titol, null, null, null, null, false, null, null, null, null, null, null, false, null, null, false, valors, null, IniciadorTipusDto.INTERN, null, null, null, null);
	}

	public List<ParellaCodiValorDto> getAnysSeleccionables() {
		List<ParellaCodiValorDto> anys = new ArrayList<ParellaCodiValorDto>();
		int anyActual = Calendar.getInstance().get(Calendar.YEAR);
		for (int i = 0; i < 5; i++) {
			anys.add(new ParellaCodiValorDto(String.valueOf(anyActual - i), anyActual - i));
		}
		return anys;
	}

	protected ExpedientTascaDto obtenirTascaInicial(Long entornId, Long expedientTipusId, Long definicioProcesId, Map<String, Object> valors, HttpServletRequest request) {
		ExpedientTascaDto tasca = expedientService.getStartTask(entornId, expedientTipusId, definicioProcesId, valors);
		tasca.setId((String) request.getSession().getAttribute(ExpedientIniciController.CLAU_SESSIO_TASKID));
		Object validat = request.getSession().getAttribute(ExpedientIniciController.CLAU_SESSIO_FORM_VALIDAT);
		tasca.setValidada(validat != null);
		return tasca;
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
//				new CustomBooleanEditor(false));
				new CustomBooleanEditor(true));
		binder.registerCustomEditor(
				Date.class,
				new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true));
//		binder.registerCustomEditor(
//				TerminiDto.class,
//				new TerminiTypeEditorHelper());
		binder.registerCustomEditor(
				Object.class,
				new ObjectTypeEditorHelper());
	}

	private static final Log logger = LogFactory.getLog(ExpedientIniciController.class);
}
