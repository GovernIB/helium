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
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.core.model.dto.ExpedientDto;
import net.conselldemallorca.helium.core.model.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.core.model.dto.TascaDto;
import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.Camp.TipusCamp;
import net.conselldemallorca.helium.core.model.hibernate.CampTasca;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.Tasca.TipusTasca;
import net.conselldemallorca.helium.jbpm3.integracio.Termini;
import net.conselldemallorca.helium.core.model.service.DissenyService;
import net.conselldemallorca.helium.core.model.service.ExpedientService;
import net.conselldemallorca.helium.core.model.service.PermissionService;
import net.conselldemallorca.helium.core.model.service.TascaService;
import net.conselldemallorca.helium.core.security.permission.ExtendedPermission;
import net.conselldemallorca.helium.webapp.mvc.util.BaseController;
import net.conselldemallorca.helium.webapp.mvc.util.TascaFormUtil;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.security.acls.Permission;
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
 * Controlador per a modificar les dades dels expedients
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
public class ExpedientDadaModificarController extends BaseController {

	private DissenyService dissenyService;
	private ExpedientService expedientService;
	private TascaService tascaService;
	private PermissionService permissionService;

	private Validator validator;



	@Autowired
	public ExpedientDadaModificarController(
			DissenyService dissenyService,
			ExpedientService expedientService,
			TascaService tascaService,
			PermissionService permissionService) {
		this.dissenyService = dissenyService;
		this.expedientService = expedientService;
		this.tascaService = tascaService;
		this.permissionService = permissionService;
		this.validator = new TascaFormValidator(expedientService);
	}

	@SuppressWarnings("rawtypes")
	@ModelAttribute("command")
	public Object populateCommand(
			HttpServletRequest request,
			@RequestParam(value = "id", required = false) String id,
			@RequestParam(value = "taskId", required = false) String taskId,
			@RequestParam(value = "var", required = true) String var) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			Map<String, Object> campsAddicionals = new HashMap<String, Object>();
			Map<String, Class> campsAddicionalsClasses = new HashMap<String, Class>();
			if (id != null) {
				campsAddicionals.put("id", id);
				campsAddicionalsClasses.put("id", String.class);
			}
			if (taskId != null) {
				campsAddicionals.put("taskId", taskId);
				campsAddicionalsClasses.put("taskId", String.class);
			}
			campsAddicionals.put("var", var);
			campsAddicionalsClasses.put("var", String.class);
			Object command = TascaFormUtil.getCommandForTasca(
					createTascaAmbVar(entorn.getId(), id, taskId, var),
					campsAddicionals,
					campsAddicionalsClasses);
			return command;
		}
		return null;
	}

	@RequestMapping(value = "/expedient/dadaModificar", method = RequestMethod.GET)
	public String dadaModificar(
			HttpServletRequest request,
			@RequestParam(value = "id", required = false) String id,
			@RequestParam(value = "taskId", required = false) String taskId,
			@RequestParam(value = "var", required = true) String var,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientDto expedient = getExpedient(entorn.getId(), id, taskId);
			if (potModificarExpedient(expedient)) {
				model.addAttribute("expedient", expedient);
				TascaDto tasca = createTascaAmbVar(entorn.getId(), id, taskId, var);
				model.addAttribute("tasca", tasca);
				Object command = model.get("command");
				model.addAttribute("valorsPerSuggest", TascaFormUtil.getValorsPerSuggest(tasca, command));
				return "expedient/dadaForm";
			} else {
				missatgeError(request, getMessage("error.permisos.modificar.expedient"));
			}
			return "redirect:/expedient/consulta.html";
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}
	@RequestMapping(value = "/expedient/dadaModificar", method = RequestMethod.POST)
	public String dadaModificar(
			HttpServletRequest request,
			@RequestParam(value = "id", required = false) String id,
			@RequestParam(value = "taskId", required = false) String taskId,
			@RequestParam(value = "var", required = true) String var,
			@RequestParam(value = "submit", required = false) String submit,
			@RequestParam(value = "helMultipleIndex", required = false) Integer index,
			@RequestParam(value = "helMultipleField", required = false) String field,
			@ModelAttribute("command") Object command,
			BindingResult result,
			SessionStatus status,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientDto expedient = getExpedient(entorn.getId(), id, taskId);
			if (potModificarExpedient(expedient)) {
				TascaDto tasca = createTascaAmbVar(entorn.getId(), id, taskId, var);
				List<Camp> camps = new ArrayList<Camp>();
	    		for (CampTasca campTasca: tasca.getCamps())
	    			camps.add(campTasca.getCamp());
				if ("submit".equals(submit) || submit.length() == 0) {
					((TascaFormValidator)validator).setTasca(tasca);
					validator.validate(command, result);
					if (result.hasErrors()) {
						model.addAttribute("expedient", expedient);
						model.addAttribute("tasca", tasca);
			        	model.addAttribute("valorsPerSuggest", TascaFormUtil.getValorsPerSuggest(tasca, command));
			        	return "expedient/dadaForm";
			        }
					try {
						if (id != null) {
					        expedientService.updateVariable(
					        		id,
					        		var,
					        		PropertyUtils.getSimpleProperty(command, var));
						} else {
							tascaService.updateVariable(
									entorn.getId(),
									taskId,
									var,
									PropertyUtils.getSimpleProperty(command, var));
						}
				        missatgeInfo(request, getMessage("info.dada.modificat") );
			        } catch (Exception ex) {
						missatgeError(request, getMessage("error.proces.peticio"), ex.getLocalizedMessage());
			        	logger.error("No s'ha pogut modificat la dada", ex);
			        	return "expedient/dadaForm";
					}
				} else if ("multipleAdd".equals(submit)) {
					try {
						if (field != null)
							PropertyUtils.setSimpleProperty(
									command,
									field,
									TascaFormUtil.addMultiple(field, command, camps));
					} catch (Exception ex) {
						missatgeError(request, getMessage("error.proces.peticio"), ex.getLocalizedMessage());
						logger.error("No s'ha pogut afegir el camp múltiple", ex);
					}
					model.addAttribute("tasca", tasca);
		        	model.addAttribute("valorsPerSuggest", TascaFormUtil.getValorsPerSuggest(tasca, command));
		        	return "expedient/dadaForm";
				} else if ("multipleRemove".equals(submit)) {
					try {
						if (field != null && index != null)
							PropertyUtils.setSimpleProperty(
									command,
									field,
									TascaFormUtil.deleteMultiple(field, command, camps, index));
					} catch (Exception ex) {
						missatgeError(request, getMessage("error.proces.peticio"), ex.getLocalizedMessage());
						logger.error("No s'ha pogut afegir el camp múltiple", ex);
					}
					model.addAttribute("tasca", tasca);
		        	model.addAttribute("valorsPerSuggest", TascaFormUtil.getValorsPerSuggest(tasca, command));
		        	return "expedient/dadaForm";
				}
				return "redirect:/expedient/dades.html?id=" + getUrlParamId(entorn.getId(), id, taskId);
			} else {
				missatgeError(request, getMessage("error.permisos.modificar.expedient"));
				return "redirect:/expedient/consulta.html";
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



	private boolean potModificarExpedient(ExpedientDto expedient) {
		return permissionService.filterAllowed(
				expedient.getTipus(),
				ExpedientTipus.class,
				new Permission[] {
					ExtendedPermission.ADMINISTRATION,
					ExtendedPermission.WRITE}) != null;
	}

	private TascaDto createTascaAmbVar(
			Long entornId,
			String id,
			String taskId,
			String var) {
		Camp camp = null;
		Object valor = null;
		InstanciaProcesDto instanciaProces = null;
		TascaDto tasca = null;
		if (taskId == null) {
			instanciaProces = expedientService.getInstanciaProcesById(
					id,
					true);
			camp = dissenyService.findCampAmbDefinicioProcesICodi(
					instanciaProces.getDefinicioProces().getId(),
					var);
			valor = instanciaProces.getVariables().get(var);
		} else {
			tasca = tascaService.getById(entornId, taskId);
			instanciaProces = expedientService.getInstanciaProcesById(
					tasca.getProcessInstanceId(),
					true);
			for (CampTasca campTasca: tasca.getCamps()) {
				if (campTasca.getCamp().getCodi().equals(var)) {
					camp = campTasca.getCamp();
					break;
				}
			}
			valor = tasca.getVariable(var);
		}
		if (camp == null) {
			camp = new Camp();
			camp.setTipus(TipusCamp.STRING);
			camp.setCodi(var);
			camp.setEtiqueta(var);
		}
		TascaDto tascaNova = new TascaDto();
		tascaNova.setId(taskId);
		tascaNova.setTipus(TipusTasca.FORM);
		tascaNova.setProcessInstanceId(instanciaProces.getId());
		List<CampTasca> camps = new ArrayList<CampTasca>();
		CampTasca campTasca = new CampTasca();
		campTasca.setCamp(camp);
		camps.add(campTasca);
		tascaNova.setCamps(camps);
		if (taskId == null)
			tascaNova.setDefinicioProces(instanciaProces.getDefinicioProces());
		else
			tascaNova.setDefinicioProces(tasca.getDefinicioProces());
		if (valor != null) {
			Map<String, Object> variables = new HashMap<String, Object>();
			variables.put(camp.getCodi(), valor);
			tascaNova.setVariables(variables);
			if (taskId == null) {
				tascaNova.setValorsDomini(instanciaProces.getValorsDomini());
				tascaNova.setValorsMultiplesDomini(instanciaProces.getValorsMultiplesDomini());
				tascaNova.setVarsComText(instanciaProces.getVarsComText());
			} else {
				tascaNova.setValorsDomini(tasca.getValorsDomini());
				tascaNova.setValorsMultiplesDomini(tasca.getValorsMultiplesDomini());
				tascaNova.setVarsComText(tasca.getVarsComText());
			}
		}
		return tascaNova;
	}

	private ExpedientDto getExpedient(Long entornId, String id, String taskId) {
		if (id != null)
			return expedientService.findExpedientAmbProcessInstanceId(id);
		if (taskId != null) {
			TascaDto tasca = tascaService.getById(entornId, taskId);
			return expedientService.findExpedientAmbProcessInstanceId(tasca.getProcessInstanceId());
		}
		return null;
	}

	private String getUrlParamId(Long entornId, String id, String taskId) {
		if (id != null)
			return id;
		if (taskId != null) {
			TascaDto tasca = tascaService.getById(entornId, taskId);
			if (tasca != null)
				return tasca.getProcessInstanceId();
		}
		return null;
	}



	private static final Log logger = LogFactory.getLog(ExpedientDadaModificarController.class);

}
