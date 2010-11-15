/**
 * 
 */
package net.conselldemallorca.helium.presentacio.mvc;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.jbpm3.integracio.Termini;
import net.conselldemallorca.helium.model.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.model.dto.TascaDto;
import net.conselldemallorca.helium.model.exception.NotFoundException;
import net.conselldemallorca.helium.model.hibernate.Camp;
import net.conselldemallorca.helium.model.hibernate.CampTasca;
import net.conselldemallorca.helium.model.hibernate.Entorn;
import net.conselldemallorca.helium.model.service.DissenyService;
import net.conselldemallorca.helium.model.service.ExpedientService;
import net.conselldemallorca.helium.model.service.TascaService;
import net.conselldemallorca.helium.presentacio.mvc.util.BaseController;
import net.conselldemallorca.helium.presentacio.mvc.util.TascaFormUtil;

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
 * @author Josep Gayà <josepg@limit.es>
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

	@SuppressWarnings("unchecked")
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
				missatgeError(request, "Aquesta tasca ja no està disponible");
				return "redirect:/tasca/personaLlistat.html";
			}
			return "tasca/form";
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
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
				missatgeError(request, "Aquesta tasca ja no està disponible");
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
		        try {
		        	tascaService.guardarVariables(
		        			entorn.getId(),
		        			id,
		        			TascaFormUtil.getValorsFromCommand(
		        					camps,
		        					command,
		        					true,
		    						false));
		        	missatgeInfo(request, "Les dades del formulari s'han guardat correctament");
		        } catch (Exception ex) {
		        	missatgeError(request, "S'ha produït un error processant la seva petició", ex.getLocalizedMessage());
		        	logger.error("No s'ha pogut guardar les dades del formulari", ex);
		        	return "tasca/form";
		        }
		        try {
					if (accioCamp != null && accioCamp.length() > 0) {
						tascaService.executarAccio(
								entorn.getId(),
								id,
								accioCamp);
						missatgeInfo(request, "L'acció s'ha executat amb èxit");
					}
				} catch (Exception ex) {
					missatgeError(request, "No s'ha pogut executar l'acció", ex.getLocalizedMessage());
					logger.error("No s'ha pogut executar l'acció: ", ex);
					return "tasca/form";
				}
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
					missatgeError(request, "S'han produit errors de validació", ex.getLocalizedMessage());
		        	logger.error("S'han produit errors de validació", ex);
		        	return "tasca/form";
				}
		        if (result.hasErrors()) {
		        	return "tasca/form";
		        }
		        try {
		        	tascaService.validar(
		        			entorn.getId(),
		        			id,
		        			TascaFormUtil.getValorsFromCommand(
		        					camps,
		        					command,
		        					true,
		    						false),
		    				true);
		        	missatgeInfo(request, "El formulari s'ha validat correctament");
		        	status.setComplete();
		        	if (iframe != null)
		        		return "redirect:/tasca/formIframe.html?id=" + id + "&iframe=iframe&toParent=toParent";
		        	else
		        		return "redirect:/tasca/form.html?id=" + id;
		        } catch (Exception ex) {
		        	missatgeError(request, "No s'ha pogut validar el formulari", ex.getLocalizedMessage());
		        	logger.error("No s'ha pogut validar el formulari", ex);
		        	return "tasca/form";
		        }
			} else if ("restore".equals(submit)) {
		        /*if (result.hasErrors()) {
		        	return "tasca/form";
		        }*/
		        try {
		        	tascaService.restaurar(
		        			entorn.getId(),
		        			id);
		        	missatgeInfo(request, "El formulari s'ha restaurat correctament");
		        	status.setComplete();
		        	return "redirect:/tasca/form.html?id=" + id;
		        } catch (Exception ex) {
		        	missatgeError(request, "No s'ha pogut restaurar el formulari", ex.getLocalizedMessage());
		        	logger.error("No s'ha pogut restaurar el formulari", ex);
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
					missatgeError(request, "No s'ha pogut afegir el camp múltiple", ex.getLocalizedMessage());
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
					missatgeError(request, "No s'ha pogut esborrar el camp múltiple", ex.getLocalizedMessage());
					logger.error("No s'ha pogut esborrar el camp múltiple", ex);
				}
	        	return "tasca/form";
			/*} else if ("accioCamp".equals(submit)) {
		        try {
					if (accioCamp != null && accioCamp != null) {
						tascaService.executarAccio(
								entorn.getId(),
								id,
								accioCamp);
						missatgeInfo(request, "L'acció s'ha executat amb èxit");
					}
				} catch (Exception ex) {
					missatgeError(request, "No s'ha pogut executar l'acció", ex.getLocalizedMessage());
					logger.error("No s'ha pogut executar l'acció: ", ex);
					return "tasca/form";
				}
		        status.setComplete();
	        	return "tasca/form";*/
			} else {
				status.setComplete();
				if (registreEsborrarId != null && registreEsborrarIndex != null) {
					Camp camp = dissenyService.getCampById(registreEsborrarId);
					try {
						tascaService.esborrarRegistre(id, camp.getCodi(), registreEsborrarIndex.intValue());
					} catch (Exception ex) {
			        	missatgeError(request, "No s'ha pogut esborrar el registre", ex.getLocalizedMessage());
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
			missatgeError(request, "No hi ha cap entorn seleccionat");
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

	private static final Log logger = LogFactory.getLog(TascaFormController.class);

}
