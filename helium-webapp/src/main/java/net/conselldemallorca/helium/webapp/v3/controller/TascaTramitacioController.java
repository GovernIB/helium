package net.conselldemallorca.helium.webapp.v3.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import net.conselldemallorca.helium.core.model.dto.ParellaCodiValorDto;
import net.conselldemallorca.helium.jbpm3.handlers.exception.ValidationException;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExecucioMassivaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExecucioMassivaDto.ExecucioMassivaTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.SeleccioOpcioDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDadaDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDocumentDto;
import net.conselldemallorca.helium.v3.core.api.exception.TascaNotFoundException;
import net.conselldemallorca.helium.v3.core.api.service.DissenyService;
import net.conselldemallorca.helium.v3.core.api.service.ExecucioMassivaService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.v3.core.api.service.TascaService;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.ModalHelper;
import net.conselldemallorca.helium.webapp.v3.helper.ObjectTypeEditorHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper.SessionManager;
import net.conselldemallorca.helium.webapp.v3.helper.TascaFormHelper;
import net.conselldemallorca.helium.webapp.v3.helper.TascaFormValidatorHelper;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.support.SessionStatus;

/**
 * Controlador per a la tramitació de taques.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedient")
public class TascaTramitacioController extends BaseController {

	protected String TAG_PARAM_REGEXP = "<!--helium:param-(.+?)-->";
	public static final String VARIABLE_SESSIO_CAMP_FOCUS = "helCampFocus";
	public static final String VARIABLE_COMMAND_TRAMITACIO = "variableCommandTramitacio";

	@Autowired
	protected ExpedientService expedientService;
	@Autowired
	protected TascaService tascaService;
	@Autowired
	protected DissenyService dissenyService;
	@Autowired
	protected ExecucioMassivaService execucioMassivaService;

	@RequestMapping(value = "/massivaTramitacioTasca", method = RequestMethod.GET)
	public String massivaTramitacio(
			HttpServletRequest request,
			@RequestParam(value = "inici", required = false) String inici,
			@RequestParam(value = "correu", required = false) boolean correu,
			Model model) throws IOException, ServletException {
		SessionManager sessionManager = SessionHelper.getSessionManager(request);
		Set<Long> seleccio = sessionManager.getSeleccioConsultaTasca();
		if (seleccio == null || seleccio.isEmpty()) {
			MissatgesHelper.error(request, getMessage(request, "error.no.tasc.selec"));
			return "redirect:/v3/tasca";
		}
		String tascaId = String.valueOf(seleccio.iterator().next());
		ExpedientTascaDto tasca = tascaService.findAmbIdPerTramitacio(tascaId);
		getModelTramitacio(tascaId, model);
		model.addAttribute("command", populateCommand(request, tascaId, inici, correu, seleccio.size(), model));
		
		return getReturnUrl(request, tasca.getExpedientId(), tascaId);
	}

	@RequestMapping(value = "/massivaTramitacioTasca/taula", method = RequestMethod.GET)
	public String massivaTramitacioTaula(
			HttpServletRequest request,
			Model model) {
		SessionManager sessionManager = SessionHelper.getSessionManager(request);
		Set<Long> seleccio = sessionManager.getSeleccioConsultaTasca();		
		model.addAttribute("tasques", tascaService.findDadesPerIds(seleccio));
		
		return "v3/import/tasquesMassivaTaula";
	}

	@SuppressWarnings("rawtypes")
	@ModelAttribute("command")
	public Object initializeCommand(
			HttpServletRequest request, 
			String tascaId, 
			String inici, 
			Boolean correu, 
			Integer numTascaMassiva,
			Model model) {		
		if (tascaId != null && !tascaId.isEmpty()) {
			try {
				Map<String, Object> campsAddicionals = new HashMap<String, Object>();
				Map<String, Class> campsAddicionalsClasses = new HashMap<String, Class>();
				campsAddicionals.put("inici", inici);
				campsAddicionals.put("correu", correu);
				campsAddicionals.put("numTascaMassiva", numTascaMassiva);
				campsAddicionalsClasses.put("inici", String.class);
				campsAddicionalsClasses.put("correu", Boolean.class);
				campsAddicionalsClasses.put("numTascaMassiva", Integer.class);
				return TascaFormHelper.getCommandBuitForCamps(
						tascaService.findDades(tascaId),	// tascaDadas
						campsAddicionals,					// campsAddicionals
						campsAddicionalsClasses, 			// campsAddicionalsClasses
						false);								// perFiltre
			} catch (TascaNotFoundException ex) {
				MissatgesHelper.error(request, ex.getMessage());
				logger.error("No s'ha pogut trobar la tasca: " + ex.getMessage(), ex);
			} catch (Exception ignored) {} 
		}
		return null;
	}

	private Object populateCommand(HttpServletRequest request, String tascaId, String inici, boolean correu, int numTascaMassiva, Model model) {
		Object filtreCommand = SessionHelper.getAttribute(request,VARIABLE_COMMAND_TRAMITACIO);
		if (filtreCommand != null) {
			return SessionHelper.getAttribute(request,VARIABLE_COMMAND_TRAMITACIO);
		} else if (tascaId != null && !tascaId.isEmpty()) {
			try {				
				Map<String, Object> campsAddicionals = new HashMap<String, Object>();
				@SuppressWarnings("rawtypes")
				Map<String, Class> campsAddicionalsClasses = new HashMap<String, Class>();
				campsAddicionals.put("inici", inici);
				campsAddicionals.put("correu", correu);
				campsAddicionals.put("numTascaMassiva", numTascaMassiva);
				campsAddicionalsClasses.put("inici", String.class);
				campsAddicionalsClasses.put("correu", Boolean.class);
				campsAddicionalsClasses.put("numTascaMassiva", Integer.class);
				
				Object command = TascaFormHelper.getCommandForCamps(
						tascaService.findDades(tascaId),	// tascaDadas
						new HashMap<String, Object>(),		// valors
						campsAddicionals,					// campsAddicionals
						campsAddicionalsClasses, 			// campsAddicionalsClasses
						false);								// perFiltre
				SessionHelper.setAttribute(request,VARIABLE_COMMAND_TRAMITACIO, command);
				return command;
			} catch (TascaNotFoundException ex) {
				MissatgesHelper.error(request, ex.getMessage());
				logger.error("No s'ha pogut trobar la tasca: " + ex.getMessage(), ex);
			} catch (Exception ignored) {} 
		}
		return null;
	}

	public Object populateCommand(
			HttpServletRequest request, 
			String tascaId,
			Model model) {
		return populateCommand(
				request, 
				tascaId, null, false, 0,
				model);
		
	}
	
	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}", method = RequestMethod.GET)
	public String tramitar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String tascaId,
			Model model) {
		SessionHelper.removeAttribute(request, SessionHelper.VARIABLE_TASCA_ERRROR);
		return getReturnUrl(request, expedientId, tascaId);
	}

	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/form", method = RequestMethod.GET)
	public String formGet(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String tascaId,
			Model model) {
		// Omple les dades del formulari i les de només lectura
		getModelTramitacio(tascaId, model);
		model.addAttribute("command", populateCommand(request, tascaId, model));
		return "v3/expedientTascaTramitacio";
	}
	
	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/form/guardar", method = RequestMethod.POST)
	public String formGuardar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String tascaId,
			@Valid @ModelAttribute("command") Object command,
			BindingResult result, 
			SessionStatus status,
			@RequestParam(value = "accio", required = false) String accio,
			ModelMap model) {
		EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
		if (entorn != null) {
			List<TascaDadaDto> tascaDadas = tascaService.findDades(tascaId);
			Map<String, Object> variables = TascaFormHelper.getValorsFromCommand(tascaDadas, command, false);
			TascaFormValidatorHelper validator = new TascaFormValidatorHelper(tascaService, false);
			validator.setTasca(tascaDadas);
			guardarForm(validator, variables, command, result, request, tascaId);
			status.setComplete();
			getModelTramitacio(tascaId, (Model)model);
			TascaFormHelper.ompleMultiplesBuits(command, tascaDadas, false);
		} else {
			MissatgesHelper.error(request, getMessage(request, "error.no.entorn.selec"));			
		}
		return "v3/expedientTascaTramitacio";
	}
	
	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/form/validar", method = RequestMethod.POST)
	public String formPost(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String tascaId,
			@ModelAttribute("command") Object command, 
			BindingResult result, 
			SessionStatus status, 
			ModelMap model) {
		EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
		if (entorn != null) {
			List<TascaDadaDto> tascaDadas = tascaService.findDades(tascaId);
			Map<String, Object> variables = TascaFormHelper.getValorsFromCommand(tascaDadas, command, false);
			TascaFormValidatorHelper validator = new TascaFormValidatorHelper(tascaService, false);
			validator.setTasca(tascaDadas);
			validator.setRequest(request);
			guardarForm(validator, variables, command, result, request, tascaId);
			validarForm(validator, variables, command, result, request, tascaId);
			status.setComplete();
			getModelTramitacio(tascaId, (Model)model);
			TascaFormHelper.ompleMultiplesBuits(command, tascaDadas, false);
		} else {
			MissatgesHelper.error(request, getMessage(request, "error.no.entorn.selec"));			
		}
		return "v3/expedientTascaTramitacio";
	}
	
	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/form/completar", method = RequestMethod.POST)
	public String formCompletar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String tascaId,
			@RequestParam(value = "__transicio__", required = false) String transicio,
			@ModelAttribute("command") Object command, 
			BindingResult result, 
			SessionStatus status, 
			ModelMap model) {
		EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
		if (entorn != null) {
			List<TascaDadaDto> tascaDadas = tascaService.findDades(tascaId);
			Map<String, Object> variables = TascaFormHelper.getValorsFromCommand(tascaDadas, command, false);
			TascaFormValidatorHelper validator = new TascaFormValidatorHelper(tascaService, false);
			validator.setTasca(tascaDadas);
			validator.setRequest(request);
			guardarForm(validator, variables, command, result, request, tascaId);
			validarForm(validator, variables, command, result, request, tascaId);
			completarForm(request, tascaId, transicio, command);
			status.setComplete();
			getModelTramitacio(tascaId, (Model)model);
			TascaFormHelper.ompleMultiplesBuits(command, tascaDadas, false);
		} else {
			MissatgesHelper.error(request, getMessage(request, "error.no.entorn.selec"));			
		}
		return "v3/expedientTascaTramitacio";
	}
	
	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/form/restaurar", method = RequestMethod.POST)
	public String formRestaurar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String tascaId,
			@ModelAttribute("command") Object command, 
			BindingResult result, 
			SessionStatus status, 
			ModelMap model) {
		EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
		if (entorn != null) {			
//			ExpedientTascaDto tasca = (ExpedientTascaDto) model.get("tasca");
//			List<TascaDadaDto> tascaDadas = tascaService.findDades(tascaId);
//
//			boolean ok = accioRestaurarForm(request, entorn.getId(), tascaId, tascaDadas, command);
//			if (ok) {
//				status.setComplete();
//			} else {
//				MissatgesHelper.error(request, getMessage(request, "error.validacio"));
//			}
		} else {
			MissatgesHelper.error(request, getMessage(request, "error.no.entorn.selec"));			
		}
		
		return getReturnUrl(request, expedientId, tascaId);
	}
	
	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/form/accio/{accioCamp}", method = RequestMethod.POST)
	public String formAccio(
	HttpServletRequest request,
	@PathVariable Long expedientId,
	@PathVariable String tascaId,
	@PathVariable String accioCamp,
	@ModelAttribute("command") Object command, 
	BindingResult result, 
	SessionStatus status, 
	ModelMap model) {
		List<TascaDadaDto> tascaDadas = tascaService.findDades(tascaId);
		Map<String, Object> variables = TascaFormHelper.getValorsFromCommand(tascaDadas, command, false);
		TascaFormValidatorHelper validator = new TascaFormValidatorHelper(tascaService, false);
		validator.setTasca(tascaDadas);
		validator.setRequest(request);
		validator.validate(command, result);
		if (result.hasErrors() || !accioGuardarForm(request, tascaId, variables, command)) {
			MissatgesHelper.error(request, getMessage(request, "error.guardar.dades"));
		} else if (accioExecutarAccio(request, tascaId, accioCamp, command)) {
			model.addAttribute("campFocus", accioCamp);
		}
		
		return getReturnUrl(request, expedientId, tascaId);
	}
	
	private void guardarForm(
			TascaFormValidatorHelper validator, 
			Map<String, Object> variables, 
			Object command, 
			BindingResult result, 
			HttpServletRequest request,
			String tascaId) {
		validator.validate(command, result);
		if (result.hasErrors() || !accioGuardarForm(request, tascaId, variables, command)) {
			MissatgesHelper.error(request, getMessage(request, "error.guardar.dades"));
		}
	}
	
	private void validarForm(
			TascaFormValidatorHelper validator, 
			Map<String, Object> variables, 
			Object command, 
			BindingResult result, 
			HttpServletRequest request,
			String tascaId) {
		validator.setRequest(request);
		validator.setValidarObligatoris(true);
		validator.validate(command, result);
		if (result.hasErrors()) {
			MissatgesHelper.error(request, getMessage(request, "error.validacio"));
			MissatgesHelper.errorGlobal(request, result, getMessage(request, "error.validacio"));
			System.out.println(result);
		} else if (!accioValidarForm(request, tascaId, variables, command)) {
			MissatgesHelper.error(request, getMessage(request, "error.validar.dades"));
		}
	}
	
	private void completarForm(
			HttpServletRequest request,
			String tascaId,
			String transicio,
			Object command) {
		if (!accioCompletarForm(request, tascaId, transicio, command)) {
			MissatgesHelper.error(request, getMessage(request, "error.validar.dades"));
		}
	}
	
	private void getModelTramitacio(String tascaId, Model model) {		
		ExpedientTascaDto tasca = tascaService.findAmbIdPerTramitacio(tascaId);
		if (tasca.getRecursForm() != null && tasca.getRecursForm().length() > 0) {
			try {
				byte[] contingut = dissenyService.getDeploymentResource(tasca.getDefinicioProces().getId(), tasca.getRecursForm());
				model.addAttribute("formRecursParams", getFormRecursParams(new String(contingut, "UTF-8")));
			} catch (Exception ex) {
				logger.error("No s'han pogut extreure els parametres del recurs", ex);
			}
		}
		model.addAttribute("tasca", tasca);
		
		List<TascaDadaDto> dadesNomesLectura = new ArrayList<TascaDadaDto>();
		List<TascaDadaDto> dades = tascaService.findDades(tascaId);
		Iterator<TascaDadaDto> itDades = dades.iterator();
		while (itDades.hasNext()) {
			TascaDadaDto dada = itDades.next();
			if (dada.isReadOnly()) {
				dadesNomesLectura.add(dada);
				itDades.remove();
			}
		}
		model.addAttribute("dadesNomesLectura", dadesNomesLectura);
		model.addAttribute("dades", dades);
		
		// Omple els documents per adjuntar i els de només lectura
		List<TascaDocumentDto> documents = tascaService.findDocuments(tascaId);
		List<TascaDocumentDto> documentsNomesLectura = new ArrayList<TascaDocumentDto>();
		Iterator<TascaDocumentDto> itDocuments = documents.iterator();
		while (itDocuments.hasNext()) {
			TascaDocumentDto document = itDocuments.next();
			if (document.isReadOnly()) {
				if (document.getId() != null)
					documentsNomesLectura.add(document);
				itDocuments.remove();
			}
		}
		model.addAttribute("documentsNomesLectura", documentsNomesLectura);
		model.addAttribute("documents", documents);
	}
	
	private boolean accioGuardarForm(
			HttpServletRequest request, 
			String tascaId, 
			Map<String, Object> variables, Object command) {
		boolean resposta = false;
		
		SessionManager sessionManager = SessionHelper.getSessionManager(request);
		Set<Long> seleccio = sessionManager.getSeleccioConsultaTasca();
		
		if (seleccio != null && !seleccio.isEmpty()) {
			try {
				String inici = (String) PropertyUtils.getSimpleProperty(command, "inici");
				boolean correu = (Boolean) PropertyUtils.getSimpleProperty(command, "correu");
				
				Date dInici = new Date();
				if (inici != null) {
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
					try {
						dInici = sdf.parse(inici);
					} catch (ParseException e) {}
				}
				
				String[] tascaIds = new String[seleccio.size()];
				int i = 0;
				for (Long tId: seleccio) {
					tascaIds[i++] = tId.toString();
				}

				
				EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
				ExecucioMassivaDto dto = new ExecucioMassivaDto();
				dto.setDataInici(dInici);
				dto.setEnviarCorreu(correu);
				dto.setTascaIds(tascaIds);
//				dto.setExpedientTipusId(expTipusId);
				dto.setTipus(ExecucioMassivaTipusDto.EXECUTAR_TASCA);
				dto.setParam1("Guardar");
				Object[] params = new Object[4];
				params[0] = entorn.getId();
				params[1] = variables;
				params[2] = auth.getCredentials();
				List<String> rols = new ArrayList<String>();
				for (GrantedAuthority gauth : auth.getAuthorities()) {
					rols.add(gauth.getAuthority());
				}
				params[3] = rols;
				dto.setParam2(execucioMassivaService.serialize(params));
				execucioMassivaService.crearExecucioMassiva(dto);
				
				tascaService.guardar(tascaId, variables);
				MissatgesHelper.info(request, getMessage(request, "info.tasca.massiu.guardar", new Object[] {seleccio.size()}));
				resposta = true;
			} catch (Exception ex) {
				MissatgesHelper.error(request, getMessage(request, "error.no.massiu"));
				logger.error("No s'ha pogut guardar les dades del formulari massiu en la tasca " + tascaId, ex);
			}
		} else {
			try {
				tascaService.guardar(tascaId, variables);
				MissatgesHelper.info(request, getMessage(request, "info.dades.form.guardat"));
				resposta = true;
			} catch (Exception ex) {
//				String tascaIdLog = getIdTascaPerLogs(entornId, tascaId);
//				MissatgesHelper.error(request, getMessage(request, "error.proces.peticio") + " " + tascaIdLog);
//				logger.error("No s'ha pogut guardar les dades del formulari en la tasca " + tascaIdLog, ex);
				MissatgesHelper.error(request, getMessage(request, "error.proces.peticio") + " " + tascaId);
				logger.error("No s'ha pogut guardar les dades del formulari en la tasca " + tascaId, ex);
			}
		}
		return resposta;
	}

	private boolean accioValidarForm(
			HttpServletRequest request, 
			String tascaId, 
			Map<String, Object> variables,
			Object command) {
		boolean resposta = false;
		
		SessionManager sessionManager = SessionHelper.getSessionManager(request);
		Set<Long> seleccio = sessionManager.getSeleccioConsultaTasca();
		
		if (seleccio != null && !seleccio.isEmpty()) {
			try {
				String inici = (String) PropertyUtils.getSimpleProperty(command, "inici");
				boolean correu = (Boolean) PropertyUtils.getSimpleProperty(command, "correu");
				
				Date dInici = new Date();
				if (inici != null) {
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
					try {
						dInici = sdf.parse(inici);
					} catch (ParseException e) {}
				}
				
				String[] tascaIds = new String[seleccio.size()];
				int i = 0;
				for (Long tId: seleccio) {
					tascaIds[i++] = tId.toString();
				}

				
				EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
				ExecucioMassivaDto dto = new ExecucioMassivaDto();
				dto.setDataInici(dInici);
				dto.setEnviarCorreu(correu);
				dto.setTascaIds(tascaIds);
//				dto.setExpedientTipusId(expTipusId);
				dto.setTipus(ExecucioMassivaTipusDto.EXECUTAR_TASCA);
				dto.setParam1("Validar");
				Object[] params = new Object[4];
				params[0] = entorn.getId();
				params[1] = variables;
				params[2] = auth.getCredentials();
				List<String> rols = new ArrayList<String>();
				for (GrantedAuthority gauth : auth.getAuthorities()) {
					rols.add(gauth.getAuthority());
				}
				params[3] = rols;
				dto.setParam2(execucioMassivaService.serialize(params));
				execucioMassivaService.crearExecucioMassiva(dto);
				
				tascaService.validar(tascaId, variables);
				MissatgesHelper.info(request, getMessage(request, "info.tasca.massiu.validar", new Object[] {seleccio.size()}));
				resposta = true;
			} catch (Exception ex) {
				MissatgesHelper.error(request, getMessage(request, "error.no.massiu"));
				logger.error("No s'ha pogut validar el formulari massiu en la tasca " + tascaId, ex);
			}
		} else {
			try {
				tascaService.validar(tascaId, variables);
	        	MissatgesHelper.info(request, getMessage(request, "info.formulari.validat"));
				resposta = true;
			} catch (Exception ex) {
//				String tascaIdLog = getIdTascaPerLogs(entornId, tascaId);
//				MissatgesHelper.error(request, getMessage(request, "error.proces.peticio") + " " + tascaIdLog);
//				logger.error("No s'ha pogut guardar les dades del formulari en la tasca " + tascaIdLog, ex);
				MissatgesHelper.error(request, getMessage(request, "error.validar.formulari") + " " + tascaId);
				logger.error("No s'ha pogut validar el formulari en la tasca " + tascaId, ex);
			}
		}
		return resposta;
	}

	private boolean accioExecutarAccio(
			HttpServletRequest request,
			String tascaId,
			String accio,
			Object command) {
		boolean resposta = false;
		
		SessionManager sessionManager = SessionHelper.getSessionManager(request);
		Set<Long> seleccio = sessionManager.getSeleccioConsultaTasca();
		
		if (seleccio != null && !seleccio.isEmpty()) {
			try {
				String inici = (String) PropertyUtils.getSimpleProperty(command, "inici");
				boolean correu = (Boolean) PropertyUtils.getSimpleProperty(command, "correu");
				
				Date dInici = new Date();
				if (inici != null) {
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
					try {
						dInici = sdf.parse(inici);
					} catch (ParseException e) {}
				}
				
				String[] tascaIds = new String[seleccio.size()];
				int i = 0;
				for (Long tId: seleccio) {
					tascaIds[i++] = tId.toString();
				}

				
				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
				EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
				ExecucioMassivaDto dto = new ExecucioMassivaDto();
				dto.setDataInici(dInici);
				dto.setEnviarCorreu(correu);
				dto.setTascaIds(tascaIds);					
				dto.setExpedientTipusId(null);
				dto.setTipus(ExecucioMassivaTipusDto.EXECUTAR_TASCA);
				dto.setParam1("Accio");
				Object[] params = new Object[4];
				params[0] = entorn.getId();
				params[1] = accio;
				params[2] = auth.getCredentials();
				List<String> rols = new ArrayList<String>();
				for (GrantedAuthority gauth : auth.getAuthorities()) {
					rols.add(gauth.getAuthority());
				}
				params[3] = rols;
				dto.setParam2(execucioMassivaService.serialize(params));
				execucioMassivaService.crearExecucioMassiva(dto);
				
				tascaService.executarAccio(tascaId, accio);
				MissatgesHelper.info(request, getMessage(request, "info.tasca.massiu.accio", new Object[] {seleccio.size()}));
				
				resposta = true;
			} catch (Exception ex) {
				MissatgesHelper.error(request, getMessage(request, "error.no.massiu"));
				logger.error("No s'ha pogut guardar les dades del formulari massiu en la tasca " + tascaId, ex);
			}
		} else {
			try {
				tascaService.executarAccio(tascaId, accio);
				MissatgesHelper.info(request, getMessage(request, "info.accio.executat"));
				resposta = true;
			} catch (Exception ex) {
				if (ex.getCause() != null && ex.getCause() instanceof ValidationException) {
					MissatgesHelper.error(
		        			request,
		        			getMessage(request, "error.validacio.tasca") + " " + tascaId + ": " + ex.getCause().getMessage());
				} else {
					MissatgesHelper.error(
		        			request,
		        			getMessage(request, "error.executar.accio") + " " + tascaId + ": " + 
		        					(ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage()));
					logger.error("No s'ha pogut executar l'acció " + tascaId, ex);
				}
			}
		}
		return resposta;
	}
	
	private boolean accioCompletarForm(
			HttpServletRequest request,
			String tascaId,
			String transicio,
			Object command) {
		ExpedientTascaDto tasca = tascaService.findAmbIdPerTramitacio(tascaId);
		String transicio_sortida = null;
		for (String outcome: tasca.getOutcomes()) {
			if (outcome != null && outcome.equals(transicio)) {
				transicio_sortida = outcome;
				break;
			}
		}
		
		boolean resposta = false;
		
		SessionManager sessionManager = SessionHelper.getSessionManager(request);
		Set<Long> seleccio = sessionManager.getSeleccioConsultaTasca();
		
		if (seleccio != null && !seleccio.isEmpty()) {
			try {
				String inici = (String) PropertyUtils.getSimpleProperty(command, "inici");
				boolean correu = (Boolean) PropertyUtils.getSimpleProperty(command, "correu");
				
				Date dInici = new Date();
				if (inici != null) {
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
					try {
						dInici = sdf.parse(inici);
					} catch (ParseException e) {}
				}
				
				String[] tascaIds = new String[seleccio.size()];
				int i = 0;
				for (Long tId: seleccio) {
					tascaIds[i++] = tId.toString();
				}

				
				EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
				ExecucioMassivaDto dto = new ExecucioMassivaDto();
				dto.setDataInici(dInici);
				dto.setEnviarCorreu(correu);
				dto.setTascaIds(tascaIds);
//				dto.setExpedientTipusId(expTipusId);
				dto.setTipus(ExecucioMassivaTipusDto.EXECUTAR_TASCA);
				dto.setParam1("Completar");
				Object[] params = new Object[4];
				params[0] = entorn.getId();
				params[1] = transicio;
				params[2] = auth.getCredentials();
				List<String> rols = new ArrayList<String>();
				for (GrantedAuthority gauth : auth.getAuthorities()) {
					rols.add(gauth.getAuthority());
				}
				params[3] = rols;
				dto.setParam2(execucioMassivaService.serialize(params));
				execucioMassivaService.crearExecucioMassiva(dto);
				
				tascaService.completar(tascaId, transicio_sortida);
				MissatgesHelper.info(request, getMessage(request, "info.tasca.massiu.completar", new Object[] {seleccio.size()}));
				resposta = true;
			} catch (Exception ex) {
				if (ex.getCause() != null && ex.getCause() instanceof ValidationException) {
					MissatgesHelper.error(
		        			request,
		        			getMessage(request, "error.validacio.tasca") + " " + tascaId + ": " + ex.getCause().getMessage());
				} else {
					MissatgesHelper.error(
		        			request,
		        			getMessage(request, "error.finalitzar.tasca") + " " + tascaId + ": " + 
		        					(ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage()));
					logger.error("No s'ha pogut finalitzar la tasca massiu" + tascaId, ex);
				}
			}
		} else {
			try {
				tascaService.completar(tascaId, transicio_sortida);
				MissatgesHelper.info(request, getMessage(request, "info.tasca.completat"));
				resposta = true;
			} catch (Exception ex) {
				if (ex.getCause() != null && ex.getCause() instanceof ValidationException) {
					MissatgesHelper.error(
		        			request,
		        			getMessage(request, "error.validacio.tasca") + " " + tascaId + ": " + ex.getCause().getMessage());
				} else {
					MissatgesHelper.error(
		        			request,
		        			getMessage(request, "error.finalitzar.tasca") + " " + tascaId + ": " + 
		        					(ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage()));
					logger.error("No s'ha pogut finalitzar la tasca " + tascaId, ex);
				}
	        }
		}
		return resposta;
	}

	@RequestMapping(value = {"/{expedientId}/tasca/{tascaId}/camp/{campId}/valorsSeleccio", 
							"/{expedientId}/tasca/{tascaId}/form/camp/{campId}/valorsSeleccio"}, method = RequestMethod.GET)
	@ResponseBody
	public List<SeleccioOpcioDto> valorsSeleccio(
			HttpServletRequest request,
			@PathVariable String tascaId,
			@PathVariable Long campId,
			Model model) {
		return tascaService.findllistaValorsPerCampDesplegable(
				tascaId,
				campId,
				null,
				new HashMap<String, Object>());
	}

	@RequestMapping(value = {"/camp/{campId}/valorSeleccioInicial/{valor}"}, method = RequestMethod.GET)
	@ResponseBody
	public SeleccioOpcioDto valorsSeleccioInicial(
			HttpServletRequest request,
			@PathVariable Long campId,
			@PathVariable String valor,
			Model model) {		
		return valorsSeleccioInicial(
				request,
				null,
				campId,
				valor,
				model);
	}

	@RequestMapping(value = {"/camp/{campId}/valorsSeleccio"}, method = RequestMethod.GET)
	@ResponseBody
	public List<SeleccioOpcioDto> valorsSeleccio(
			HttpServletRequest request,
			@PathVariable Long campId,
			Model model) {
		return valorsSeleccio(
				request,
				null,
				campId,
				model);
	}

	@RequestMapping(value = {"/{expedientId}/tasca/{tascaId}/camp/{campId}/valorsSeleccio/{valor}", 
							"/{expedientId}/tasca/{tascaId}/form/camp/{campId}/valorsSeleccio/{valor}"}, method = RequestMethod.GET)
	@ResponseBody
	public List<SeleccioOpcioDto> valorsSeleccio(
			HttpServletRequest request,
			@PathVariable String tascaId,
			@PathVariable Long campId,
			@PathVariable String valor,
			Model model) {
		return tascaService.findllistaValorsPerCampDesplegable(
				tascaId,
				campId,
				valor,
				new HashMap<String, Object>());
	}

	@RequestMapping(value = {"/{expedientId}/tasca/{tascaId}/camp/{campId}/valorSeleccioInicial/{valor}", 
							 "/{expedientId}/tasca/{tascaId}/form/camp/{campId}/valorSeleccioInicial/{valor}"}, method = RequestMethod.GET)
	@ResponseBody
	public SeleccioOpcioDto valorsSeleccioInicial(
			HttpServletRequest request,
			@PathVariable String tascaId,
			@PathVariable Long campId,
			@PathVariable String valor,
			Model model) {
		List<SeleccioOpcioDto> valorsSeleccio = tascaService.findllistaValorsPerCampDesplegable(
				tascaId,
				campId,
				null,
				getMapDelsValors(valor));
		for (SeleccioOpcioDto sel : valorsSeleccio) {
			if (sel.getCodi().equals(valor)) {
				return sel;
			}
		}
		return new SeleccioOpcioDto();
	}

	@ModelAttribute("listTerminis")
	public List<ParellaCodiValorDto> valors12(HttpServletRequest request) {
		List<ParellaCodiValorDto> resposta = new ArrayList<ParellaCodiValorDto>();
		for (int i=0; i <= 12 ; i++)		
			resposta.add(new ParellaCodiValorDto(String.valueOf(i), i));
		return resposta;
	}

	private String getReturnUrl(HttpServletRequest request, Long expedientId, String tascaId) {
		if (ModalHelper.isModal(request))
			return "redirect:/modal/v3/expedient/" + expedientId + "/tasca/" + tascaId + "/form";
		else
			return "redirect:/v3/expedient/" + expedientId + "/tasca/" + tascaId + "/form";
	
	}
	private Map<String, Object> getMapDelsValors(String valors) {
		if (valors == null)
			return null;
		Map<String, Object> resposta = new HashMap<String, Object>();
		String[] parelles = valors.split(",");
		for (int i = 0; i < parelles.length; i++) {
			String[] parts = parelles[i].split(":");
			if (parts.length == 2)
				resposta.put(parts[0], parts[1]);
		}
		return resposta;
	}
	
	private Map<String, String> getFormRecursParams(String text) {
		Map<String, String> params = new HashMap<String, String>();
		Pattern pattern = Pattern.compile(TAG_PARAM_REGEXP);
		Matcher matcher = pattern.matcher(text);
		while (matcher.find()) {
			String[] paramParts = matcher.group(1).split(":");
			if (paramParts.length == 2) {
				params.put(paramParts[0], paramParts[1]);
			}
		}
		return params;
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

	private static final Logger logger = LoggerFactory.getLogger(TascaTramitacioController.class);

}
