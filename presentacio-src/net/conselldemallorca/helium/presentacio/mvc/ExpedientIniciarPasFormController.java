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

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.model.dto.TascaDto;
import net.conselldemallorca.helium.model.hibernate.Camp;
import net.conselldemallorca.helium.model.hibernate.CampTasca;
import net.conselldemallorca.helium.model.hibernate.Entorn;
import net.conselldemallorca.helium.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.model.hibernate.Expedient.IniciadorTipus;
import net.conselldemallorca.helium.jbpm3.integracio.Termini;
import net.conselldemallorca.helium.model.service.DissenyService;
import net.conselldemallorca.helium.model.service.ExpedientService;
import net.conselldemallorca.helium.model.service.PermissionService;
import net.conselldemallorca.helium.presentacio.mvc.util.BaseController;
import net.conselldemallorca.helium.presentacio.mvc.util.TascaFormUtil;
import net.conselldemallorca.helium.security.permission.ExtendedPermission;

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
 * Controlador pel pas de formulari de l'inici d'expedient
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@Controller
public class ExpedientIniciarPasFormController extends BaseController {

	private DissenyService dissenyService;
	private ExpedientService expedientService;
	private PermissionService permissionService;

	private Validator validator;



	@Autowired
	public ExpedientIniciarPasFormController(
			DissenyService dissenyService,
			ExpedientService expedientService,
			PermissionService permissionService) {
		this.dissenyService = dissenyService;
		this.expedientService = expedientService;
		this.permissionService = permissionService;
		this.validator = new TascaFormValidator(expedientService);
	}

	@ModelAttribute("expedientTipus")
	public ExpedientTipus populateExpedientTipus(
			@RequestParam(value = "expedientTipusId", required = true) Long expedientTipusId) {
		return dissenyService.getExpedientTipusById(expedientTipusId);
	}

	@SuppressWarnings("unchecked")
	@ModelAttribute("command")
	public Object populateCommand(
			HttpServletRequest request,
			@RequestParam(value = "expedientTipusId", required = false) Long expedientTipusId,
			@RequestParam(value = "definicioProcesId", required = false) Long definicioProcesId) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			TascaDto tascaInicial = expedientService.getStartTask(
					entorn.getId(),
					expedientTipusId,
					definicioProcesId,
	        		null);
			if (tascaInicial != null) {
				Map<String, Object> campsAddicionals = new HashMap<String, Object>();
				campsAddicionals.put("entornId", entorn.getId());
				campsAddicionals.put("expedientTipusId", expedientTipusId);
				campsAddicionals.put("definicioProcesId", definicioProcesId);
				Map<String, Class> campsAddicionalsClasses = new HashMap<String, Class>();
				campsAddicionalsClasses.put("entornId", Long.class);
				campsAddicionalsClasses.put("expedientTipusId", Long.class);
				campsAddicionalsClasses.put("definicioProcesId", Long.class);
				Object command = TascaFormUtil.getCommandForTasca(
						tascaInicial,
						campsAddicionals,
						campsAddicionalsClasses);
				return command;
			}
		}
		return null;
	}

	@RequestMapping(value = "/expedient/iniciarPasForm", method = RequestMethod.GET)
	public String iniciarPasFormGet(
			HttpServletRequest request,
			@RequestParam(value = "expedientTipusId", required = true) Long expedientTipusId,
			@RequestParam(value = "definicioProcesId", required = false) Long definicioProcesId,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientTipus tipus = dissenyService.getExpedientTipusById(expedientTipusId);
			if (potIniciarExpedientTipus(tipus)) {
				model.addAttribute(
						"tasca",
						expedientService.getStartTask(
								entorn.getId(),
								expedientTipusId,
								definicioProcesId,
				        		null));
				return "expedient/iniciarPasForm";
			} else {
				missatgeError(request, "No té permisos per iniciar expedients d'aquest tipus");
				return "redirect:/expedient/iniciar.html";
			}
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/expedient/iniciarPasForm", method = RequestMethod.POST)
	public String iniciarPasFormPost(
			HttpServletRequest request,
			@RequestParam(value = "expedientTipusId", required = true) Long expedientTipusId,
			@RequestParam(value = "definicioProcesId", required = false) Long definicioProcesId,
			@RequestParam(value = "submit", required = false) String submit,
			@RequestParam(value = "helMultipleIndex", required = false) Integer index,
			@RequestParam(value = "helMultipleField", required = false) String field,
			@ModelAttribute("command") Object command,
			BindingResult result,
			SessionStatus status,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientTipus tipus = dissenyService.getExpedientTipusById(expedientTipusId);
			if (potIniciarExpedientTipus(tipus)) {
				TascaDto tascaInicialSenseValors = expedientService.getStartTask(
						entorn.getId(),
						expedientTipusId,
						definicioProcesId,
		        		null);
				List<Camp> camps = new ArrayList<Camp>();
	    		for (CampTasca campTasca: tascaInicialSenseValors.getCamps())
	    			camps.add(campTasca.getCamp());
				TascaDto tascaInicial = expedientService.getStartTask(
						entorn.getId(),
						expedientTipusId,
						definicioProcesId,
						TascaFormUtil.valorsFromCommand(
								camps,
	        					command,
	        					false,
	    						false));
				if ("submit".equals(submit) || submit.length() == 0) {
					validator.validate(command, result);
					try {
						TascaFormUtil.getBeanValidatorForCommand(camps).validate(command, result);
					} catch (Exception ex) {
						missatgeError(request, "S'han produit errors de validació", ex.getLocalizedMessage());
			        	logger.error("S'han produit errors de validació", ex);
			        	model.addAttribute("tasca", tascaInicial);
			        	model.addAttribute("valorsPerSuggest", TascaFormUtil.getValorsPerSuggest(tascaInicial, command));
			        	return "expedient/iniciarPasForm";
					}
					if (result.hasErrors()) {
						model.addAttribute("tasca", tascaInicial);
			        	model.addAttribute("valorsPerSuggest", TascaFormUtil.getValorsPerSuggest(tascaInicial, command));
			        	return "expedient/iniciarPasForm";
			        }
					try {
				        expedientService.iniciar(
								entorn.getId(),
								expedientTipusId,
								definicioProcesId,
					        	(String)request.getSession().getAttribute(ExpedientIniciarPasTitolController.CLAU_SESSIO_NUMERO),
					        	(String)request.getSession().getAttribute(ExpedientIniciarPasTitolController.CLAU_SESSIO_TITOL),
					        	TascaFormUtil.valorsFromCommand(
					        			camps,
			        					command,
			        					true,
			    						false),
								null,
								IniciadorTipus.INTERN,
								null,
								null);
				        missatgeInfo(request, "L'expedient s'ha iniciat correctament");
				        return "redirect:/expedient/iniciar.html";
			        } catch (Exception ex) {
			        	missatgeError(
								request,
								"S'ha produït un error iniciant l'expedient",
								(ex.getCause() != null) ? ex.getCause().getMessage() : ex.getMessage());
			        	logger.error("No s'ha pogut iniciar l'expedient", ex);
			        	return "expedient/iniciarPasForm";
					}
				} else if ("multipleAdd".equals(submit)) {
					try {
						if (field != null)
							PropertyUtils.setSimpleProperty(
									command,
									field,
									TascaFormUtil.addMultiple(field, command, camps));
					} catch (Exception ex) {
						missatgeError(request, "S'ha produït un error processant la seva petició", ex.getLocalizedMessage());
						logger.error("No s'ha pogut afegir el camp múltiple", ex);
					}
					model.addAttribute("tasca", tascaInicial);
		        	model.addAttribute("valorsPerSuggest", TascaFormUtil.getValorsPerSuggest(tascaInicial, command));
		        	return "expedient/iniciarPasForm";
				} else if ("multipleRemove".equals(submit)) {
					try {
						if (field != null && index != null)
							PropertyUtils.setSimpleProperty(
									command,
									field,
									TascaFormUtil.deleteMultiple(field, command, camps, index));
					} catch (Exception ex) {
						missatgeError(request, "S'ha produït un error processant la seva petició", ex.getLocalizedMessage());
						logger.error("No s'ha pogut afegir el camp múltiple", ex);
					}
					model.addAttribute("tasca", tascaInicial);
		        	model.addAttribute("valorsPerSuggest", TascaFormUtil.getValorsPerSuggest(tascaInicial, command));
		        	return "expedient/iniciarPasForm";
				} else {
					return "redirect:/expedient/iniciar.html";
				}
			} else {
				missatgeError(request, "No té permisos per iniciar expedients d'aquest tipus");
				return "redirect:/expedient/iniciar.html";
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



	private boolean potIniciarExpedientTipus(ExpedientTipus expedientTipus) {
		return permissionService.filterAllowed(
				expedientTipus,
				ExpedientTipus.class,
				new Permission[] {
					ExtendedPermission.ADMINISTRATION,
					ExtendedPermission.CREATE}) != null;
	}

	private static final Log logger = LogFactory.getLog(ExpedientIniciarPasFormController.class);

}
