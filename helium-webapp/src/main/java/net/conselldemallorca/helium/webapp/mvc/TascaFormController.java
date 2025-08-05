/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;

import net.conselldemallorca.helium.core.model.dto.ExecucioMassivaDto;
import net.conselldemallorca.helium.core.model.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.core.model.dto.TascaDto;
import net.conselldemallorca.helium.core.model.dto.TascaLlistatDto;
import net.conselldemallorca.helium.core.model.exception.NotFoundException;
import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.CampTasca;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.ExecucioMassiva.ExecucioMassivaTipus;
import net.conselldemallorca.helium.core.model.service.DissenyService;
import net.conselldemallorca.helium.core.model.service.ExecucioMassivaService;
import net.conselldemallorca.helium.core.model.service.ExpedientService;
import net.conselldemallorca.helium.core.model.service.TascaService;
import net.conselldemallorca.helium.v3.core.api.exception.TramitacioException;
import net.conselldemallorca.helium.v3.core.api.exception.TramitacioHandlerException;
import net.conselldemallorca.helium.v3.core.api.exception.TramitacioValidacioException;
import net.conselldemallorca.helium.v3.core.api.exception.ValidacioException;
import net.conselldemallorca.helium.v3.core.api.service.AdminService;
import net.conselldemallorca.helium.webapp.mvc.util.BaseController;
import net.conselldemallorca.helium.webapp.mvc.util.TascaFormUtil;
import net.conselldemallorca.helium.webapp.mvc.util.TramitacioMassiva;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.TascaFormHelper;



/**
 * Controlador per la gestió dels formularis de les tasques
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
public class TascaFormController extends BaseController {

	public static final String VARIABLE_SESSIO_CAMP_FOCUS = "helCampFocus";

	private String TAG_PARAM_REGEXP = "<!--helium:param-(.+?)-->";

	private TascaService tascaService;
	private DissenyService dissenyService;
	private ExpedientService expedientService;
	private Validator validatorGuardar;
	private Validator validatorValidar;
	private ExecucioMassivaService execucioMassivaService;
	private AdminService adminService;


	@Autowired
	public TascaFormController(
			TascaService tascaService,
			DissenyService dissenyService,
			ExpedientService expedientService, 
			TascaController tascaController,
			ExecucioMassivaService execucioMassivaService,
			AdminService adminService) {
		this.tascaService = tascaService;
		this.dissenyService = dissenyService;
		this.expedientService = expedientService;
		this.validatorGuardar = new TascaFormValidator(tascaService, false);
		this.validatorValidar = new TascaFormValidator(tascaService);
		this.execucioMassivaService = execucioMassivaService;
		this.adminService = adminService;
	}



	@ModelAttribute("seleccioMassiva")
	public List<TascaLlistatDto> populateSeleccioMassiva(
			HttpServletRequest request,
			@RequestParam(value = "id", required = false) String id) {
		if (id != null) {
			Entorn entorn = getEntornActiu(request);
			if (entorn != null) {
				String[] ids = TramitacioMassiva.getTasquesTramitacioMassiva(request, id);
				if (ids != null) {
					List<TascaLlistatDto> tasquesTramitacioMassiva = tascaService.findTasquesPerTramitacioMassiva(
							entorn.getId(),
							null,
							id);
					for (Iterator<TascaLlistatDto> it = tasquesTramitacioMassiva.iterator(); it.hasNext();) {
						TascaLlistatDto tasca = it.next();
						boolean trobada = false;
						for (String tascaId: ids) {
							if (tascaId.equals(tasca.getId())) {
								trobada = true;
								break;
							}
						}
						if (!trobada)
							it.remove();
					}
					return tasquesTramitacioMassiva;
				}
			}
		}
		return null;
	}

	@ModelAttribute("command")
	public Object populateCommand(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			Object command = null;
			Object commandSessio = TascaFormUtil.recuperarCommandTemporal(request, true);
			TascaDto tasca = null;
			try {
				tasca = tascaService.getById(
						entorn.getId(),
						id,
						null,
						null,
						true,
						true);
			} catch (net.conselldemallorca.helium.core.model.exception.IllegalStateException ex) {
				return null;
			}
			if (commandSessio != null) {
				List<Camp> camps = new ArrayList<Camp>();
	    		for (CampTasca campTasca: tasca.getCamps())
	    			camps.add(campTasca.getCamp());
				tasca = tascaService.getById(
						entorn.getId(),
						id,
						null,
						TascaFormUtil.getValorsFromCommand(
	        					camps,
	        					commandSessio,
	        					true,
	    						false),
	    				true,
	    				true);
				model.addAttribute(
						"valorsPerSuggest",
						TascaFormUtil.getValorsPerSuggest(tasca, commandSessio));
				model.addAttribute("commandReadOnly", commandSessio);
				command = commandSessio;
			} else {
				tasca = tascaService.getById(
						entorn.getId(),
						id,
						null,
						null,
						true,
						true);
				try {
					Map<String, Object> campsAddicionals = new HashMap<String, Object>();
					campsAddicionals.put("id", id);
					campsAddicionals.put("entornId", entorn.getId());
					campsAddicionals.put("procesScope", null);
					@SuppressWarnings("rawtypes")
					Map<String, Class> campsAddicionalsClasses = new HashMap<String, Class>();
					campsAddicionalsClasses.put("id", String.class);
					campsAddicionalsClasses.put("entornId", Long.class);
					campsAddicionalsClasses.put("procesScope", Map.class);
					command = TascaFormUtil.getCommandForTasca(
							tasca,
							campsAddicionals,
							campsAddicionalsClasses);
					model.addAttribute(
							"valorsPerSuggest",
							TascaFormUtil.getValorsPerSuggest(tasca, command));
					model.addAttribute("commandReadOnly", command);
				} catch (NotFoundException ignored) {}
			}
			if (tasca.getRecursForm() != null && tasca.getRecursForm().length() > 0) {
				try {
					byte[] contingut = dissenyService.getDeploymentResource(
							tasca.getDefinicioProces().getId(),
							tasca.getRecursForm());
					model.addAttribute(
							"formRecursParams",
							getFormRecursParams(new String(contingut, "UTF-8")));
				} catch (Exception ex) {
					logger.error("No s'han pogut extreure els parametres del recurs", ex);
				}
			}
			model.addAttribute("tasca", tasca);
			return command;
		}
		return null;
	}

	@RequestMapping(value = {"/tasca/form", "/tasca/formIframe"}, method = RequestMethod.GET)
	public String formGet(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			TascaDto tasca = null;
			if (adminService.mesuraTemporalIsActive()) {
				try {
					tasca = tascaService.getById(
							entorn.getId(),
							id,
							null,
							null,
							true,
							false);
					if (adminService.mesuraTemporalIsActive())
							adminService.mesuraTemporalIniciar(
									"Tasca FORM",
									"tasques",
									tasca.getExpedient().getTipus().getNom(),
									tasca.getNomLimitat(),
									null);
				} catch (net.conselldemallorca.helium.core.model.exception.IllegalStateException ex) {
					missatgeError(request, getMessage("error.tasca.no.disponible") );
					logger.error(getMessage("error.tascaService.noDisponible"), ex);
					return "redirect:/index.html";
				}
			}
			String campFocus = (String)request.getSession().getAttribute(VARIABLE_SESSIO_CAMP_FOCUS);
			if (campFocus != null) {
				String[] partsCampFocus = campFocus.split("#");
				if (partsCampFocus.length == 2) {
					if (!id.equals(partsCampFocus[1]))
						request.getSession().removeAttribute(VARIABLE_SESSIO_CAMP_FOCUS);
				}
			}
			if (adminService.mesuraTemporalIsActive())
				adminService.mesuraTemporalCalcular(
						"Tasca FORM",
						"tasques",
						tasca.getExpedient().getTipus().getNom(),
						tasca.getNomLimitat(),
						null);
			if (model.get("command") == null) {
				missatgeError(request, getMessage("error.tasca.no.disponible") );
				return "redirect:/tasca/personaLlistat.html";
			}
			return "tasca/form";
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}
	@RequestMapping(value = {"/tasca/form", "/tasca/formIframe"}, method = RequestMethod.POST)
	public String formPost(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
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
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			if (model.get("command") == null) {
				missatgeError(request, getMessage("error.tasca.no.disponible") );
				return "redirect:/tasca/personaLlistat.html";
			}
			TascaDto tasca = (TascaDto)model.get("tasca");
			List<Camp> camps = new ArrayList<Camp>();
    		for (CampTasca campTasca: tasca.getCamps())
    			camps.add(campTasca.getCamp());
    		if (campFocus != null) {
				String[] partsCampFocus = campFocus.split("#");
				if (partsCampFocus.length == 2) {
					request.getSession().setAttribute(VARIABLE_SESSIO_CAMP_FOCUS, partsCampFocus[0]);
				} else {
					request.getSession().setAttribute(VARIABLE_SESSIO_CAMP_FOCUS, campFocus);
				}
    		}
			if ("submit".equals(submit) || "@@@".equals(finalitzarAmbOutcome) || "submit".equals(submitar) || "validate".equals(submit) || "validate".equals(submitar)) {
				// Comprobamos si estaba validada
				if (tasca.isValidada()) {
					missatgeError(request, getMessage("error.tasca.validada") );
					return "tasca/form";
				}
				validatorGuardar.validate(command, result);
				if (result.hasErrors()) {
					return "tasca/form";
				}
				boolean ok = accioGuardarForm(
						request,
						entorn.getId(),
						id,
						camps,
						command);
				if (!ok)
					return "tasca/form";
				if (accioCamp != null && accioCamp.length() > 0) {
					ok = accioExecutarAccio(
							request,
							entorn.getId(),
							id,
							accioCamp);
					if (!ok)
						return "tasca/form";
				}
				if ("validate".equals(submit) || "validate".equals(submitar) || "@@@".equals(finalitzarAmbOutcome)) {
					validatorValidar.validate(command, result);
					try {
						afegirVariablesDelProces(command, tasca);
						TascaFormUtil.getBeanValidatorForCommand(camps).validate(command, result);
					} catch (Exception ex) {
						missatgeError(request, getMessage("error.validacio"), ex.getLocalizedMessage());
			        	logger.error("S'han produit errors de validació", ex);
			        	return "tasca/form";
					}
			        if (result.hasErrors()) {
			        	return "tasca/form";
			        }
			        ok = accioValidarForm(
			        		request,
							entorn.getId(),
							id,
							camps,
							command);
			        if (!ok)
						return "tasca/form";
				}
				status.setComplete();
				if (finalitzarAmbOutcome != null && !finalitzarAmbOutcome.equals("@#@")) {
					return "redirect:/tasca/completar.html?id=" + id + "&pipella=form&submit=" + finalitzarAmbOutcome;
				} else {
		        	if (iframe != null)
		        		return "redirect:/tasca/formIframe.html?id=" + id + "&iframe=iframe";
		        	else
		        		return "redirect:/tasca/form.html?id=" + id;
				}
			} else if ("restore".equals(submit) || "restore".equals(submitar)) {
				boolean ok = accioRestaurarForm(
		        		request,
						entorn.getId(),
						id,
						camps,
						command);
				if (ok) {
					status.setComplete();
					return "redirect:/tasca/form.html?id=" + id;
				} else {
					return "tasca/form";
				}
			} else if ("multipleAdd".equals(submit)) {
				try {
					if (field != null)
						PropertyUtils.setSimpleProperty(
								command,
								field,
								TascaFormUtil.addMultiple(field, command, camps));
				} catch (Exception ex) {
					missatgeError(request, getMessage("error.afegir.camp.multiple"), ex.getLocalizedMessage());
					logger.error("No s'ha pogut afegir el camp múltiple", ex);
				}
	        	return "tasca/form";
			} else if ("multipleRemove".equals(submit)) {
				try {
					if (field != null && index != null)
						PropertyUtils.setSimpleProperty(
								command,
								field,
								TascaFormUtil.deleteMultiple(field, command, camps, index));
				} catch (Exception ex) {
					missatgeError(request, getMessage("error.esborrar.camp.multiple"), ex.getLocalizedMessage());
					logger.error("No s'ha pogut esborrar el camp múltiple", ex);
				}
	        	return "tasca/form";
			} else {
				status.setComplete();
				boolean ok = false;
				if (registreEsborrarId != null && registreEsborrarIndex != null) {
//					Camp camp = dissenyService.getCampById(registreEsborrarId);
//					try {
//						tascaService.esborrarRegistre(
//								entorn.getId(),
//								id,
//								camp.getCodi(),
//								registreEsborrarIndex.intValue());
//					} catch (Exception ex) {
//			        	missatgeError(request, getMessage("error.esborrar.registre"), ex.getLocalizedMessage());
//			        	logger.error("No s'ha pogut esborrar el registre", ex);
//			        }
					ok = accioEsborrarRegistre(
			        		request,
							entorn.getId(),
							id,
							registreEsborrarId,
							registreEsborrarIndex);
				}
				TascaFormUtil.guardarCommandTemporal(request, command);
				if (ok) {
		        	if (iframe != null)
		        		return "redirect:/tasca/formIframe.html?id=" + id + "&iframe=iframe";
		        	else
		        		return "redirect:/tasca/form.html?id=" + id;
				} else {
					return "tasca/form";
				}
			}	
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
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
	private void afegirVariablesDelProces(Object command, TascaDto tasca) throws Exception {
		InstanciaProcesDto instanciaProces = expedientService.getInstanciaProcesById(
				tasca.getProcessInstanceId(),
				false, false, false);
		PropertyUtils.setSimpleProperty(
				command,
				"procesScope",
				instanciaProces.getVariables());
		
	}
	
	private boolean accioGuardarForm(
			HttpServletRequest request,
			Long entornId,
			String id,
			List<Camp> camps,
			Object command) {
		boolean resposta = true;
		boolean massivaActiu = TramitacioMassiva.isTramitacioMassivaActiu(request, id);
		String[] tascaIds;
		TascaDto task = tascaService.getByIdSenseComprovacio(id);
		
		if (massivaActiu) {
			String[] parametresTram = TramitacioMassiva.getParamsTramitacioMassiva(request, id);
			tascaIds = TramitacioMassiva.getTasquesTramitacioMassiva(request, id);
			try {
//				TascaDto task = tascaService.getByIdSenseComprovacio(id);
				Long expTipusId = task.getExpedient().getTipus().getId();
				Map<String, Object> variables = TascaFormUtil.getValorsFromCommand(
    					camps,
    					command,
    					true,
						false);
				if (logger.isDebugEnabled()) {
					logger.debug("Guardant dades de la tasca (id=" + id + ")");
					for (String var: variables.keySet()) {
						Object valor = variables.get(var);
						String valorComString = TascaFormHelper.varValorToString(valor);
						logger.debug("    Variable (" +
								"varCodi=" + var + ", " +
								"class=" + ((valor != null) ? valor.getClass().getName() : "null") + ", " +
								"valor=" + valorComString + ")");
					}
				}
				
				// Restauram la primera tasca
				// ------------------------------------------
				tascaService.guardarVariables(
	        			entornId,
	        			id,
	        			variables,
	    				null);
				
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
					
//					Authentication auth = SecurityContextHolder.getContext().getAuthentication();
					ExecucioMassivaDto dto = new ExecucioMassivaDto();
					dto.setDataInici(dInici);
					dto.setEnviarCorreu(bCorreu);
					dto.setTascaIds(tIds);
					dto.setExpedientTipusId(expTipusId);
					dto.setTipus(ExecucioMassivaTipus.EXECUTAR_TASCA);
					dto.setParam1("Guardar");
					Object[] params = new Object[2];
					params[0] = entornId;
					params[1] = variables;
//					params[2] = auth.getCredentials();
//					List<String> rols = new ArrayList<String>();
//					for (GrantedAuthority gauth : auth.getAuthorities()) {
//						rols.add(gauth.getAuthority());
//					}
//					params[3] = rols;
					dto.setParam2(execucioMassivaService.serialize(params));
					execucioMassivaService.crearExecucioMassiva(dto);
					
					missatgeInfo(request, getMessage("info.tasca.massiu.guardar", new Object[] {tIds.length}));
				}
			} catch (Exception e) {
				missatgeError(request, getMessage("error.no.massiu"));
				resposta = false;
			}
		} else {
			try {
				Map<String, Object> variables = TascaFormUtil.getValorsFromCommand(
    					camps,
    					command,
    					true,
						false);
				if (logger.isDebugEnabled()) {
					logger.debug("Guardant dades de la tasca (id=" + id + ")");
					for (String var: variables.keySet()) {
						Object valor = variables.get(var);
						String valorComString = TascaFormHelper.varValorToString(valor);
						logger.debug("    Variable (" +
								"varCodi=" + var + ", " +
								"class=" + ((valor != null) ? valor.getClass().getName() : "null") + ", " +
								"valor=" + valorComString + ")");
					}
				}
	        	tascaService.guardarVariables(
	        			entornId,
	        			id,
	        			variables,
	    				null);
	        	missatgeInfo(request, getMessage("info.dades.form.guardat"));
	        } catch (Exception ex) {
	        	String tascaIdLog = getIdTascaPerLogs(entornId, id);
				missatgeError(
		    			request,
		    			getMessage("error.proces.peticio") + " " + tascaIdLog,
		    			ex.getLocalizedMessage());
	        	logger.error("No s'ha pogut guardar les dades del formulari en la tasca " + tascaIdLog, ex);
	        	resposta = false;
	        }
		}
		return resposta;
	}
	private boolean accioValidarForm(
			HttpServletRequest request,
			Long entornId,
			String id,
			List<Camp> camps,
			Object command) {
		boolean resposta = true;
		TascaDto task = tascaService.getByIdSenseComprovacio(id);
		if (TramitacioMassiva.isTramitacioMassivaActiu(request, id)) {
			String[] tascaIds = TramitacioMassiva.getTasquesTramitacioMassiva(request, id);
			String[] parametresTram = TramitacioMassiva.getParamsTramitacioMassiva(request, id);
			try {
				// TascaDto task = tascaService.getByIdSenseComprovacio(id);
				Long expTipusId = task.getExpedient().getTipus().getId();
				Map<String, Object> variables = TascaFormUtil.getValorsFromCommand(
    					camps,
    					command,
    					true,
						false);
				// Restauram la primera tasca
				// ------------------------------------------
				tascaService.validar(
	        			entornId,
	        			id,
	        			variables,
	    				true);
				
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
					
//					Authentication auth = SecurityContextHolder.getContext().getAuthentication();
					ExecucioMassivaDto dto = new ExecucioMassivaDto();
					dto.setDataInici(dInici);
					dto.setEnviarCorreu(bCorreu);
					dto.setTascaIds(tIds);
					dto.setExpedientTipusId(expTipusId);
					dto.setTipus(ExecucioMassivaTipus.EXECUTAR_TASCA);
					dto.setParam1("Validar");
					Object[] params = new Object[2];
					params[0] = entornId;
					params[1] = variables;
//					params[2] = auth.getCredentials();
//					List<String> rols = new ArrayList<String>();
//					for (GrantedAuthority gauth : auth.getAuthorities()) {
//						rols.add(gauth.getAuthority());
//					}
//					params[3] = rols;
					dto.setParam2(execucioMassivaService.serialize(params));
					execucioMassivaService.crearExecucioMassiva(dto);
					
					missatgeInfo(request, getMessage("info.tasca.massiu.validar", new Object[] {tIds.length}));
				}
			} catch (Exception e) {
				missatgeError(request, getMessage("error.no.massiu"));
				resposta = false;
			}
		} else {
			try {
	        	tascaService.validar(
	        			entornId,
	        			id,
	        			TascaFormUtil.getValorsFromCommand(
	        					camps,
	        					command,
	        					true,
	    						false),
	    				true);
	        	missatgeInfo(request, getMessage("info.formulari.validat"));
	        } catch (Exception ex) {
	        	String tascaIdLog = getIdTascaPerLogs(entornId, id);
				missatgeError(
		    			request,
		    			getMessage("error.validar.formulari") + " " + tascaIdLog,
		    			ex.getLocalizedMessage());
				logger.error("No s'ha pogut validar el formulari en la tasca " + tascaIdLog, ex);
				resposta = false;
	        }
		}
		return resposta;
	}

	private boolean accioRestaurarForm(
			HttpServletRequest request,
			Long entornId,
			String id,
			List<Camp> camps,
			Object command) {
		boolean resposta = true;
		boolean massivaActiu = TramitacioMassiva.isTramitacioMassivaActiu(request, id);
		String[] tascaIds;
		TascaDto task = tascaService.getByIdSenseComprovacio(id);
		if (massivaActiu) {
			String[] parametresTram = TramitacioMassiva.getParamsTramitacioMassiva(request, id);
			tascaIds = TramitacioMassiva.getTasquesTramitacioMassiva(request, id);
			try {
//				TascaDto task = tascaService.getByIdSenseComprovacio(id);
				Long expTipusId = task.getExpedient().getTipus().getId();
				
				// Restauram la primera tasca
				// ------------------------------------------
				tascaService.restaurar(
	        			entornId,
	        			id);
				
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
					
//					Authentication auth = SecurityContextHolder.getContext().getAuthentication();
					ExecucioMassivaDto dto = new ExecucioMassivaDto();
					dto.setDataInici(dInici);
					dto.setEnviarCorreu(bCorreu);
					dto.setTascaIds(tIds);
					dto.setExpedientTipusId(expTipusId);
					dto.setTipus(ExecucioMassivaTipus.EXECUTAR_TASCA);
					dto.setParam1("Restaurar");
					Object[] params = new Object[1];
					params[0] = entornId;
//					params[1] = auth.getCredentials();
//					List<String> rols = new ArrayList<String>();
//					for (GrantedAuthority gauth : auth.getAuthorities()) {
//						rols.add(gauth.getAuthority());
//					}
//					params[2] = rols;
					dto.setParam2(execucioMassivaService.serialize(params));
					dto.setParam2(execucioMassivaService.serialize(params));
					execucioMassivaService.crearExecucioMassiva(dto);
					
					missatgeInfo(request, getMessage("info.tasca.massiu.restaurar", new Object[] {tIds.length}));
				}
			} catch (Exception e) {
				missatgeError(request, getMessage("error.no.massiu"));
				resposta = false;
			}
		} else {
			try {
	        	tascaService.restaurar(
	        			entornId,
	        			id);
	        	missatgeInfo(request, getMessage("info.formulari.restaurat"));
	        } catch (Exception ex) {
	        	String tascaIdLog = getIdTascaPerLogs(entornId, id);
				missatgeError(
		    			request,
		    			getMessage("error.restaurar.formulari") + " " + tascaIdLog,
		    			ex.getLocalizedMessage());
	        	logger.error("No s'ha pogut restaurar el formulari en la tasca " + tascaIdLog, ex);
	        	resposta = false;
	        }
		}
		return resposta;
	}

	private boolean accioExecutarAccio(
			HttpServletRequest request,
			Long entornId,
			String id,
			String accio) {
		boolean resposta = true;
		boolean massivaActiu = TramitacioMassiva.isTramitacioMassivaActiu(request, id);
		String[] tascaIds;
		TascaDto task = tascaService.getByIdSenseComprovacio(id);
		if (massivaActiu) {
			String[] parametresTram = TramitacioMassiva.getParamsTramitacioMassiva(request, id);
			tascaIds = TramitacioMassiva.getTasquesTramitacioMassiva(request, id);
			try {
//				TascaDto task = tascaService.getByIdSenseComprovacio(id);
				Long expTipusId = task.getExpedient().getTipus().getId();
				
				// Restauram la primera tasca
				// ------------------------------------------
				tascaService.executarAccio(
						entornId,
						id,
						accio);
				
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
					
//					Authentication auth = SecurityContextHolder.getContext().getAuthentication();
					ExecucioMassivaDto dto = new ExecucioMassivaDto();
					dto.setDataInici(dInici);
					dto.setEnviarCorreu(bCorreu);
					dto.setTascaIds(tIds);
					dto.setExpedientTipusId(expTipusId);
					dto.setTipus(ExecucioMassivaTipus.EXECUTAR_TASCA);
					dto.setParam1("Accio");
					Object[] params = new Object[2];
					params[0] = entornId;
					params[1] = accio;
//					params[2] = auth.getCredentials();
//					List<String> rols = new ArrayList<String>();
//					for (GrantedAuthority gauth : auth.getAuthorities()) {
//						rols.add(gauth.getAuthority());
//					}
//					params[3] = rols;
					dto.setParam2(execucioMassivaService.serialize(params));
					execucioMassivaService.crearExecucioMassiva(dto);
					
					missatgeInfo(request, getMessage("info.tasca.massiu.accio", new Object[] {tIds.length}));
				}
			} catch (Exception e) {
				missatgeError(request, getMessage("error.no.massiu"));
				resposta = false;
			}
		} else {
			try {
				tascaService.executarAccio(
						entornId,
						id,
						accio);
				missatgeInfo(request, getMessage("info.accio.executat"));
	        } catch (Exception ex) {
	        	String tascaIdLog = getIdTascaPerLogs(entornId, id);
	        	
	        	if (ex instanceof ValidacioException) {
					MissatgesHelper.error(
		        			request,
		        			getMessage("error.validacio.tasca") + " " + tascaIdLog + ": " + ex.getMessage(),
		        			ex);
				}  else if (ex instanceof TramitacioValidacioException) {
					MissatgesHelper.error(
		        			request,
		        			getMessage("error.validacio.tasca") + " " + tascaIdLog + ": " + ex.getMessage(),
		        			ex);
				} else if (ex instanceof TramitacioHandlerException) {
					MissatgesHelper.error(
		        			request,
		        			getMessage("error.executar.accio") + " " + tascaIdLog + ": " + ((TramitacioHandlerException)ex).getPublicMessage(),
		        			ex);
				} else if (ex instanceof TramitacioException) {
					MissatgesHelper.error(
		        			request,
		        			getMessage("error.executar.accio") + " " + tascaIdLog + ": " + ((TramitacioException)ex).getPublicMessage(),
		        			ex);
				} else {
					MissatgesHelper.error(
		        			request,
		        			getMessage("error.executar.accio") + " " + tascaIdLog + ": " + 
		        					(ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage()),
		        			ex);
		        }
	        	logger.error("No s'ha pogut executar l'acció '" + accio + "' en la tasca " + tascaIdLog, ex);
	        	
	        	resposta = false;
	        }
		}
		return resposta;
	}
	
	private boolean accioEsborrarRegistre(
			HttpServletRequest request,
			Long entornId,
			String id,
			Long registreEsborrarId,
			Integer registreEsborrarIndex) {
		boolean massivaActiu = TramitacioMassiva.isTramitacioMassivaActiu(request, id);
		String[] tascaIds;
		Camp camp = dissenyService.getCampById(registreEsborrarId);
		if (massivaActiu) {
			String[] parametresTram = TramitacioMassiva.getParamsTramitacioMassiva(request, id);
			tascaIds = TramitacioMassiva.getTasquesTramitacioMassiva(request, id);
			try {
				TascaDto task = tascaService.getByIdSenseComprovacio(id);
				Long expTipusId = task.getExpedient().getTipus().getId();
				
				// Restauram la primera tasca
				// ------------------------------------------
				tascaService.esborrarRegistre(
						entornId,
						id,
						camp.getCodi(),
						registreEsborrarIndex.intValue());
				
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
					
//					Authentication auth = SecurityContextHolder.getContext().getAuthentication();
					ExecucioMassivaDto dto = new ExecucioMassivaDto();
					dto.setDataInici(dInici);
					dto.setEnviarCorreu(bCorreu);
					dto.setTascaIds(tIds);
					dto.setExpedientTipusId(expTipusId);
					dto.setTipus(ExecucioMassivaTipus.EXECUTAR_TASCA);
					dto.setParam1("RegEsborrar");
					Object[] params = new Object[3];
					params[0] = entornId;
					params[1] = camp.getCodi();
					params[2] = registreEsborrarIndex;
//					params[3] = auth.getCredentials();
//					List<String> rols = new ArrayList<String>();
//					for (GrantedAuthority gauth : auth.getAuthorities()) {
//						rols.add(gauth.getAuthority());
//					}
//					params[4] = rols;
					dto.setParam2(execucioMassivaService.serialize(params));
					execucioMassivaService.crearExecucioMassiva(dto);
					
					missatgeInfo(request, getMessage("info.tasca.massiu.registre.esborrar", new Object[] {tIds.length}));
				}
			} catch (Exception e) {
				missatgeError(request, getMessage("error.no.massiu"));
				return false;
			}
		} else {
			try {
				tascaService.esborrarRegistre(
						entornId,
						id,
						camp.getCodi(),
						registreEsborrarIndex.intValue());
			} catch (Exception ex) {
	        	missatgeError(request, getMessage("error.esborrar.registre"), ex.getLocalizedMessage());
	        	logger.error("No s'ha pogut esborrar el registre", ex);
	        }
		}
		return true;
	}

	private String getIdTascaPerLogs(Long entornId, String tascaId) {
		TascaDto tascaActual = tascaService.getById(
				entornId,
				tascaId,
				null,
				null,
				false,
				false);
		return tascaActual.getNom() + " - " + tascaActual.getExpedient().getIdentificador();
	}

	private static final Log logger = LogFactory.getLog(TascaFormController.class);

}
