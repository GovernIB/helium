/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import java.math.BigDecimal;
import java.text.DecimalFormat;
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

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import net.conselldemallorca.helium.core.model.dto.ParellaCodiValorDto;
import net.conselldemallorca.helium.jbpm3.handlers.exception.ValidationException;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
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
			Model model) {
		SessionManager sessionManager = SessionHelper.getSessionManager(request);
		Set<Long> seleccio = sessionManager.getSeleccioConsultaTasca();
		if (seleccio == null || seleccio.isEmpty()) {
			MissatgesHelper.error(request, getMessage(request, "error.no.tasc.selec"));
			return "redirect:/v3/tasca";
		}
		String tascaId = String.valueOf(seleccio.iterator().next());
		
		model.addAttribute("numTascaMassiva", seleccio.size());
		getModelTramitacio(tascaId, model, inici, correu);
		model.addAttribute("command", populateCommand(request, tascaId, model));
		
		return "v3/expedientTascaTramitacio";
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
			Model model) {		
		if (tascaId != null && !tascaId.isEmpty()) {
			try {
				return TascaFormHelper.getCommandBuitForCamps(
						tascaService.findDades(tascaId),	// tascaDadas
						new HashMap<String, Object>(),		// campsAddicionals
						new HashMap<String, Class>(), 		// campsAddicionalsClasses
						false);								// perFiltre
			} catch (TascaNotFoundException ex) {
				MissatgesHelper.error(request, ex.getMessage());
				logger.error("No s'ha pogut trobar la tasca: " + ex.getMessage(), ex);
			} catch (Exception ignored) {} 
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	public Object populateCommand(
			HttpServletRequest request, 
			String tascaId,
			Model model) {		
		if (tascaId != null && !tascaId.isEmpty()) {
			try {
				return TascaFormHelper.getCommandForCamps(
						tascaService.findDades(tascaId),	// tascaDadas
						new HashMap<String, Object>(),		// valors
						new HashMap<String, Object>(),		// campsAddicionals
						new HashMap<String, Class>(), 		// campsAddicionalsClasses
						false);								// perFiltre
			} catch (TascaNotFoundException ex) {
				MissatgesHelper.error(request, ex.getMessage());
				logger.error("No s'ha pogut trobar la tasca: " + ex.getMessage(), ex);
			} catch (Exception ignored) {} 
		}
		return null;
	}
	
	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}", method = RequestMethod.GET)
	public String tramitar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String tascaId,
			Model model) {
		SessionHelper.removeAttribute(request, SessionHelper.VARIABLE_TASCA_ERRROR);
		if (ModalHelper.isModal(request))
			return "redirect:/modal/v3/expedient/" + expedientId + "/tasca/" + tascaId + "/form";
		else
			return "redirect:/v3/expedient/" + expedientId + "/tasca/" + tascaId + "/form";
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
			@PathVariable boolean isTramitacioMassiva,
			@PathVariable String inici,
			@PathVariable boolean correu,
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
			@PathVariable boolean isTramitacioMassiva,
			@PathVariable String inici,
			@PathVariable boolean correu,
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
			@PathVariable boolean isTramitacioMassiva,
			@PathVariable String inici,
			@PathVariable boolean correu,
//			@PathVariable String transicio,
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
			completarForm(request, tascaId, transicio);
			status.setComplete();
			getModelTramitacio(tascaId, (Model)model);
			TascaFormHelper.ompleMultiplesBuits(command, tascaDadas, false);
		} else {
			MissatgesHelper.error(request, getMessage(request, "error.no.entorn.selec"));			
		}
		return "v3/expedientTascaTramitacio";
//			boolean okCompletar = accioCompletarTasca(
//					request,
//					entorn.getId(),
//					id,
//					finalitzarAmbOutcome);
//			if (okCompletar) {
//				return "redirect:/v3/expedient/"+expedientId;
//			}
//		} else {
//			MissatgesHelper.error(request, getMessage(request, "error.no.entorn.selec"));			
//		}
//		
//		return "redirect:/v3/expedient/"+expedientId+"/tasca/"+tascaId+"/form";
	}
	
	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/form/restaurar", method = RequestMethod.POST)
	public String formRestaurar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String tascaId,
			@PathVariable boolean isTramitacioMassiva,
			@PathVariable String inici,
			@PathVariable boolean correu,
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
		
		return "redirect:/v3/expedient/"+expedientId+"/tasca/"+tascaId+"/form";
	}
	
	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/form/accio/{accioCamp}", method = RequestMethod.POST)
	public String formAccio(
	HttpServletRequest request,
	@PathVariable Long expedientId,
	@PathVariable String tascaId,
	@PathVariable boolean isTramitacioMassiva,
	@PathVariable String inici,
	@PathVariable boolean correu,
	@PathVariable String accioCamp,
	@ModelAttribute("command") Object command, 
	BindingResult result, 
	SessionStatus status, 
	ModelMap model) {
		EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
		if (entorn != null) {			
//			this.validatorGuardar = new TascaFormValidatorHelper(tascaService, false);
//			
//			ExpedientTascaDto tasca = (ExpedientTascaDto) model.get("tasca");
//			List<TascaDadaDto> tascaDadas = tascaService.findDades(tascaId);
//		
//			validatorGuardar.validate(command, result);
//			if (result.hasErrors()) {
//				MissatgesHelper.error(request, getMessage(request, "error.guardar.dades"));
//				return "redirect:/v3/expedient/"+expedientId+"/tasca/"+tascaId+"/form";
//			}
//			boolean ok = accioGuardarForm(request, entorn.getId(), tascaId, tascaDadas, command);
//			if (!ok) {
//				MissatgesHelper.error(request, getMessage(request, "error.guardar.dades"));
//				return "redirect:/v3/expedient/"+expedientId+"/tasca/"+tascaId+"/form";
//			}
//			ok = accioExecutarAccio(request, entorn.getId(), tascaId, accioCamp);
//			if (!ok) {
//				MissatgesHelper.error(request, getMessage(request, "error.guardar.dades"));
//				return "redirect:/v3/expedient/"+expedientId+"/tasca/"+tascaId+"/form";
//			}
		} else {
			MissatgesHelper.error(request, getMessage(request, "error.no.entorn.selec"));			
		}
		
		return "redirect:/v3/expedient/"+expedientId+"/tasca/"+tascaId+"/form";
	}
	
	private void guardarForm(
			TascaFormValidatorHelper validator, 
			Map<String, Object> variables, 
			Object command, 
			BindingResult result, 
			HttpServletRequest request,
			String tascaId) {
		validator.validate(command, result);
		if (result.hasErrors()) {
			MissatgesHelper.error(request, getMessage(request, "error.guardar.dades"));
		} else if (!accioGuardarForm(request, tascaId, variables)) {
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
		validator.setValidarObligatoris(true);
		validator.validate(command, result);
		if (result.hasErrors()) {
			MissatgesHelper.error(request, getMessage(request, "error.validacio"));
			MissatgesHelper.errorGlobal(request, result, getMessage(request, "error.validacio"));
			System.out.println(result);
		} else if (!accioValidarForm(request, tascaId, variables)) {
			MissatgesHelper.error(request, getMessage(request, "error.validar.dades"));
		}
	}
	
	private void completarForm(
			HttpServletRequest request,
			String tascaId,
			String transicio) {
		if (!accioCompletarForm(request, tascaId, transicio)) {
			MissatgesHelper.error(request, getMessage(request, "error.validar.dades"));
		}
	}
	
//	public String formPost(
//			HttpServletRequest request,
//			@PathVariable Long expedientId,
//			@PathVariable String tascaId,
//			@RequestParam(value = "id", required = false) String id,
//			@RequestParam(value = "submit", required = false) String submit,
//			@RequestParam(value = "submitar", required = false) String submitar,
//			@RequestParam(value = "helMultipleIndex", required = false) Integer index,
//			@RequestParam(value = "helMultipleField", required = false) String field,
//			@RequestParam(value = "iframe", required = false) String iframe,
//			@RequestParam(value = "registreEsborrarCodi", required = false) String registreEsborrarCodi,
//			@RequestParam(value = "registreEsborrarIndex", required = false) Integer registreEsborrarIndex,
//			@RequestParam(value = "helAccioCamp", required = false) String accioCamp,
//			@RequestParam(value = "helCampFocus", required = false) String campFocus,
//			@RequestParam(value = "helFinalitzarAmbOutcome", required = false) String finalitzarAmbOutcome,
//			@ModelAttribute("command") Object command, 
//			BindingResult result, 
//			SessionStatus status, 
//			ModelMap model) {
//		EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
//		if (entorn != null) {			
//			this.validatorGuardar = new TascaFormValidatorHelper(tascaService, false);
//			this.validatorValidar = new TascaFormValidatorHelper(tascaService);
//			
//			boolean opValidar = "validate".equals(submit) || "validate".equals(submitar);
//			boolean opSubmit = "submit".equals(submit)  || "submit".equals(submitar);
//			boolean opRestore = "restore".equals(submit) || "restore".equals(submitar);
//			
//			ExpedientTascaDto tasca = (ExpedientTascaDto) model.get("tasca");
//			List<TascaDadaDto> tascaDadas = tascaService.findDades(id);
//
//			if (campFocus != null) {
//				String[] partsCampFocus = campFocus.split("#");
//				if (partsCampFocus.length == 2) {
//					request.getSession().setAttribute(VARIABLE_SESSIO_CAMP_FOCUS, partsCampFocus[0]);
//				} else {
//					request.getSession().setAttribute(VARIABLE_SESSIO_CAMP_FOCUS, campFocus);
//				}
//			}
//			if (opSubmit || opValidar || "@@@".equals(finalitzarAmbOutcome)) {
//				validatorGuardar.validate(command, result);
//				if (result.hasErrors()) {
//					MissatgesHelper.error(request, getMessage(request, "error.guardar.dades"));
//					return "redirect:/v3/expedient/"+expedientId+"/tasca/"+tascaId+"/form";
//				}
//				boolean ok = accioGuardarForm(request, entorn.getId(), id, tascaDadas, command);
//				if (!ok) {
//					MissatgesHelper.error(request, getMessage(request, "error.guardar.dades"));
//					return "redirect:/v3/expedient/"+expedientId+"/tasca/"+tascaId+"/form";
//				} else if (!opValidar){
//					MissatgesHelper.info(request, getMessage(request, "info.tasca.massiu.guardar"));
//				}
//				if (accioCamp != null && accioCamp.length() > 0) {
//					ok = accioExecutarAccio(request, entorn.getId(), id, accioCamp);
//					if (!ok) {
//						MissatgesHelper.error(request, getMessage(request, "error.guardar.dades"));
//						return "redirect:/v3/expedient/"+expedientId+"/tasca/"+tascaId+"/form";
//					}
//				}
//				if (opValidar || "@@@".equals(finalitzarAmbOutcome)) {
//					validatorValidar.validate(command, result);
//					try {
//						afegirVariablesDelProces(command, tasca);
//						Validator validator = TascaFormHelper.getBeanValidatorForCommand(tascaDadas);
//						Map<String, Object> valorsCommand = TascaFormHelper.getValorsFromCommand(tascaDadas, command, false);
//						validator.validate(TascaFormHelper.getCommandForCamps(tascaDadas,valorsCommand,request, null,null,false), result);
//					} catch (Exception ex) {
//						logger.error("S'han produit errors de validació", ex);
//						MissatgesHelper.error(request, getMessage(request, "error.validacio"));
//						return "redirect:/v3/expedient/"+expedientId+"/tasca/"+tascaId+"/form";
//					}
//					if (result.hasErrors()) {
//						MissatgesHelper.error(request, result, getMessage(request, "error.validacio"));
//						return "redirect:/v3/expedient/"+expedientId+"/tasca/"+tascaId+"/form";
//					}
//					ok = accioValidarForm(request, entorn.getId(), id, tascaDadas, command);
//					if (!ok) {
//						MissatgesHelper.error(request, getMessage(request, "error.validacio"));
//						return "redirect:/v3/expedient/"+expedientId+"/tasca/"+tascaId+"/form";
//					}
//				}
//				status.setComplete();
//				if (finalitzarAmbOutcome != null && !finalitzarAmbOutcome.equals("@#@")) {
//					boolean okCompletar = accioCompletarTasca(
//							request,
//							entorn.getId(),
//							id,
//							finalitzarAmbOutcome);
//					if (okCompletar) {
//						return "redirect:/v3/expedient/"+expedientId;
//					}
//				}
//			} else if (opRestore) {
//				boolean ok = accioRestaurarForm(request, entorn.getId(), id, tascaDadas, command);
//				if (ok) {
//					status.setComplete();
//				} else {
//					MissatgesHelper.error(request, getMessage(request, "error.validacio"));
//				}
//			} else if ("multipleAdd".equals(submit)) {
//				try {
//					if (field != null) {
//						PropertyUtils.setSimpleProperty(command, field, TascaFormHelper.addMultiple(field, command, tascaDadas));
//					}
//				} catch (Exception ex) {
//					MissatgesHelper.error(request, getMessage(request, "error.afegir.camp.multiple"));
//					logger.error("No s'ha pogut afegir el camp múltiple", ex);
//				}
//			} else if ("multipleRemove".equals(submit)) {
//				try {
//					if (field != null && index != null) {
//						PropertyUtils.setSimpleProperty(command, field, TascaFormHelper.deleteMultiple(field, command, tascaDadas, index));
//					}
//				} catch (Exception ex) {
//					MissatgesHelper.error(request, getMessage(request, "error.esborrar.camp.multiple"));
//					logger.error("No s'ha pogut esborrar el camp múltiple", ex);
//				}
//			} else {
//				status.setComplete();
//				if (registreEsborrarCodi != null && registreEsborrarIndex != null) {
//					accioEsborrarRegistre(request, entorn.getId(), id, registreEsborrarCodi, registreEsborrarIndex);
//				}
//				TascaFormHelper.guardarCommandTemporal(request, command);
//			}
//		} else {
//			MissatgesHelper.error(request, getMessage(request, "error.no.entorn.selec"));			
//		}
//		
//		return "redirect:/v3/expedient/"+expedientId+"/tasca/"+tascaId+"/form";
//	}
	private void getModelTramitacio(String tascaId, Model model) {
		getModelTramitacio(tascaId, model, null, false);
	}
	private void getModelTramitacio(String tascaId, Model model, String inici, boolean correu) {
		model.addAttribute("inici", inici);
		model.addAttribute("correu", correu);
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
			Map<String, Object> variables) {
		boolean resposta = true;
//		boolean massivaActiu = TramitacioMassiva.isTramitacioMassivaActiu(request, id);
//		String[] tascaIds;
		
//		if (massivaActiu) {
//			String[] parametresTram = TramitacioMassiva.getParamsTramitacioMassiva(request, id);
//			tascaIds = TramitacioMassiva.getTasquesTramitacioMassiva(request, id);
//			try {
//				// TODO es necessari l'expTipusId?
//				Long expTipusId = null;
//				// Restauram la primera tasca
//				if (tascaIds.length > 1) {
//					// Programam massivament la resta de tasques
//					String[] tIds = new String[tascaIds.length - 1];
//					int j = 0;
//					for (int i = 0; i < tascaIds.length; i++) {
//						if (!tascaIds[i].equals(id)) {
//							tIds[j++] = tascaIds[i];
//						}
//					}
//					
//					// Obtenim informació de l'execució massiva
//					
//					// Data d'inici
//					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
//					Date dInici = new Date();
//					if (parametresTram[0] != null) {
//						try {
//							dInici = sdf.parse(parametresTram[0]);
//						} catch (ParseException pe) {
//						}
//					}
//					
//					// Enviar correu
//					Boolean bCorreu = false;
//					if (parametresTram[1] != null && parametresTram[1].equals("true"))
//						bCorreu = true;
//
//					ExecucioMassivaDto dto = new ExecucioMassivaDto();
//					dto.setDataInici(dInici);
//					dto.setEnviarCorreu(bCorreu);
//					dto.setTascaIds(tIds);					
//					dto.setExpedientTipusId(expTipusId);
//					dto.setTipus(ExecucioMassivaTipusDto.EXECUTAR_TASCA);
//					dto.setParam1("Guardar");
//					Object[] params = new Object[2];
//					params[0] = entornId;
//					params[1] = variables;
//					dto.setParam2(execucioMassivaService.serialize(params));
//					execucioMassivaService.crearExecucioMassiva(dto);
//					
//					MissatgesHelper.info(request, getMessage(request, "info.tasca.massiu.completar", new Object[] {tIds.length}));
//				}
//			} catch (Exception e) {
//				MissatgesHelper.error(request, getMessage(request, "error.no.massiu"));
//				resposta = false;
//			}
//		} else {
			try {
				tascaService.guardar(tascaId, variables);
				MissatgesHelper.info(request, getMessage(request, "info.dades.form.guardat"));
			} catch (Exception ex) {
//				String tascaIdLog = getIdTascaPerLogs(entornId, tascaId);
//				MissatgesHelper.error(request, getMessage(request, "error.proces.peticio") + " " + tascaIdLog);
//				logger.error("No s'ha pogut guardar les dades del formulari en la tasca " + tascaIdLog, ex);
				MissatgesHelper.error(request, getMessage(request, "error.proces.peticio") + " " + tascaId);
				logger.error("No s'ha pogut guardar les dades del formulari en la tasca " + tascaId, ex);
				resposta = false;
			}
//		}
		return resposta;
	}

	private boolean accioValidarForm(
			HttpServletRequest request, 
			String tascaId, 
			Map<String, Object> variables) {
		boolean resposta = true;
//		TascaDto task = tascaService.getByIdSenseComprovacio(id);
//		if (TramitacioMassiva.isTramitacioMassivaActiu(request, id)) {
//			String[] tascaIds = TramitacioMassiva.getTasquesTramitacioMassiva(request, id);
//			String[] parametresTram = TramitacioMassiva.getParamsTramitacioMassiva(request, id);
//			try {
////				TascaDto task = tascaService.getByIdSenseComprovacio(id);
//				Long expTipusId = task.getExpedient().getTipus().getId();
//				Map<String, Object> variables = TascaFormUtil.getValorsFromCommand(
//    					camps,
//    					command,
//    					true,
//						false);
//				// Restauram la primera tasca
//				// ------------------------------------------
//				tascaService.validar(
//	        			entornId,
//	        			id,
//	        			variables,
//	    				true);
//				
//				if (tascaIds.length > 1) {
//					// Programam massivament la resta de tasques
//					// ------------------------------------------
//					String[] tIds = new String[tascaIds.length - 1];
//					int j = 0;
//					for (int i = 0; i < tascaIds.length; i++) {
//						if (!tascaIds[i].equals(id)) {
//							tIds[j++] = tascaIds[i];
//						}
//					}
//					// Obtenim informació de l'execució massiva
//					// Data d'inici
//					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
//					Date dInici = new Date();
//					if (parametresTram[0] != null) {
//						try { dInici = sdf.parse(parametresTram[0]); } catch (ParseException pe) {};
//					}
//					// Enviar correu
//					Boolean bCorreu = false;
//					if (parametresTram[1] != null && parametresTram[1].equals("true")) bCorreu = true;
//					
//					ExecucioMassivaDto dto = new ExecucioMassivaDto();
//					dto.setDataInici(dInici);
//					dto.setEnviarCorreu(bCorreu);
//					dto.setTascaIds(tIds);
//					dto.setExpedientTipusId(expTipusId);
//					dto.setTipus(ExecucioMassivaTipus.EXECUTAR_TASCA);
//					dto.setParam1("Validar");
//					Object[] params = new Object[2];
//					params[0] = entornId;
//					params[1] = variables;
//					dto.setParam2(execucioMassivaService.serialize(params));
//					execucioMassivaService.crearExecucioMassiva(dto);
//					
//					missatgeInfo(request, getMessage("info.tasca.massiu.validar", new Object[] {tIds.length}));
//				}
//			} catch (Exception e) {
//				missatgeError(request, getMessage("error.no.massiu"));
//				resposta = false;
//			}
//		} else {
			try {
	        	tascaService.validar(tascaId, variables);
	        	MissatgesHelper.info(request, getMessage(request, "info.formulari.validat"));
	        } catch (Exception ex) {
				MissatgesHelper.error(request, getMessage(request, "error.validar.formulari") + " " + tascaId);
				logger.error("No s'ha pogut validar el formulari en la tasca " + tascaId, ex);
				resposta = false;
	        }
//		}
		return resposta;
	}

	private boolean accioCompletarForm(
			HttpServletRequest request,
			String tascaId,
			String transicio) {
		ExpedientTascaDto tasca = tascaService.findAmbIdPerTramitacio(tascaId);
//				.getById(
//				entornId,
//				tascaId,
//				null,
//				null,
//				false,
//				false);
		String transicio_sortida = null;
		for (String outcome: tasca.getOutcomes()) {
			if (outcome != null && outcome.equals(transicio)) {
				transicio_sortida = outcome;
				break;
			}
		}
		boolean resposta = true;
//		boolean massivaActiu = TramitacioMassiva.isTramitacioMassivaActiu(request, tascaId);
//		String[] tascaIds;
//		if (massivaActiu) {
//			String[] parametresTram = TramitacioMassiva.getParamsTramitacioMassiva(request, id);
//			tascaIds = TramitacioMassiva.getTasquesTramitacioMassiva(request, id);
//			try {
//				TascaDto task = tascaService.getByIdSenseComprovacio(tascaIds[0]);
//				Long expTipusId = task.getExpedient().getTipus().getId();
//				
//				// Restauram la primera tasca
//				// ------------------------------------------
//				//tascaService.completar(entornId, id, true, null, transicio);
//				
//				if (tascaIds.length > 0) {
//					// Obtenim informació de l'execució massiva
//					// Data d'inici
//					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
//					Date dInici = new Date();
//					if (parametresTram[0] != null) {
//						try { dInici = sdf.parse(parametresTram[0]); } catch (ParseException pe) {};
//					}
//					// Enviar correu
//					Boolean bCorreu = false;
//					if (parametresTram[1] != null && parametresTram[1].equals("true")) bCorreu = true;
//					
//					ExecucioMassivaDto dto = new ExecucioMassivaDto();
//					dto.setDataInici(dInici);
//					dto.setEnviarCorreu(bCorreu);
//					dto.setTascaIds(tascaIds);
//					dto.setExpedientTipusId(expTipusId);
//					dto.setTipus(ExecucioMassivaTipus.EXECUTAR_TASCA);
//					dto.setParam1("Completar");
//					Object[] params = new Object[2];
//					params[0] = entornId;
//					params[1] = transicio;
//					dto.setParam2(execucioMassivaService.serialize(params));
//					execucioMassivaService.crearExecucioMassiva(dto);
//					
//					missatgeInfo(request, getMessage("info.tasca.massiu.completar", new Object[] {tascaIds.length}));
//				}
//			} catch (Exception e) {
//				missatgeError(request, getMessage("error.no.massiu"));
//				resposta = false;
//			}
//		} else {
			try {
				tascaService.completar(tascaId, transicio_sortida);
				MissatgesHelper.info(request, getMessage(request, "info.tasca.completat"));
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
				resposta = false;
	        }
//		}
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
