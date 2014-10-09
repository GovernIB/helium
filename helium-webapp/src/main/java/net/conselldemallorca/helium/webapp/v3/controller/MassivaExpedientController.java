package net.conselldemallorca.helium.webapp.v3.controller;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExecucioMassivaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExecucioMassivaDto.ExecucioMassivaTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.service.ExecucioMassivaService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.v3.core.api.service.TascaService;
import net.conselldemallorca.helium.webapp.v3.command.CanviVersioProcesCommand;
import net.conselldemallorca.helium.webapp.v3.command.DocumentExpedientCommand;
import net.conselldemallorca.helium.webapp.v3.command.ExecucioAccioCommand;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientEinesAturarCommand;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientEinesReassignarCommand;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientEinesScriptCommand;
import net.conselldemallorca.helium.webapp.v3.command.ModificarVariablesCommand;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper.SessionManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;

/**
 * Controlador per iniciar un expedient
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedient/massiva")
public class MassivaExpedientController extends BaseExpedientController {

	@Autowired
	private ExpedientService expedientService;

	@Autowired
	private TascaService tascaService;
	
	@Autowired
	ExecucioMassivaService execucioMassivaService;

	@RequestMapping(method = RequestMethod.GET)
	public String getExpedient(
			HttpServletRequest request,
			Model model) {
		SessionManager sessionManager = SessionHelper.getSessionManager(request);
		Set<Long> ids = sessionManager.getSeleccioConsultaGeneral();
		if (ids == null || ids.isEmpty()) {
			MissatgesHelper.error(request, getMessage(request, "error.no.exp.selec"));
			return "redirect:/v3";
		} else {
			List<ExpedientDto> expedients = expedientService.findAmbIds(ids);
			
			model.addAttribute("expedients", expedients);
			model.addAttribute("inici", null);
			model.addAttribute("correu", false);
			model.addAttribute(new ExpedientEinesScriptCommand());
			model.addAttribute(new ExecucioAccioCommand());
			model.addAttribute(new CanviVersioProcesCommand());
			model.addAttribute(new ExpedientEinesAturarCommand());
			model.addAttribute(new ModificarVariablesCommand());
			model.addAttribute(new DocumentExpedientCommand());
			model.addAttribute(new ExpedientEinesReassignarCommand());
			
			model.addAttribute("accions",expedientService.findAccionsVisibles(expedients.get(0).getId()));
			
			DefinicioProcesDto definicioProces = dissenyService.getByInstanciaProcesById(expedients.get(0).getProcessInstanceId());
			List<DefinicioProcesDto> supProcessos = dissenyService.getSubprocessosByProces(definicioProces.getJbpmId());
			model.addAttribute("definicioProces",definicioProces);
			model.addAttribute("subDefinicioProces", supProcessos);
			
			return "v3/massivaInfo";
		}
	}

	@RequestMapping(method = RequestMethod.POST)
	public String massivaPost(
			HttpServletRequest request,
			@RequestParam(value = "inici", required = false) String inici,
			@RequestParam(value = "correu", required = false) boolean correu,
			@ModelAttribute Object command, 
			@RequestParam(value = "accio", required = true) String accio,
			BindingResult result, 
			SessionStatus status, 
			Model model) {
		
		SessionManager sessionManager = SessionHelper.getSessionManager(request);
		Set<Long> ids = sessionManager.getSeleccioConsultaGeneral();
		if (ids == null || ids.isEmpty()) {
			MissatgesHelper.error(request, getMessage(request, "error.no.exp.selec"));
			return "redirect:/v3";
		}

		Date dInici = new Date();
		if (inici != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			try {
				dInici = sdf.parse(inici);
			} catch (ParseException e) {}
		}

		model.addAttribute("expedients", expedientService.findAmbIds(ids));
		model.addAttribute("inici", inici);
		model.addAttribute("correu", correu);
		model.addAttribute(new ExpedientEinesScriptCommand());
		model.addAttribute(new ExecucioAccioCommand());
		model.addAttribute(new CanviVersioProcesCommand());
		model.addAttribute(new ExpedientEinesAturarCommand());
		model.addAttribute(new ModificarVariablesCommand());
		model.addAttribute(new DocumentExpedientCommand());
		model.addAttribute(new ExpedientEinesReassignarCommand());
		
		List<Long> listIds = new ArrayList<Long>(ids);
		
		ExecucioMassivaDto dto = new ExecucioMassivaDto();
		dto.setDataInici(dInici);
		dto.setEnviarCorreu(correu);
		dto.setExpedientIds(listIds);
		dto.setExpedientTipusId(listIds.get(0));
		
		try {					
			if ("reindexar".equals(accio)) {
				dto.setTipus(ExecucioMassivaTipusDto.REINDEXAR);
				execucioMassivaService.crearExecucioMassiva(dto);
				MissatgesHelper.info(request, getMessage(request, "info.reindexar.massiu.executat", new Object[] {listIds.size()}));
			} else if ("executar".equals(accio)) {
				new ExpedientScriptValidator().validate(command, result);
				if (result.hasErrors()) {
					MissatgesHelper.error(request, getMessage(request, "error.executar.script"));
				} else {
					dto.setTipus(ExecucioMassivaTipusDto.EXECUTAR_SCRIPT);
					dto.setParam2(execucioMassivaService.serialize(((ExpedientEinesScriptCommand) command).getScript()));
					execucioMassivaService.crearExecucioMassiva(dto);
					MissatgesHelper.info(request, getMessage(request, "info.script.massiu.executat", new Object[] {listIds.size()}));
					model.addAttribute("expedientEinesScriptCommand", command);
				}
			} else if ("aturar".equals(accio)) {
				new ExpedientAturarValidator().validate(command, result);
				if (result.hasErrors()) {
					MissatgesHelper.error(request, getMessage(request, "error.aturar.expedient"));
				} else {
					dto.setTipus(ExecucioMassivaTipusDto.ATURAR_EXPEDIENT);
					dto.setParam2(execucioMassivaService.serialize(((ExpedientEinesAturarCommand) command).getMotiu()));
					execucioMassivaService.crearExecucioMassiva(dto);		
					MissatgesHelper.info(request, getMessage(request, "info.expedient.massiu.aturats", new Object[] {listIds.size()}));
					model.addAttribute("expedientEinesAturarCommand", command);
				}
			} else if ("canviar_versio".equals(accio)) {
//				new ExpedientAturarValidator().validate(command, result);
//				if (result.hasErrors()) {
//					MissatgesHelper.error(request, getMessage(request, "error.aturar.expedient"));
//				} else {
//					dto.setTipus(ExecucioMassivaTipusDto.ATURAR_EXPEDIENT);
//					dto.setParam2(execucioMassivaService.serialize(((ExpedientEinesAturarCommand) command).getMotiu()));
//					execucioMassivaService.crearExecucioMassiva(dto);		
//					MissatgesHelper.info(request, getMessage(request, "info.expedient.massiu.aturats", new Object[] {listIds.size()}));
//					model.addAttribute("expedientEinesAturarCommand", command);
//				}
			}
		} catch (Exception e) {
			MissatgesHelper.error(request, getMessage(request, "error.no.massiu"));
			logger.error("Error al programar les accions massives", e);
		}
		
		return "v3/massivaInfo";
	}
	
	private class ExpedientScriptValidator implements Validator {
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public boolean supports(Class clazz) {
			return clazz.isAssignableFrom(ExpedientEinesScriptCommand.class);
		}
		public void validate(Object target, Errors errors) {
			ValidationUtils.rejectIfEmpty(errors, "script", "not.blank");
		}
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
	
	private class DocumentModificarValidator implements Validator {
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public boolean supports(Class clazz) {
			return clazz.isAssignableFrom(Object.class);
		}
		public void validate(Object command, Errors errors) {
			ValidationUtils.rejectIfEmpty(errors, "data", "not.blank");
		}
	}
	
	private class DocumentAdjuntCrearValidator implements Validator {
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public boolean supports(Class clazz) {
			return clazz.isAssignableFrom(Object.class);
		}
		public void validate(Object command, Errors errors) {
			ValidationUtils.rejectIfEmpty(errors, "nom", "not.blank");
			ValidationUtils.rejectIfEmpty(errors, "data", "not.blank");
			ValidationUtils.rejectIfEmpty(errors, "contingut", "not.blank");
		}
	}
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(
				byte[].class,
				new ByteArrayMultipartFileEditor());
		binder.registerCustomEditor(
				Date.class,
				new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true));
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
				new CustomBooleanEditor(false));
	}

	private static final Log logger = LogFactory.getLog(MassivaExpedientController.class);
}
