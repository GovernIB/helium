/**
 * 
 */
package net.conselldemallorca.helium.presentacio.mvc;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.jbpm3.integracio.Termini;
import net.conselldemallorca.helium.model.dto.TascaDto;
import net.conselldemallorca.helium.model.hibernate.Camp;
import net.conselldemallorca.helium.model.hibernate.CampTasca;
import net.conselldemallorca.helium.model.hibernate.Entorn;
import net.conselldemallorca.helium.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.model.hibernate.Expedient.IniciadorTipus;
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



	@Autowired
	public ExpedientIniciarPasFormController(
			DissenyService dissenyService,
			ExpedientService expedientService,
			PermissionService permissionService) {
		this.dissenyService = dissenyService;
		this.expedientService = expedientService;
		this.permissionService = permissionService;
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
			@RequestParam(value = "definicioProcesId", required = false) Long definicioProcesId,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			Object command = null;
			Object commandSessio = TascaFormUtil.recuperarCommandTemporal(request, true);
			TascaDto tascaInicial = expedientService.getStartTask(
					entorn.getId(),
					expedientTipusId,
					definicioProcesId,
	        		null);
			List<Camp> camps = new ArrayList<Camp>();
			for (CampTasca campTasca: tascaInicial.getCamps())
				camps.add(campTasca.getCamp());
			Map<String, Object> valorsCommand = new HashMap<String, Object>();
			if (tascaInicial.getVariables() != null)
				valorsCommand.putAll(tascaInicial.getVariables());
			valorsCommand.putAll(obtenirValorsRegistresSessio(request, camps));
			if (commandSessio != null) {
				command = commandSessio;
			} else {
				if (tascaInicial != null) {
					Map<String, Object> campsAddicionals = new HashMap<String, Object>();
					campsAddicionals.put("entornId", entorn.getId());
					campsAddicionals.put("expedientTipusId", expedientTipusId);
					campsAddicionals.put("definicioProcesId", definicioProcesId);
					Map<String, Class> campsAddicionalsClasses = new HashMap<String, Class>();
					campsAddicionalsClasses.put("entornId", Long.class);
					campsAddicionalsClasses.put("expedientTipusId", Long.class);
					campsAddicionalsClasses.put("definicioProcesId", Long.class);
					command = TascaFormUtil.getCommandForCamps(
							camps,
							valorsCommand,
							campsAddicionals,
							campsAddicionalsClasses,
							false);
				}
			}
			model.addAttribute("camps", camps);
			model.addAttribute("valorsCommand", valorsCommand);
			/*Map<String, Object> valorsRegistres = obtenirValorsRegistresSessio(request, camps);
			for (String codi: valorsRegistres.keySet()) {
				try {
					Object[] valor = (Object[])valorsRegistres.get(codi);
					PropertyUtils.setSimpleProperty(command, codi, valor);
				} catch (Exception ex) {
					logger.warn(ex);
				}
			}*/
			return command;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
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
				TascaDto tasca = expedientService.getStartTask(
						entorn.getId(),
						expedientTipusId,
						definicioProcesId,
						(Map<String, Object>)model.get("valorsCommand"));
				model.addAttribute("tasca", tasca);
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

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/expedient/iniciarPasForm", method = RequestMethod.POST)
	public String iniciarPasFormPost(
			HttpServletRequest request,
			@RequestParam(value = "expedientTipusId", required = true) Long expedientTipusId,
			@RequestParam(value = "definicioProcesId", required = false) Long definicioProcesId,
			@RequestParam(value = "submit", required = false) String submit,
			@RequestParam(value = "helMultipleIndex", required = false) Integer index,
			@RequestParam(value = "helMultipleField", required = false) String field,
			@RequestParam(value = "registreEsborrarId", required = false) Long registreEsborrarId,
			@RequestParam(value = "registreEsborrarIndex", required = false) Integer registreEsborrarIndex,
			@ModelAttribute("command") Object command,
			BindingResult result,
			SessionStatus status,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientTipus tipus = dissenyService.getExpedientTipusById(expedientTipusId);
			if (potIniciarExpedientTipus(tipus)) {
				List<Camp> camps = (List<Camp>)model.get("camps");
				TascaDto tascaInicial = expedientService.getStartTask(
						entorn.getId(),
						expedientTipusId,
						definicioProcesId,
						(Map<String, Object>)model.get("valorsCommand"));
				if ("submit".equals(submit)) {
					Validator validator = new TascaFormValidator(
							expedientService,
							obtenirValorsRegistresSessio(request, camps));
					validator.validate(command, result);
					try {
						TascaFormUtil.getBeanValidatorForCommand(camps).validate(command, result);
					} catch (Exception ex) {
						missatgeError(request, "S'han produit errors de validació", ex.getLocalizedMessage());
			        	logger.error("S'han produit errors de validació", ex);
			        	model.addAttribute("tasca", tascaInicial);
			        	model.addAttribute(
			        			"valorsPerSuggest",
			        			TascaFormUtil.getValorsPerSuggest(tascaInicial, command));
			        	return "expedient/iniciarPasForm";
					}
					if (result.hasErrors()) {
						model.addAttribute("tasca", tascaInicial);
			        	model.addAttribute(
			        			"valorsPerSuggest",
			        			TascaFormUtil.getValorsPerSuggest(tascaInicial, command));
			        	return "expedient/iniciarPasForm";
			        }
					try {
						Map<String, Object> valors = new HashMap<String, Object>();
						valors.putAll((Map<String, Object>)model.get("valorsCommand"));
						valors.putAll(TascaFormUtil.getValorsFromCommand(camps, command, true, false));
						expedientService.iniciar(
								entorn.getId(),
								expedientTipusId,
								definicioProcesId,
								(String)request.getSession().getAttribute(ExpedientIniciarPasTitolController.CLAU_SESSIO_NUMERO),
								(String)request.getSession().getAttribute(ExpedientIniciarPasTitolController.CLAU_SESSIO_TITOL),
								null,
								null,
								null,
								null,
								false,
								null,
								null,
								null,
								null,
								null,
								null,
								false,
								null,
								null,
								false,
								valors,
								null,
								IniciadorTipus.INTERN,
								null,
								null,
								null,
								null);
				        missatgeInfo(request, "L'expedient s'ha iniciat correctament");
				        netejarSessioRegistres(request);
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
				} else if ("cancel".equals(submit)) {
					status.setComplete();
					return "redirect:/expedient/iniciar.html";
				} else {
					status.setComplete();
					if (registreEsborrarId != null && registreEsborrarIndex != null) {
						Camp camp = dissenyService.getCampById(registreEsborrarId);
						try {
							esborrarRegistre(request, camp.getCodi(), registreEsborrarIndex.intValue());
						} catch (Exception ex) {
				        	missatgeError(request, "No s'ha pogut esborrar el registre", ex.getLocalizedMessage());
				        	logger.error("No s'ha pogut esborrar el registre", ex);
				        }
					}
					TascaFormUtil.guardarCommandTemporal(request, command);
					return "redirect:/expedient/iniciarPasForm.html?expedientTipusId=" + expedientTipusId;
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
	private Map<String, Object> obtenirValorsRegistresSessio(
			HttpServletRequest request,
			List<Camp> camps) {
		Map<String, Object> valors = new HashMap<String, Object>();
		for (Camp camp: camps) {
			Object obj = request.getSession().getAttribute(
					ExpedientIniciarRegistreController.PREFIX_REGISTRE_SESSIO + camp.getCodi());
			if (obj != null)
				valors.put(camp.getCodi(), obj);
		}
		return valors;
	}
	private void esborrarRegistre(
			HttpServletRequest request,
			String campCodi,
			int index) {
		Object valor = request.getSession().getAttribute(
				ExpedientIniciarRegistreController.PREFIX_REGISTRE_SESSIO + campCodi);
		if (valor != null) {
			Object[] valorMultiple = (Object[])valor;
			if (valorMultiple.length > 0) {
				Object[] valorNou = new Object[valorMultiple.length - 1];
				for (int i = 0; i < valorNou.length; i++)
					valorNou[i] = (i < index) ? valorMultiple[i] : valorMultiple[i + 1];
					request.getSession().setAttribute(
							ExpedientIniciarRegistreController.PREFIX_REGISTRE_SESSIO + campCodi,
							valorNou);
			}
		}
	}
	@SuppressWarnings("unchecked")
	private void netejarSessioRegistres(HttpServletRequest request) {
		Enumeration<String> atributs = request.getSession().getAttributeNames();
		while (atributs.hasMoreElements()) {
			String atribut = atributs.nextElement();
			if (atribut.startsWith(ExpedientIniciarRegistreController.PREFIX_REGISTRE_SESSIO))
				request.getSession().removeAttribute(atribut);
		}
	}



	private static final Log logger = LogFactory.getLog(ExpedientIniciarPasFormController.class);

}
