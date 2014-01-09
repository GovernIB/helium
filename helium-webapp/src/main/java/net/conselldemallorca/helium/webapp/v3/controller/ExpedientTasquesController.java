/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.jbpm3.integracio.ValidationException;
import net.conselldemallorca.helium.v3.core.api.dto.CampDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampDto.TipusCamp;
import net.conselldemallorca.helium.v3.core.api.dto.CampRegistreDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExecucioMassivaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExecucioMassivaDto.ExecucioMassivaTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.SeleccioOpcioDto;
import net.conselldemallorca.helium.v3.core.api.dto.TerminiDto;
import net.conselldemallorca.helium.v3.core.api.service.DissenyService;
import net.conselldemallorca.helium.v3.core.api.service.ExecucioMassivaService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.v3.core.api.service.TascaService;
import net.conselldemallorca.helium.v3.core.helper.TerminiHelper;
import net.conselldemallorca.helium.v3.core.repository.CampRepository;
import net.conselldemallorca.helium.webapp.mvc.util.TramitacioMassiva;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.NoDecorarHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;
import net.conselldemallorca.helium.webapp.v3.helper.TascaFormHelper;
import net.conselldemallorca.helium.webapp.v3.helper.TascaFormValidatorHelper;
import net.conselldemallorca.helium.webapp.v3.helper.TerminiTypeEditorHelper;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
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
 * Controlador per a la pàgina d'informació de l'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedient")
public class ExpedientTasquesController extends BaseExpedientController {

	private String TAG_PARAM_REGEXP = "<!--helium:param-(.+?)-->";
	public static final String VARIABLE_SESSIO_CAMP_FOCUS = "helCampFocus";

	@Resource(name = "expedientServiceV3")
	private ExpedientService expedientService;
	@Resource(name = "tascaServiceV3")
	private TascaService tascaService;
	@Resource(name="dissenyServiceV3")
	private DissenyService dissenyService;
	@Resource
	private CampRepository campRepository;
	@Resource(name="execucioMassivaServiceV3")
	private ExecucioMassivaService execucioMassivaService;
	@Resource(name="TerminiHelperV3")
	private TerminiHelper terminiHelper;

	private Validator validatorGuardar;
	private Validator validatorValidar;

	@RequestMapping(value = "/{expedientId}/tasques", method = RequestMethod.GET)
	public String tasques(HttpServletRequest request, @PathVariable Long expedientId, Model model) {
		if (!NoDecorarHelper.isRequestSenseDecoracio(request)) {
			return mostrarInformacioExpedientPerPipella(request, expedientId, model, "tasques", expedientService);
		}
		model.addAttribute("expedientId", expedientId);
		model.addAttribute("tasques", expedientService.findTasquesPerExpedient(expedientId));
		return "v3/expedientTasques";
	}
	
	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/reprende", 
			method = RequestMethod.GET)
	public String iniciarTasca(HttpServletRequest request, 
			@PathVariable Long expedientId, 
			@PathVariable String tascaId, Model model) {
		EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
		if (entorn != null) {			
			try {
				expedientService.findTasquesPerExpedient(expedientId);
				terminiHelper.iniciar(
						expedientId,//terminiId,
						tascaId,
						new Date(),
						true);
				MissatgesHelper.info(request, getMessage(request, "info.termini.iniciat") );
			} catch (Exception ex) {
				MissatgesHelper.error(request, getMessage(request, "error.iniciar.termini"));
	        	logger.error("No s'ha pogut iniciar el termini", ex);
			}
		} else {
			MissatgesHelper.error(request, getMessage(request, "error.no.entorn.selec"));			
		}
		
		return "v3/expedientTasques";
	}

	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/tramitar", 
			method = RequestMethod.GET)
	public String tramitar(HttpServletRequest request, 
			@PathVariable Long expedientId, 
			@PathVariable String tascaId, Model model) {
		return "redirect:/v3/expedient/" + expedientId + "/tasca/" + tascaId + "/form";
	}

	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/form",
			method = RequestMethod.GET)
	public String form(HttpServletRequest request, 
			@PathVariable Long expedientId,
			@PathVariable String tascaId, Model model) {
		NoDecorarHelper.marcarNoCapsaleraNiPeu(request);

		model.addAttribute("expedientId", expedientId);
		model.addAttribute("dades", tascaService.findDadesPerTasca(tascaId));
		model.addAttribute("tasca", expedientService.getTascaPerExpedient(expedientId, tascaId));
		return "v3/expedientTascaForm";
	}

	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/camp/{campId}/valorsSeleccio", 
			method = RequestMethod.GET)
	@ResponseBody
	public List<SeleccioOpcioDto> valorsSeleccio(HttpServletRequest request, @PathVariable Long expedientId, @PathVariable String tascaId, @PathVariable Long campId, Model model) {
		return tascaService.findOpcionsSeleccioPerCampTasca(tascaId, campId);
	}
	
	@ModelAttribute("command")
	private Object populateCommand(
			HttpServletRequest request, 
			String id,
			ModelMap model) {
		EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
		if (entorn != null && id != null) {
			Object command = null;
			Object commandSessio = TascaFormHelper.recuperarCommandTemporal(request, true);
			ExpedientTascaDto tasca = tascaService.getById(entorn.getId(), id, null, null, true, true);
			if (commandSessio != null) {
				List<CampDto> camps = new ArrayList<CampDto>();
				for (CampTascaDto campTasca : tasca.getCamps())
					camps.add(campTasca.getCamp());
				tasca = tascaService.getById(entorn.getId(), id, null, TascaFormHelper.getValorsFromCommand(camps, commandSessio, true, false), true, true);
				model.addAttribute("valorsPerSuggest", TascaFormHelper.getValorsPerSuggest(tasca, commandSessio));
				command = commandSessio;
			} else {
				try {
					Map<String, Object> campsAddicionals = new HashMap<String, Object>();
					campsAddicionals.put("id", id);
					campsAddicionals.put("entornId", entorn.getId());
					campsAddicionals.put("expedientTipusId", tasca.getExpedient().getTipus().getId());
					campsAddicionals.put("definicioProcesId", tasca.getDefinicioProces().getId());
					campsAddicionals.put("procesScope", null);
					@SuppressWarnings("rawtypes")
					Map<String, Class> campsAddicionalsClasses = new HashMap<String, Class>();
					campsAddicionalsClasses.put("id", String.class);
					campsAddicionalsClasses.put("entornId", Long.class);
					campsAddicionalsClasses.put("expedientTipusId", Long.class);
					campsAddicionalsClasses.put("definicioProcesId", Long.class);
					campsAddicionalsClasses.put("procesScope", Map.class);
					command = TascaFormHelper.getCommandForTasca(tasca, campsAddicionals, campsAddicionalsClasses);
					model.addAttribute("valorsPerSuggest", TascaFormHelper.getValorsPerSuggest(tasca, command));
				} catch (Exception ignored) {
				}
			}
			if (tasca.getRecursForm() != null && tasca.getRecursForm().length() > 0) {
				try {
					byte[] contingut = dissenyService.getDeploymentResource(tasca.getDefinicioProces().getId(), tasca.getRecursForm());
					model.addAttribute("formRecursParams", getFormRecursParams(new String(contingut, "UTF-8")));
				} catch (Exception ex) {
					logger.error("No s'han pogut extreure els parametres del recurs", ex);
				}
			}
			model.addAttribute("tasca", tasca);
			return command;
		}
		return null;
	}
	
	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/completar", method = RequestMethod.POST)
	public String completar( 
			@PathVariable Long expedientId,
			@PathVariable Long tascaId,
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "submit", required = false) String submit,
			ModelMap model) {
		EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
		if (entorn != null) {		
			boolean ok = accioCompletarTasca(
					request,
					entorn.getId(),
					id,
					submit);
			if (ok) {
				return "redirect:/v3/expedient/"+expedientId;
			}
		} else {
			MissatgesHelper.error(request, getMessage(request, "error.no.entorn.selec"));			
		}
		
		return "redirect:/v3/expedient/"+expedientId+"/tasca/"+tascaId+"/form";
	}
	
	private boolean accioCompletarTasca(
			HttpServletRequest request,
			Long entornId,
			String id,
			String submit) {
		ExpedientTascaDto tasca = tascaService.getById(
				entornId,
				id,
				null,
				null,
				false,
				false);
		String transicio = null;
		for (String outcome: tasca.getOutcomes()) {
			if (outcome != null && outcome.equals(submit.trim())) {
				transicio = outcome;
				break;
			}
		}
		boolean resposta = true;
		boolean massivaActiu = TramitacioMassiva.isTramitacioMassivaActiu(request, id);
		String[] tascaIds;
		if (massivaActiu) {
			String[] parametresTram = TramitacioMassiva.getParamsTramitacioMassiva(request, id);
			tascaIds = TramitacioMassiva.getTasquesTramitacioMassiva(request, id);
			try {
				ExpedientTascaDto task = tascaService.getByIdSenseComprovacio(tascaIds[0]);
				Long expTipusId = task.getExpedient().getTipus().getId();
				
				// Restauram la primera tasca
				// ------------------------------------------
				tascaService.completar(entornId, id, true, null, transicio);
				
				if (tascaIds.length > 1) {
					// Programam massivament la resta de tasques
					// ------------------------------------------
					String[] tIds = new String[tascaIds.length - 1];
					int j = 0;
					for (int i = 0; i < tascaIds.length; i++) {
						if (!tascaIds[i].equals(id)) {
							tIds[j++] = tascaIds[i];
						}
					}
					// Obtenim informació de l'execució massiva
					// Data d'inici
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
					Date dInici = new Date();
					if (parametresTram[0] != null) {
						try { dInici = sdf.parse(parametresTram[0]); } catch (ParseException pe) {};
					}
					// Enviar correu
					Boolean bCorreu = false;
					if (parametresTram[1] != null && parametresTram[1].equals("true")) bCorreu = true;
					
					ExecucioMassivaDto dto = new ExecucioMassivaDto();
					dto.setDataInici(dInici);
					dto.setEnviarCorreu(bCorreu);
					dto.setTascaIds(tIds);
					dto.setExpedientTipusId(expTipusId);
					dto.setTipus(ExecucioMassivaTipusDto.EXECUTAR_TASCA);
					dto.setParam1("Completar");
					Object[] params = new Object[2];
					params[0] = entornId;
					params[1] = transicio;
					dto.setParam2(execucioMassivaService.serialize(params));
					execucioMassivaService.crearExecucioMassiva(dto);
					
					MissatgesHelper.info(request, getMessage(request, "info.tasca.massiu.completar", new Object[] {tIds.length}));
				}
			} catch (Exception e) {
				MissatgesHelper.error(request, getMessage(request, "error.no.massiu"));
				resposta = false;
			}
		} else {
			try {
				tascaService.completar(entornId, id, true, null, transicio);
				MissatgesHelper.info(request, getMessage(request, "info.tasca.completat"));
			} catch (Exception ex) {
				resposta = false;
				String tascaIdLog = getIdTascaPerLogs(entornId, id);
				if (ex.getCause() != null && ex.getCause() instanceof ValidationException) {
					MissatgesHelper.error(request, getMessage(request, "error.validacio.tasca") + " " + tascaIdLog + ": " + ex.getCause().getMessage());
				} else {
					MissatgesHelper.error(request, getMessage(request, "error.finalitzar.tasca") + " " + tascaIdLog);
					logger.error("No s'ha pogut finalitzar la tasca " + tascaIdLog, ex);
				}
			}
		}
		return resposta;
	}
	
	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/form", method = RequestMethod.POST)
	public String formPost(HttpServletRequest request, 
			@PathVariable Long expedientId,
			@PathVariable Long tascaId,
			@RequestParam(value = "id", required = false) String id, 
			@RequestParam(value = "submit", required = false) String submit, 
			@RequestParam(value = "submitar", required = false) String submitar, 
			@RequestParam(value = "helMultipleIndex", required = false) Integer index, 
			@RequestParam(value = "helMultipleField", required = false) String field, 
			@RequestParam(value = "iframe", required = false) String iframe, 
			@RequestParam(value = "registreEsborrarId", required = false) Long registreEsborrarId,
			@RequestParam(value = "registreEsborrarIndex", required = false) Integer registreEsborrarIndex,
			@RequestParam(value = "helAccioCamp", required = false) String accioCamp, 
			@RequestParam(value = "helCampFocus", required = false) String campFocus,
			@RequestParam(value = "helFinalitzarAmbOutcome", required = false) String finalitzarAmbOutcome, 
			@ModelAttribute("command") Object command, 
			BindingResult result, 
			SessionStatus status, 
			ModelMap model) {
		EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
		if (entorn != null) {			
			this.validatorGuardar = new TascaFormValidatorHelper(tascaService, false);
			this.validatorValidar = new TascaFormValidatorHelper(tascaService);
			
			boolean opValidar = "validate".equals(submit) || "validate".equals(submitar);
			boolean opSubmit = "submit".equals(submit)  || "submit".equals(submitar);
			boolean opRestore = "restore".equals(submit) || "restore".equals(submitar);
			
			ExpedientTascaDto tasca = (ExpedientTascaDto) model.get("tasca");
			List<CampDto> camps = new ArrayList<CampDto>();
			for (CampTascaDto campTasca : tasca.getCamps()) {
				camps.add(campTasca.getCamp());
			}

			if (campFocus != null) {
				String[] partsCampFocus = campFocus.split("#");
				if (partsCampFocus.length == 2) {
					request.getSession().setAttribute(VARIABLE_SESSIO_CAMP_FOCUS, partsCampFocus[0]);
				} else {
					request.getSession().setAttribute(VARIABLE_SESSIO_CAMP_FOCUS, campFocus);
				}
			}
			if (opSubmit || opValidar || "@@@".equals(finalitzarAmbOutcome)) {
				validatorGuardar.validate(command, result);
				if (result.hasErrors()) {
					MissatgesHelper.error(request, getMessage(request, "error.guardar.dades"));
					return "redirect:/v3/expedient/"+expedientId+"/tasca/"+tascaId+"/form";
				}
				boolean ok = accioGuardarForm(request, entorn.getId(), id, tasca.getCamps(), command);
				if (!ok) {
					MissatgesHelper.error(request, getMessage(request, "error.guardar.dades"));
					return "redirect:/v3/expedient/"+expedientId+"/tasca/"+tascaId+"/form";
				} else if (!opValidar){
					MissatgesHelper.info(request, getMessage(request, "info.tasca.massiu.guardar"));
				}
				if (accioCamp != null && accioCamp.length() > 0) {
					ok = accioExecutarAccio(request, entorn.getId(), id, accioCamp);
					if (!ok) {
						MissatgesHelper.error(request, getMessage(request, "error.guardar.dades"));
						return "redirect:/v3/expedient/"+expedientId+"/tasca/"+tascaId+"/form";
					}
				}
				if (opValidar || "@@@".equals(finalitzarAmbOutcome)) {
					validatorValidar.validate(command, result);
					try {
						afegirVariablesDelProces(command, tasca);
						TascaFormHelper.getBeanValidatorForCommand(camps).validate(command, result);
					} catch (Exception ex) {
						logger.error("S'han produit errors de validació", ex);
						MissatgesHelper.error(request, getMessage(request, "error.validacio"));
						return "redirect:/v3/expedient/"+expedientId+"/tasca/"+tascaId+"/form";
					}
					if (result.hasErrors()) {
						MissatgesHelper.error(request, getMessage(request, "error.validacio"));
						return "redirect:/v3/expedient/"+expedientId+"/tasca/"+tascaId+"/form";
					}
					ok = accioValidarForm(request, entorn.getId(), id, camps, command);
					if (!ok) {
						MissatgesHelper.error(request, getMessage(request, "error.validacio"));
						return "redirect:/v3/expedient/"+expedientId+"/tasca/"+tascaId+"/form";
					}
				}
				status.setComplete();
				if (finalitzarAmbOutcome != null && !finalitzarAmbOutcome.equals("@#@")) {
					boolean okCompletar = accioCompletarTasca(
							request,
							entorn.getId(),
							id,
							finalitzarAmbOutcome);
					if (okCompletar) {
						return "redirect:/v3/expedient/"+expedientId;
					}
				}
			} else if (opRestore) {
				boolean ok = accioRestaurarForm(request, entorn.getId(), id, camps, command);
				if (ok) {
					status.setComplete();
				} else {
					MissatgesHelper.error(request, getMessage(request, "error.validacio"));
				}
			} else if ("multipleAdd".equals(submit)) {
				try {
					if (field != null) {
						PropertyUtils.setSimpleProperty(command, field, TascaFormHelper.addMultiple(field, command, camps));
					}
				} catch (Exception ex) {
					MissatgesHelper.error(request, getMessage(request, "error.afegir.camp.multiple"));
					logger.error("No s'ha pogut afegir el camp múltiple", ex);
				}
			} else if ("multipleRemove".equals(submit)) {
				try {
					if (field != null && index != null) {
						PropertyUtils.setSimpleProperty(command, field, TascaFormHelper.deleteMultiple(field, command, camps, index));
					}
				} catch (Exception ex) {
					MissatgesHelper.error(request, getMessage(request, "error.esborrar.camp.multiple"));
					logger.error("No s'ha pogut esborrar el camp múltiple", ex);
				}
			} else {
				status.setComplete();
				if (registreEsborrarId != null && registreEsborrarIndex != null) {
					accioEsborrarRegistre(request, entorn.getId(), id, registreEsborrarId, registreEsborrarIndex);
				}
				TascaFormHelper.guardarCommandTemporal(request, command);
			}
		} else {
			MissatgesHelper.error(request, getMessage(request, "error.no.entorn.selec"));			
		}
		
		return "redirect:/v3/expedient/"+expedientId+"/tasca/"+tascaId+"/form";
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
				new CustomBooleanEditor(false));
		binder.registerCustomEditor(
				Date.class,
				new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true));
		binder.registerCustomEditor(
				TerminiDto.class,
				new TerminiTypeEditorHelper());
	}

	private void afegirVariablesDelProces(Object command, ExpedientTascaDto tasca) throws Exception {
		InstanciaProcesDto instanciaProces = expedientService.getInstanciaProcesById(tasca.getProcessInstanceId(), false, false, false);
		PropertyUtils.setSimpleProperty(command, "procesScope", instanciaProces.getVariables());
	}

	private boolean accioGuardarForm(HttpServletRequest request, Long entornId, String id, List<CampTascaDto> tasca, Object command) {
		boolean resposta = true;
		boolean massivaActiu = TramitacioMassiva.isTramitacioMassivaActiu(request, id);
		String[] tascaIds;
		ExpedientTascaDto task = tascaService.getByIdSenseComprovacio(id);		

		List<CampDto> camps = new ArrayList<CampDto>();
		for (CampTascaDto campTasca : tasca) {
			camps.add(campTasca.getCamp());
		}
		
		Map<String, Object> variables = TascaFormHelper.getValorsFromCommand(camps, command, true, false);
				
		for (CampTascaDto camp : tasca) {
			if (camp.getCamp().getTipus().equals(TipusCamp.REGISTRE)) {
				guardarVariablesReg(request, camp, id);
			}
		}

		tascaService.guardarVariables(entornId, id, variables, null);
		
		if (massivaActiu) {
			String[] parametresTram = TramitacioMassiva.getParamsTramitacioMassiva(request, id);
			tascaIds = TramitacioMassiva.getTasquesTramitacioMassiva(request, id);
			try {				
				// Restauram la primera tasca

				if (tascaIds.length > 1) {
					// Programam massivament la resta de tasques
					String[] tIds = new String[tascaIds.length - 1];
					int j = 0;
					for (int i = 0; i < tascaIds.length; i++) {
						if (!tascaIds[i].equals(id)) {
							tIds[j++] = tascaIds[i];
						}
					}
					
					// Obtenim informació de l'execució massiva
					
					// Data d'inici
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
					Date dInici = new Date();
					if (parametresTram[0] != null) {
						try {
							dInici = sdf.parse(parametresTram[0]);
						} catch (ParseException pe) {
						}
					}
					
					// Enviar correu
					Boolean bCorreu = false;
					if (parametresTram[1] != null && parametresTram[1].equals("true"))
						bCorreu = true;

					ExecucioMassivaDto dto = new ExecucioMassivaDto();
					dto.setDataInici(dInici);
					dto.setEnviarCorreu(bCorreu);
					dto.setTascaIds(tIds);
					dto.setExpedientTipusId(task.getExpedient().getTipus().getId());
					dto.setTipus(ExecucioMassivaTipusDto.EXECUTAR_TASCA);
					dto.setParam1("Guardar");
					Object[] params = new Object[2];
					params[0] = entornId;
					params[1] = variables;
					dto.setParam2(execucioMassivaService.serialize(params));
					execucioMassivaService.crearExecucioMassiva(dto);
					
//					MissatgesHelper.info(request, getMessage(request, "info.tasca.massiu.guardar", new Object[] { tIds.length }));
				}
			} catch (Exception e) {
				MissatgesHelper.error(request, getMessage(request, "error.no.massiu"));
				resposta = false;
			}
		} else {
			try {
				MissatgesHelper.info(request, getMessage(request, "info.dades.form.guardat"));
			} catch (Exception ex) {
				String tascaIdLog = getIdTascaPerLogs(entornId, id);
				MissatgesHelper.error(request, getMessage(request, "error.proces.peticio") + " " + tascaIdLog);
				logger.error("No s'ha pogut guardar les dades del formulari en la tasca " + tascaIdLog, ex);
				resposta = false;
			}
		}
		return resposta;
	}

	private void guardarVariablesReg(HttpServletRequest request, CampTascaDto camp, String id) {	
		int i = 1;
		
		borrarTodosRegistres(request, id, camp.getCamp().getCodi());
		
		while (i < request.getParameterMap().size()) {
			Map<String, Object> variablesMultReg = new HashMap<String, Object>();
			int numMembres = camp.getCamp().getRegistreMembres().size();
			int salir = 0;
			for (CampRegistreDto registreMembre : camp.getCamp().getRegistreMembres()) {
				Object valor = null;
				boolean sinValor = false;
				String campMembre = registreMembre.getMembre().getCodi();
				if (registreMembre.getMembre().getTipus().equals(TipusCamp.BOOLEAN)) {
					variablesMultReg.put(campMembre, false);
				} else {
					variablesMultReg.put(campMembre, "");
				}
				if (request.getParameterMap().containsKey(camp.getCamp().getCodi()+"["+i+"]["+campMembre+"]")) {
					valor = request.getParameterMap().get(camp.getCamp().getCodi()+"["+i+"]["+campMembre+"]");
					valor = String.valueOf(((String[])valor)[0]);
					if (registreMembre.getMembre().getTipus().equals(TipusCamp.BOOLEAN)) {						
						valor = "on".equals(valor);
					} else if ("".equals(valor)) {
						sinValor = true;					
					}
					variablesMultReg.put(campMembre, valor);
				} else {
					sinValor = true;
				}
				
				if (sinValor) {
					salir++;
					
					if (numMembres == salir) {
						variablesMultReg.clear();
						break;
					}			
				} 
			}
			
			if (!variablesMultReg.isEmpty()) {
				List<Object> variablesRegTmp = new ArrayList<Object>();
				for (CampRegistreDto registreMembre : camp.getCamp().getRegistreMembres()) {
					String campMembre = registreMembre.getMembre().getCodi();
					variablesRegTmp.add(variablesMultReg.get(campMembre));
				}
				
				guardarRegistre(request, id, camp.getCamp().getCodi(), camp.getCamp().isMultiple(), variablesRegTmp.toArray());
			}
			
			i++;
		}
	}

	public void borrarTodosRegistres(
			HttpServletRequest request,
			String id,
			String campCodi) {
		EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
				
		tascaService.borrarVariables(
				entorn.getId(),
				id,
				campCodi,
				request.getUserPrincipal().getName());
	}
	
	private boolean accioValidarForm(HttpServletRequest request, Long entornId, String id, List<CampDto> camps, Object command) {
		boolean resposta = true;
		ExpedientTascaDto task = tascaService.getByIdSenseComprovacio(id);
		if (TramitacioMassiva.isTramitacioMassivaActiu(request, id)) {
			String[] tascaIds = TramitacioMassiva.getTasquesTramitacioMassiva(request, id);
			String[] parametresTram = TramitacioMassiva.getParamsTramitacioMassiva(request, id);
			try {
				Long expTipusId = task.getExpedient().getTipus().getId();
				Map<String, Object> variables = TascaFormHelper.getValorsFromCommand(camps, command, true, false);
				
				// Restauram la primera tasca
				tascaService.validar(entornId, id, variables, true);

				if (tascaIds.length > 1) {
					// Programam massivament la resta de tasques
					String[] tIds = new String[tascaIds.length - 1];
					int j = 0;
					for (int i = 0; i < tascaIds.length; i++) {
						if (!tascaIds[i].equals(id)) {
							tIds[j++] = tascaIds[i];
						}
					}
					
					// Obtenim informació de l'execució massiva
					
					// Data d'inici
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
					Date dInici = new Date();
					if (parametresTram[0] != null) {
						try {
							dInici = sdf.parse(parametresTram[0]);
						} catch (ParseException pe) {
						}
					}
					
					// Enviar correu
					Boolean bCorreu = false;
					if (parametresTram[1] != null && parametresTram[1].equals("true"))
						bCorreu = true;

					ExecucioMassivaDto dto = new ExecucioMassivaDto();
					dto.setDataInici(dInici);
					dto.setEnviarCorreu(bCorreu);
					dto.setTascaIds(tIds);
					dto.setExpedientTipusId(expTipusId);
					dto.setTipus(ExecucioMassivaTipusDto.EXECUTAR_TASCA);
					dto.setParam1("Validar");
					Object[] params = new Object[2];
					params[0] = entornId;
					params[1] = variables;
					dto.setParam2(execucioMassivaService.serialize(params));
					execucioMassivaService.crearExecucioMassiva(dto);

					MissatgesHelper.info(request, getMessage(request, "info.tasca.massiu.validar", new Object[] { tIds.length }));
				}
			} catch (Exception e) {
				MissatgesHelper.error(request, getMessage(request, "error.no.massiu"));
				resposta = false;
			}
		} else {
			try {
				tascaService.validar(entornId, id, TascaFormHelper.getValorsFromCommand(camps, command, true, false), true);
				MissatgesHelper.info(request, getMessage(request, "info.formulari.validat"));
			} catch (Exception ex) {
				String tascaIdLog = getIdTascaPerLogs(entornId, id);
				MissatgesHelper.error(request, getMessage(request, "error.validar.formulari") + " " + tascaIdLog);
				logger.error("No s'ha pogut validar el formulari en la tasca " + tascaIdLog, ex);
				resposta = false;
			}
		}
		return resposta;
	}

	private boolean accioRestaurarForm(HttpServletRequest request, Long entornId, String id, List<CampDto> camps, Object command) {
		boolean resposta = true;
		boolean massivaActiu = TramitacioMassiva.isTramitacioMassivaActiu(request, id);
		String[] tascaIds;
		ExpedientTascaDto task = tascaService.getByIdSenseComprovacio(id);
		if (massivaActiu) {
			String[] parametresTram = TramitacioMassiva.getParamsTramitacioMassiva(request, id);
			tascaIds = TramitacioMassiva.getTasquesTramitacioMassiva(request, id);
			try {
				Long expTipusId = task.getExpedient().getTipus().getId();

				// Restauram la primera tasca
				tascaService.restaurar(entornId, id);

				if (tascaIds.length > 1) {
					// Programam massivament la resta de tasques
					String[] tIds = new String[tascaIds.length - 1];
					int j = 0;
					for (int i = 0; i < tascaIds.length; i++) {
						if (!tascaIds[i].equals(id)) {
							tIds[j++] = tascaIds[i];
						}
					}

					// Obtenim informació de l'execució massiva
					
					// Data d'inici
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
					Date dInici = new Date();
					if (parametresTram[0] != null) {
						try {
							dInici = sdf.parse(parametresTram[0]);
						} catch (ParseException pe) {
						}
					}
					
					// Enviar correu
					Boolean bCorreu = false;
					if (parametresTram[1] != null && parametresTram[1].equals("true"))
						bCorreu = true;

					ExecucioMassivaDto dto = new ExecucioMassivaDto();
					dto.setDataInici(dInici);
					dto.setEnviarCorreu(bCorreu);
					dto.setTascaIds(tIds);
					dto.setExpedientTipusId(expTipusId);
					dto.setTipus(ExecucioMassivaTipusDto.EXECUTAR_TASCA);
					dto.setParam1("Restaurar");
					Long params = entornId;
					dto.setParam2(execucioMassivaService.serialize(params));
					execucioMassivaService.crearExecucioMassiva(dto);

					MissatgesHelper.info(request, getMessage(request, "info.tasca.massiu.restaurar", new Object[] { tIds.length }));
				}
			} catch (Exception e) {
				MissatgesHelper.error(request, getMessage(request, "error.no.massiu"));
				resposta = false;
			}
		} else {
			try {
				tascaService.restaurar(entornId, id);
				MissatgesHelper.info(request, getMessage(request, "info.formulari.restaurat"));
			} catch (Exception ex) {
				String tascaIdLog = getIdTascaPerLogs(entornId, id);
				MissatgesHelper.error(request, getMessage(request, "error.restaurar.formulari") + " " + tascaIdLog);
				logger.error("No s'ha pogut restaurar el formulari en la tasca " + tascaIdLog, ex);
				resposta = false;
			}
		}
		return resposta;
	}

	private boolean accioExecutarAccio(HttpServletRequest request, Long entornId, String id, String accio) {
		boolean resposta = true;
		boolean massivaActiu = TramitacioMassiva.isTramitacioMassivaActiu(request, id);
		String[] tascaIds;
		ExpedientTascaDto task = tascaService.getByIdSenseComprovacio(id);
		if (massivaActiu) {
			String[] parametresTram = TramitacioMassiva.getParamsTramitacioMassiva(request, id);
			tascaIds = TramitacioMassiva.getTasquesTramitacioMassiva(request, id);
			try {
				Long expTipusId = task.getExpedient().getTipus().getId();

				// Restauram la primera tasca
				tascaService.executarAccio(entornId, id, accio);

				if (tascaIds.length > 1) {
					// Programam massivament la resta de tasques
					String[] tIds = new String[tascaIds.length - 1];
					int j = 0;
					for (int i = 0; i < tascaIds.length; i++) {
						if (!tascaIds[i].equals(id)) {
							tIds[j++] = tascaIds[i];
						}
					}
					
					// Obtenim informació de l'execució massiva
					
					// Data d'inici
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
					Date dInici = new Date();
					if (parametresTram[0] != null) {
						try {
							dInici = sdf.parse(parametresTram[0]);
						} catch (ParseException pe) {
						}
					}
					
					// Enviar correu
					Boolean bCorreu = false;
					if (parametresTram[1] != null && parametresTram[1].equals("true"))
						bCorreu = true;

					ExecucioMassivaDto dto = new ExecucioMassivaDto();
					dto.setDataInici(dInici);
					dto.setEnviarCorreu(bCorreu);
					dto.setTascaIds(tIds);
					dto.setExpedientTipusId(expTipusId);
					dto.setTipus(ExecucioMassivaTipusDto.EXECUTAR_TASCA);
					dto.setParam1("Accio");
					Object[] params = new Object[2];
					params[0] = entornId;
					params[1] = accio;
					dto.setParam2(execucioMassivaService.serialize(params));
					execucioMassivaService.crearExecucioMassiva(dto);

					MissatgesHelper.info(request, getMessage(request, "info.tasca.massiu.accio", new Object[] { tIds.length }));
				}
			} catch (Exception e) {
				MissatgesHelper.error(request, getMessage(request, "error.no.massiu"));
				resposta = false;
			}
		} else {
			try {
				tascaService.executarAccio(entornId, id, accio);
				MissatgesHelper.info(request, getMessage(request, "info.accio.executat"));
			} catch (Exception ex) {
				String tascaIdLog = getIdTascaPerLogs(entornId, id);
				if (ex.getCause() != null && ex.getCause() instanceof ValidationException) {
					MissatgesHelper.error(request, getMessage(request, "error.validacio.tasca") + " " + tascaIdLog + ": " + ex.getCause().getMessage());
				} else {
					MissatgesHelper.error(request, getMessage(request, "error.executar.accio") + " " + tascaIdLog);
					logger.error("No s'ha pogut executar l'acció '" + accio + "' en la tasca " + tascaIdLog, ex);
				}
				resposta = false;
			}
		}
		return resposta;
	}

	private String getIdTascaPerLogs(Long entornId, String tascaId) {
		ExpedientTascaDto tascaActual = tascaService.getById(
				entornId,
				tascaId,
				null,
				null,
				false,
				false);
		return tascaActual.getNom() + " - " + tascaActual.getExpedient().getIdentificador();
	}

	private boolean accioEsborrarRegistre(HttpServletRequest request, Long entornId, String id, Long registreEsborrarId, Integer registreEsborrarIndex) {
		boolean massivaActiu = TramitacioMassiva.isTramitacioMassivaActiu(request, id);
		String[] tascaIds;
		Camp camp = campRepository.findById(registreEsborrarId);
		if (massivaActiu) {
			String[] parametresTram = TramitacioMassiva.getParamsTramitacioMassiva(request, id);
			tascaIds = TramitacioMassiva.getTasquesTramitacioMassiva(request, id);
			try {
				ExpedientTascaDto task = tascaService.getByIdSenseComprovacio(id);
				Long expTipusId = task.getExpedient().getTipus().getId();

				// Restauram la primera tasca
				// ------------------------------------------
				tascaService.esborrarRegistre(entornId, id, camp.getCodi(), registreEsborrarIndex.intValue());

				if (tascaIds.length > 1) {
					// Programam massivament la resta de tasques
					// ------------------------------------------
					String[] tIds = new String[tascaIds.length - 1];
					int j = 0;
					for (int i = 0; i < tascaIds.length; i++) {
						if (!tascaIds[i].equals(id)) {
							tIds[j++] = tascaIds[i];
						}
					}
					// Obtenim informació de l'execució massiva
					// Data d'inici
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
					Date dInici = new Date();
					if (parametresTram[0] != null) {
						try {
							dInici = sdf.parse(parametresTram[0]);
						} catch (ParseException pe) {
						}
					}
					
					// Enviar correu
					Boolean bCorreu = (parametresTram[1] != null && parametresTram[1].equals("true"));

					ExecucioMassivaDto dto = new ExecucioMassivaDto();
					dto.setDataInici(dInici);
					dto.setEnviarCorreu(bCorreu);
					dto.setTascaIds(tIds);
					dto.setExpedientTipusId(expTipusId);
					dto.setTipus(ExecucioMassivaTipusDto.EXECUTAR_TASCA);
					dto.setParam1("RegEsborrar");
					Object[] params = new Object[3];
					params[0] = entornId;
					params[1] = camp.getCodi();
					params[2] = registreEsborrarIndex;
					dto.setParam2(execucioMassivaService.serialize(params));
					execucioMassivaService.crearExecucioMassiva(dto);

					MissatgesHelper.info(request, getMessage(request, "info.tasca.massiu.registre.esborrar", new Object[] { tIds.length }));
				}
			} catch (Exception e) {
				MissatgesHelper.error(request, getMessage(request, "error.no.massiu"));
				return false;
			}
		} else {
			try {
				tascaService.esborrarRegistre(entornId, id, camp.getCodi(), registreEsborrarIndex.intValue());
			} catch (Exception ex) {
				MissatgesHelper.error(request, getMessage(request, "error.esborrar.registre"));
				logger.error("No s'ha pogut esborrar el registre", ex);
			}
		}
		return true;
	}
	
	public void guardarRegistre(
			HttpServletRequest request,
			String id,
			String campCodi,
			boolean multiple,
			Object[] valors) {
		EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();		
		tascaService.guardarRegistre(entorn.getId(), id, campCodi, valors, request.getUserPrincipal().getName());
		
		if (TramitacioMassiva.isTramitacioMassivaActiu(request, id)) {
			String[] tascaIds = TramitacioMassiva.getTasquesTramitacioMassiva(request, id);
			if (tascaIds.length > 1) {
				String[] parametresTram = TramitacioMassiva.getParamsTramitacioMassiva(request, id);
				try {
					ExpedientTascaDto task = tascaService.getByIdSenseComprovacio(id);
					Long expTipusId = task.getExpedient().getTipus().getId();
					
					// La primera tasca ja s'ha executat. Programam massivament la resta de tasques
					// ----------------------------------------------------------------------------
					String[] tIds = new String[tascaIds.length - 1];
					int j = 0;
					for (int i = 0; i < tascaIds.length; i++) {
						if (!tascaIds[i].equals(id)) {
							tIds[j++] = tascaIds[i];
						}
					}
					// Obtenim informació de l'execució massiva
					// Data d'inici
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
					Date dInici = new Date();
					if (parametresTram[0] != null) {
						try { dInici = sdf.parse(parametresTram[0]); } catch (ParseException pe) {};
					}
					// Enviar correu
					Boolean bCorreu = false;
					if (parametresTram[1] != null && parametresTram[1].equals("true")) bCorreu = true;
					
					ExecucioMassivaDto dto = new ExecucioMassivaDto();
					dto.setDataInici(dInici);
					dto.setEnviarCorreu(bCorreu);
					dto.setTascaIds(tIds);
					dto.setExpedientTipusId(expTipusId);
					dto.setTipus(ExecucioMassivaTipusDto.EXECUTAR_TASCA);
					dto.setParam1("RegGuardar");
					Object[] params = new Object[4];
					params[0] = entorn.getId();
					params[1] = campCodi;
					params[2] = valors;
					params[3] = Integer.valueOf(-1);
					dto.setParam2(execucioMassivaService.serialize(params));
					execucioMassivaService.crearExecucioMassiva(dto);
					
//					MissatgesHelper.error(request, getMessage(request, "info.tasca.massiu.registre.guardar"));
				} catch (Exception e) {
					MissatgesHelper.error(request, getMessage(request, "error.no.massiu"));
				}
			}
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(ExpedientTasquesController.class);
}
