/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import java.math.BigDecimal;
import java.text.DecimalFormat;
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

import net.conselldemallorca.helium.core.model.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.core.model.dto.TascaDto;
import net.conselldemallorca.helium.core.model.dto.TascaLlistatDto;
import net.conselldemallorca.helium.core.model.exception.NotFoundException;
import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.CampTasca;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.service.DissenyService;
import net.conselldemallorca.helium.core.model.service.ExpedientService;
import net.conselldemallorca.helium.core.model.service.TascaService;
import net.conselldemallorca.helium.jbpm3.integracio.Termini;
import net.conselldemallorca.helium.webapp.mvc.util.BaseController;
import net.conselldemallorca.helium.webapp.mvc.util.TascaFormUtil;
import net.conselldemallorca.helium.webapp.mvc.util.TramitacioMassiva;

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



	@Autowired
	public TascaFormController(
			TascaService tascaService,
			DissenyService dissenyService,
			ExpedientService expedientService) {
		this.tascaService = tascaService;
		this.dissenyService = dissenyService;
		this.expedientService = expedientService;
		this.validatorGuardar = new TascaFormValidator(tascaService, false);
		this.validatorValidar = new TascaFormValidator(tascaService);
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
			TascaDto tasca = tascaService.getById(entorn.getId(), id);
			Object command = null;
			Object commandSessio = TascaFormUtil.recuperarCommandTemporal(request, true);
			if (commandSessio != null) {
				List<Camp> camps = new ArrayList<Camp>();
	    		for (CampTasca campTasca: tasca.getCamps())
	    			camps.add(campTasca.getCamp());
				tasca = tascaService.getById(
						entorn.getId(),
						id,
						TascaFormUtil.getValorsFromCommand(
	        					camps,
	        					commandSessio,
	        					true,
	    						false));
				model.addAttribute(
						"valorsPerSuggest",
						TascaFormUtil.getValorsPerSuggest(tasca, commandSessio));
				model.addAttribute("commandReadOnly", commandSessio);
				command = commandSessio;
			} else {
				tasca = tascaService.getById(entorn.getId(), id);
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
			String campFocus = (String)request.getSession().getAttribute(VARIABLE_SESSIO_CAMP_FOCUS);
			if (campFocus != null) {
				String[] partsCampFocus = campFocus.split("#");
				if (partsCampFocus.length == 2) {
					if (!id.equals(partsCampFocus[1]))
						request.getSession().removeAttribute(VARIABLE_SESSIO_CAMP_FOCUS);
				}
			}
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
			@RequestParam(value = "helMultipleIndex", required = false) Integer index,
			@RequestParam(value = "helMultipleField", required = false) String field,
			@RequestParam(value = "iframe", required = false) String iframe,
			@RequestParam(value = "registreEsborrarId", required = false) Long registreEsborrarId,
			@RequestParam(value = "registreEsborrarIndex", required = false) Integer registreEsborrarIndex,
			@RequestParam(value = "helAccioCamp", required = false) String accioCamp,
			@RequestParam(value = "helCampFocus", required = false) String campFocus,
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
			if ("submit".equals(submit)) {
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
				/*try {
					tascaService.guardarVariables(
							entorn.getId(),
							id,
							TascaFormUtil.getValorsFromCommand(
									camps,
									command,
									true,
									false));
					missatgeInfo(request, getMessage("info.dades.form.guardat") );
				} catch (Exception ex) {
					missatgeError(request, getMessage("error.proces.peticio"), ex.getLocalizedMessage());
					logger.error("No s'ha pogut guardar les dades del formulari", ex);
					return "tasca/form";
				}*/
				if (accioCamp != null && accioCamp.length() > 0) {
					ok = accioExecutarAccio(
							request,
							entorn.getId(),
							id,
							accioCamp);
					if (!ok)
						return "tasca/form";
				}
		        /*try {
					if (accioCamp != null && accioCamp.length() > 0) {
						tascaService.executarAccio(
								entorn.getId(),
								id,
								accioCamp);
						missatgeInfo(request, getMessage("info.accio.executat") );
					}
				} catch (Exception ex) {
					missatgeError(request, getMessage("error.executar.accio"), ex.getLocalizedMessage());
					logger.error("No s'ha pogut executar l'acció: ", ex);
					return "tasca/form";
				}*/
		        status.setComplete();
	        	if (iframe != null)
	        		return "redirect:/tasca/formIframe.html?id=" + id + "&iframe=iframe";
	        	else
	        		return "redirect:/tasca/form.html?id=" + id;
			} else if ("validate".equals(submit)) {
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
		        boolean ok = accioValidarForm(
		        		request,
						entorn.getId(),
						id,
						camps,
						command);
				if (!ok)
					return "tasca/form";
		        /*try {
		        	tascaService.validar(
		        			entorn.getId(),
		        			id,
		        			TascaFormUtil.getValorsFromCommand(
		        					camps,
		        					command,
		        					true,
		    						false),
		    				true);
		        	missatgeInfo(request, getMessage("info.formulari.validat"));
		        } catch (Exception ex) {
		        	missatgeError(request, getMessage("error.validar.formulari"), ex.getLocalizedMessage());
		        	logger.error("No s'ha pogut validar el formulari", ex);
		        	return "tasca/form";
		        }*/
		        status.setComplete();
	        	if (iframe != null)
	        		return "redirect:/tasca/formIframe.html?id=" + id + "&iframe=iframe&toParent=toParent";
	        	else
	        		return "redirect:/tasca/form.html?id=" + id;
			} else if ("restore".equals(submit)) {
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
		        /*try {
		        	tascaService.restaurar(
		        			entorn.getId(),
		        			id);
		        	missatgeInfo(request, getMessage("info.formulari.restaurat") );
		        	status.setComplete();
		        	return "redirect:/tasca/form.html?id=" + id;
		        } catch (Exception ex) {
		        	missatgeError(request, getMessage("error.restaurar.formulari"), ex.getLocalizedMessage());
		        	logger.error("No s'ha pogut restaurar el formulari", ex);
		        	return "tasca/form";
		        }*/
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
				if (registreEsborrarId != null && registreEsborrarIndex != null) {
					Camp camp = dissenyService.getCampById(registreEsborrarId);
					try {
						tascaService.esborrarRegistre(id, camp.getCodi(), registreEsborrarIndex.intValue());
					} catch (Exception ex) {
			        	missatgeError(request, getMessage("error.esborrar.registre"), ex.getLocalizedMessage());
			        	logger.error("No s'ha pogut esborrar el registre", ex);
			        }
				}
				TascaFormUtil.guardarCommandTemporal(request, command);
	        	if (iframe != null)
	        		return "redirect:/tasca/formIframe.html?id=" + id + "&iframe=iframe";
	        	else
	        		return "redirect:/tasca/form.html?id=" + id;
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
						new DecimalFormat("#,###.00"),
						true));
		binder.registerCustomEditor(
				Boolean.class,
				new CustomBooleanEditor(false));
		binder.registerCustomEditor(
				Date.class,
				new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true));
		binder.registerCustomEditor(
				Termini.class,
				new TerminiTypeEditor());
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
				true);
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
		boolean massivaActiu = TramitacioMassiva.isTramitacioMassivaActiu(request, id);
		String[] tascaIds;
		if (massivaActiu)
			tascaIds = TramitacioMassiva.getTasquesTramitacioMassiva(request, id);
		else
			tascaIds = new String[]{id};
		boolean error = false;
		for (String tascaId: tascaIds) {
			try {
	        	tascaService.guardarVariables(
	        			entornId,
	        			tascaId,
	        			TascaFormUtil.getValorsFromCommand(
	        					camps,
	        					command,
	        					true,
	    						false));
	        } catch (Exception ex) {
	        	String tascaIdLog = getIdTascaPerLogs(entornId, tascaId);
				missatgeError(
		    			request,
		    			getMessage("error.proces.peticio") + " " + tascaIdLog,
		    			ex.getLocalizedMessage());
	        	logger.error("No s'ha pogut guardar les dades del formulari en la tasca " + tascaIdLog, ex);
	        	error = true;
	        }
		}
		if (!error) {
			if (massivaActiu)
				missatgeInfo(request, getMessage("info.dades.form.guardades"));
			else
				missatgeInfo(request, getMessage("info.dades.form.guardat"));
		}
		return !error;
	}
	private boolean accioValidarForm(
			HttpServletRequest request,
			Long entornId,
			String id,
			List<Camp> camps,
			Object command) {
		boolean massivaActiu = TramitacioMassiva.isTramitacioMassivaActiu(request, id);
		String[] tascaIds;
		if (massivaActiu)
			tascaIds = TramitacioMassiva.getTasquesTramitacioMassiva(request, id);
		else
			tascaIds = new String[]{id};
		boolean error = false;
		for (String tascaId: tascaIds) {
			try {
	        	tascaService.validar(
	        			entornId,
	        			tascaId,
	        			TascaFormUtil.getValorsFromCommand(
	        					camps,
	        					command,
	        					true,
	    						false),
	    				true);
	        	
	        } catch (Exception ex) {
	        	String tascaIdLog = getIdTascaPerLogs(entornId, tascaId);
				missatgeError(
		    			request,
		    			getMessage("error.validar.formulari") + " " + tascaIdLog,
		    			ex.getLocalizedMessage());
				logger.error("No s'ha pogut validar el formulari en la tasca " + tascaIdLog, ex);
	        	error = true;
	        }
		}
		if (!error) {
			if (massivaActiu)
				missatgeInfo(request, getMessage("info.formulari.validats"));
			else
				missatgeInfo(request, getMessage("info.formulari.validat"));
		}
		return !error;
	}
	private boolean accioRestaurarForm(
			HttpServletRequest request,
			Long entornId,
			String id,
			List<Camp> camps,
			Object command) {
		boolean massivaActiu = TramitacioMassiva.isTramitacioMassivaActiu(request, id);
		String[] tascaIds;
		if (massivaActiu)
			tascaIds = TramitacioMassiva.getTasquesTramitacioMassiva(request, id);
		else
			tascaIds = new String[]{id};
		boolean error = false;
		for (String tascaId: tascaIds) {
			try {
	        	tascaService.restaurar(
	        			entornId,
	        			tascaId);
	        } catch (Exception ex) {
	        	String tascaIdLog = getIdTascaPerLogs(entornId, tascaId);
				missatgeError(
		    			request,
		    			getMessage("error.restaurar.formulari") + " " + tascaIdLog,
		    			ex.getLocalizedMessage());
	        	logger.error("No s'ha pogut restaurar el formulari en la tasca " + tascaIdLog, ex);
	        	error = true;
	        }
		}
		if (!error) {
			if (massivaActiu)
				missatgeInfo(request, getMessage("info.formulari.restaurats"));
			else
				missatgeInfo(request, getMessage("info.formulari.restaurat"));
		}
		return !error;
	}
	private boolean accioExecutarAccio(
			HttpServletRequest request,
			Long entornId,
			String id,
			String accio) {
		boolean massivaActiu = TramitacioMassiva.isTramitacioMassivaActiu(request, id);
		String[] tascaIds;
		if (massivaActiu)
			tascaIds = TramitacioMassiva.getTasquesTramitacioMassiva(request, id);
		else
			tascaIds = new String[]{id};
		boolean error = false;
		for (String tascaId: tascaIds) {
			try {
				tascaService.executarAccio(
						entornId,
						tascaId,
						accio);
	        } catch (Exception ex) {
	        	String tascaIdLog = getIdTascaPerLogs(entornId, tascaId);
				missatgeError(
		    			request,
		    			getMessage("error.executar.accio") + " " + tascaIdLog,
		    			ex.getLocalizedMessage());
	        	logger.error("No s'ha pogut executar l'acció '" + accio + "' en la tasca " + tascaIdLog, ex);
	        	error = true;
	        }
		}
		if (!error) {
			if (massivaActiu)
				missatgeInfo(request, getMessage("info.accio.executades"));
			else
				missatgeInfo(request, getMessage("info.accio.executat"));
		}
		return !error;
	}

	private String getIdTascaPerLogs(Long entornId, String tascaId) {
		TascaDto tascaActual = tascaService.getById(entornId, tascaId);
		return tascaActual.getNom() + " - " + tascaActual.getExpedient().getIdentificador();
	}



	private static final Log logger = LogFactory.getLog(TascaFormController.class);

}
