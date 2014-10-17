package net.conselldemallorca.helium.webapp.v3.controller;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.v3.core.api.dto.CampDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExecucioMassivaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExecucioMassivaDto.ExecucioMassivaTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDadaDto;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
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
			
			CanviVersioProcesCommand canviVersioProcesCommand = new CanviVersioProcesCommand();
			DefinicioProcesDto definicioProces = dissenyService.getByInstanciaProcesById(expedients.get(0).getProcessInstanceId());
			
			canviVersioProcesCommand.setDefinicioProcesId(definicioProces.getId());			
			model.addAttribute(canviVersioProcesCommand);
			
			List<DefinicioProcesDto> supProcessos = dissenyService.getSubprocessosByProces(definicioProces.getJbpmId());
			model.addAttribute("definicioProces",definicioProces);
			model.addAttribute("subDefinicioProces", supProcessos);
			
			// Documents
			InstanciaProcesDto instanciaProces = expedientService.getInstanciaProcesById(expedients.get(0).getProcessInstanceId());
			model.addAttribute("instanciaProces", instanciaProces);
			
			// Variables
			List<CampDto> variables = new ArrayList<CampDto>();
			if (instanciaProces != null) {
				for (CampDto camp : expedientService.getCampsInstanciaProcesById(expedients.get(0).getProcessInstanceId())){
					if (!CampTipusDto.ACCIO.equals(camp.getTipus())) {
						variables.add(camp);
					}
				}
			}
			Collections.sort(variables, new ComparadorCampCodi());
			model.addAttribute("variables", variables);
			
			List<DocumentDto> documents = expedientService.findListDocumentsPerDefinicioProces(
					definicioProces.getId(),
					expedients.get(0).getProcessInstanceId(),
					expedients.get(0).getTipus().getNom());
			Collections.sort(documents, new ComparadorDocument());
			model.addAttribute("documents", documents);
			
			return "v3/massivaInfo";
		}
	}

	public class ComparadorCampCodi implements Comparator<CampDto> {
	    public int compare(CampDto c1, CampDto c2) {
	        return c1.getCodi().compareToIgnoreCase(c2.getCodi());
	    }
	}
	
	public class ComparadorDocument implements Comparator<DocumentDto> {
	    public int compare(DocumentDto d1, DocumentDto d2) {
	        return d1.getDocumentNom().compareToIgnoreCase(d2.getDocumentNom());
	    }
	}

	@RequestMapping(value="reindexarMas", method = RequestMethod.POST)
	public String reindexarMasPost(
			HttpServletRequest request,
			@RequestParam(value = "inici", required = false) String inici,
			@RequestParam(value = "correu", required = false) boolean correu,
			@RequestParam(value = "accio", required = true) String accio,
			Model model) {
		return massivaPost(request, inici, correu, null, accio, null, null, model);
	}

	@RequestMapping(value="massivaExecutarAccio", method = RequestMethod.POST)
	public String execucioAccioCommandPost(
			HttpServletRequest request,
			@RequestParam(value = "inici", required = false) String inici,
			@RequestParam(value = "correu", required = false) boolean correu,
			@ModelAttribute ExecucioAccioCommand command, 
			@RequestParam(value = "accio", required = true) String accio,
			BindingResult result, 
			SessionStatus status, 
			Model model) {		
		return massivaPost(request, inici, correu, command, accio, result, status, model);
	}

	@RequestMapping(value="aturarMas", method = RequestMethod.POST)
	public String expedientEinesAturarCommandPost(
			HttpServletRequest request,
			@RequestParam(value = "inici", required = false) String inici,
			@RequestParam(value = "correu", required = false) boolean correu,
			@ModelAttribute ExpedientEinesAturarCommand command, 
			@RequestParam(value = "accio", required = true) String accio,
			BindingResult result, 
			SessionStatus status, 
			Model model) {		
		return massivaPost(request, inici, correu, command, accio, result, status, model);
	}

	@RequestMapping(value="scriptMas", method = RequestMethod.POST)
	public String expedientEinesScriptCommandPost(
			HttpServletRequest request,
			@RequestParam(value = "inici", required = false) String inici,
			@RequestParam(value = "correu", required = false) boolean correu,
			@ModelAttribute ExpedientEinesScriptCommand command, 
			@RequestParam(value = "accio", required = true) String accio,
			BindingResult result, 
			SessionStatus status, 
			Model model) {		
		return massivaPost(request, inici, correu, command, accio, result, status, model);
	}

	@RequestMapping(value="massivaCanviVersio", method = RequestMethod.POST)
	public String canviVersioProcesCommandPost(
			HttpServletRequest request,
			@RequestParam(value = "inici", required = false) String inici,
			@RequestParam(value = "correu", required = false) boolean correu,
			@ModelAttribute CanviVersioProcesCommand command, 
			@RequestParam(value = "accio", required = true) String accio,
			BindingResult result, 
			SessionStatus status, 
			Model model) {		
		return massivaPost(request, inici, correu, command, accio, result, status, model);
	}
	
	public String massivaPost(
			HttpServletRequest request,
			String inici,
			boolean correu,
			Object command, 
			String accio,
			BindingResult result, 
			SessionStatus status, 
			Model model) {		
		SessionManager sessionManager = SessionHelper.getSessionManager(request);
		getExpedient(request,model);
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

		model.addAttribute("inici", inici);
		model.addAttribute("correu", correu);
		
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
			} else if ("executar_accio".equals(accio)) {
				dto.setTipus(ExecucioMassivaTipusDto.EXECUTAR_ACCIO);
				execucioMassivaService.crearExecucioMassiva(dto);
					
				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
				Object[] params = new Object[3];
				params[0] = ((ExecucioAccioCommand) command).getAccioId();
				params[1] = auth.getCredentials();
				List<String> rols = new ArrayList<String>();
				for (GrantedAuthority gauth : auth.getAuthorities()) {
					rols.add(gauth.getAuthority());
				}
				params[2] = rols;
				dto.setParam2(execucioMassivaService.serialize(params));
				execucioMassivaService.crearExecucioMassiva(dto);				
				MissatgesHelper.info(request, getMessage(request, "info.accio.massiu.executat", new Object[] {listIds.size()}));
				model.addAttribute("execucioAccioCommand", command);
			} else if ("canviar_versio".equals(accio)) {
				dto.setTipus(ExecucioMassivaTipusDto.ACTUALITZAR_VERSIO_DEFPROC);
				Object[] params = new Object[3];
				params[0] = ((CanviVersioProcesCommand) command).getDefinicioProcesId();
				params[1] = ((CanviVersioProcesCommand) command).getSubprocesId();
								
				ExpedientDto expedient = expedientService.findAmbId(listIds.get(0));
				DefinicioProcesDto definicioProces = dissenyService.getByInstanciaProcesById(expedient.getProcessInstanceId());
				List<DefinicioProcesDto> supProcessos = dissenyService.getSubprocessosByProces(definicioProces.getJbpmId());

				String[] keys = new String[supProcessos.size()];
				int i = 0;
				for (DefinicioProcesDto subproces: supProcessos) {
					keys[i++] = subproces.getJbpmKey();
				}
				params[2] = keys;
				dto.setParam2(execucioMassivaService.serialize(params));
				execucioMassivaService.crearExecucioMassiva(dto);
				
				MissatgesHelper.info(request, getMessage(request, "info.canvi.versio.massiu", new Object[] {listIds.size()}));
				model.addAttribute("canviVersioProcesCommand", command);
			} else if ("document_esborrar".equals(accio)) {
				dto.setTipus(ExecucioMassivaTipusDto.MODIFICAR_DOCUMENT);
				
				Long docId = ((DocumentExpedientCommand) command).getDocId();
				DocumentDto document = expedientService.findDocumentsPerId(docId);
				dto.setParam1(document.getDocumentNom());
				
				Object[] params = new Object[4];
				params[0] = docId;
				params[1] = null;
				params[2] = "delete";
				dto.setParam2(execucioMassivaService.serialize(params));
				execucioMassivaService.crearExecucioMassiva(dto);
				
				MissatgesHelper.info(request, getMessage(request, "info.document.massiu.esborrar", new Object[] {listIds.size()}));
				model.addAttribute("documentExpedientCommand", command);
			} else if ("document_generar".equals(accio)) {
				dto.setTipus(ExecucioMassivaTipusDto.MODIFICAR_DOCUMENT);
				Long docId = ((DocumentExpedientCommand) command).getDocId();
				DocumentDto document = expedientService.findDocumentsPerId(docId);
				dto.setParam1(document.getDocumentNom());
				
				Object[] params = new Object[4];
				params[0] = docId;
				params[1] = new Date();
				params[2] = "generate";
				dto.setParam2(execucioMassivaService.serialize(params));
				execucioMassivaService.crearExecucioMassiva(dto);
				
				MissatgesHelper.info(request, getMessage(request, "info.document.massiu.generar", new Object[] {listIds.size()}));
				model.addAttribute("documentExpedientCommand", command);
			} 
			
			else if ("document_modificar".equals(accio)) {
				Long docId = ((DocumentExpedientCommand) command).getDocId();
				DocumentDto document = expedientService.findDocumentsPerId(docId);
				model.addAttribute("document", document);
				
				return "expedient/documentFormMassiva";
			} else if ("modificar_variable".equals(accio)) {
//				ModificarVariablesCommand mvCommand = ((ModificarVariablesCommand)command);
//				EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
//				TascaDto tasca = createTascaAmbVar(request, entorn.getId(), mvCommand.getId(), mvCommand.getTaskId(), mvCommand.getVar());
				model.addAttribute("modificarVariablesCommand", command);
//				model.addAttribute("tasca", tasca);
				model.addAttribute("expedient", expedientService.findAmbId(listIds.get(0)));
//				model.addAttribute("valorsPerSuggest", TascaFormUtil.getValorsPerSuggest(tasca, command));
				return "/expedient/dadaFormMassiva";
			}
		} catch (Exception e) {
			MissatgesHelper.error(request, getMessage(request, "error.no.massiu"));
			logger.error("Error al programar les accions massives", e);
		}
		
		return "redirect:/v3/expedient/massiva";
	}
	
	@RequestMapping(value = "/{var}/modificarVariables", method = RequestMethod.GET)
	public String modificarVariablesGet(
			HttpServletRequest request,
			@PathVariable String var,
			Model model) {
		SessionManager sessionManager = SessionHelper.getSessionManager(request);
		Set<Long> ids = sessionManager.getSeleccioConsultaGeneral();		
		CampDto camp = expedientService.getCampsInstanciaProcesByIdAmdVarcodi(expedientService.findAmbIds(ids).get(0).getProcessInstanceId(), var);
		
		TascaDadaDto tascaDada = new TascaDadaDto();
		tascaDada.setVarCodi(camp.getCodi());
		ModificarVariablesCommand command = new ModificarVariablesCommand();
		command.setVar(var);
		model.addAttribute("modificarVariablesCommand", command);
		return "v3/massivaInfoModificarVariables";
	}
	
	@RequestMapping(value = "/{docId}/documentAdjunt", method = RequestMethod.GET)
	public String documentAdjuntGet(
			HttpServletRequest request,
			@PathVariable Long docId,
			Model model) {
		DocumentExpedientCommand command = new DocumentExpedientCommand();
		command.setDocId(docId);
		model.addAttribute("documentExpedientCommand", command);
		return "v3/massivaInfoDocumentAdjunt";
	}
	
	@RequestMapping(value = "/{docId}/documentModificar", method = RequestMethod.GET)
	public String documentModificarGet(
			HttpServletRequest request,
			@PathVariable Long docId,
			Model model) {
		DocumentExpedientCommand command = new DocumentExpedientCommand();
		command.setDocId(docId);
		model.addAttribute("documentExpedientCommand", command);
		return "v3/massivaInfoDocumentModificar";
	}

	@RequestMapping(value="modificarVariablesMasCommand", method = RequestMethod.POST)
	public String modificarVariablesCommandPost(
			HttpServletRequest request,
			@RequestParam(value = "inici", required = false) String inici,
			@RequestParam(value = "correu", required = false) boolean correu,
			@ModelAttribute ModificarVariablesCommand command, 
			@RequestParam(value = "accio", required = true) String accio,
			BindingResult result, 
			SessionStatus status, 
			Model model) {		
		return massivaPost(request, inici, correu, command, accio, result, status, model);
	}

	@RequestMapping(value="documentModificarMas", method = RequestMethod.POST)
	public String documentExpedientCommandPost(
			HttpServletRequest request,
			@RequestParam(value = "inici", required = false) String inici,
			@RequestParam(value = "correu", required = false) boolean correu,
			@ModelAttribute DocumentExpedientCommand command, 
			@RequestParam(value = "accio", required = true) String accio,
			BindingResult result, 
			SessionStatus status, 
			Model model) {		
		return massivaPost(request, inici, correu, command, accio, result, status, model);
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
