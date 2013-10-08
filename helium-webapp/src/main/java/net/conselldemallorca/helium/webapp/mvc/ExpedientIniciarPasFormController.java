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
import net.conselldemallorca.helium.core.model.dto.TascaDto;
import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.CampTasca;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.Expedient.IniciadorTipus;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.service.DissenyService;
import net.conselldemallorca.helium.core.model.service.ExpedientService;
import net.conselldemallorca.helium.core.model.service.PermissionService;
import net.conselldemallorca.helium.core.model.service.TascaService;
import net.conselldemallorca.helium.core.security.ExtendedPermission;
import net.conselldemallorca.helium.jbpm3.integracio.Termini;
import net.conselldemallorca.helium.webapp.mvc.util.BaseController;
import net.conselldemallorca.helium.webapp.mvc.util.TascaFormUtil;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.security.acls.model.Permission;
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
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
public class ExpedientIniciarPasFormController extends BaseController {

	private DissenyService dissenyService;
	private ExpedientService expedientService;
	private TascaService tascaService;
	private PermissionService permissionService;



	@Autowired
	public ExpedientIniciarPasFormController(
			DissenyService dissenyService,
			ExpedientService expedientService,
			TascaService tascaService,
			PermissionService permissionService) {
		this.dissenyService = dissenyService;
		this.expedientService = expedientService;
		this.tascaService = tascaService;
		this.permissionService = permissionService;
	}

	@ModelAttribute("expedientTipus")
	public ExpedientTipus populateExpedientTipus(
			@RequestParam(value = "expedientTipusId", required = true) Long expedientTipusId) {
		return dissenyService.getExpedientTipusById(expedientTipusId);
	}

	@ModelAttribute("command")
	public Object populateCommand(
			HttpServletRequest request,
			@RequestParam(value = "expedientTipusId", required = false) Long expedientTipusId,
			@RequestParam(value = "definicioProcesId", required = false) Long definicioProcesId,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			Object command = null;
			Object commandSessio = obtenirCommandSessio(request);
			TascaDto tascaInicial = obtenirTascaInicial(
					entorn.getId(),
					expedientTipusId,
					definicioProcesId,
					null,
					request);
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
					if (tascaInicial.getFormExtern() != null) {
						guardarValorsFormExtern(
								request,
								tascaInicial.getId(),
								valorsCommand);
					}
					Map<String, Object> campsAddicionals = new HashMap<String, Object>();
					campsAddicionals.put("entornId", entorn.getId());
					campsAddicionals.put("expedientTipusId", expedientTipusId);
					campsAddicionals.put("definicioProcesId", definicioProcesId);
					@SuppressWarnings("rawtypes")
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
			guardarValorsSessio(request, valorsCommand);
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
				TascaDto tasca = obtenirTascaInicial(
						entorn.getId(),
						expedientTipusId,
						definicioProcesId,
						(Map<String, Object>)model.get("valorsCommand"),
						request);
				model.addAttribute("tasca", tasca);
				return "expedient/iniciarPasForm";
			} else {
				missatgeError(request, getMessage("error.permisos.iniciar.tipus.exp"));
				return "redirect:/expedient/iniciar.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
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
				TascaDto tascaInicial = obtenirTascaInicial(
						entorn.getId(),
						expedientTipusId,
						definicioProcesId,
						(Map<String, Object>)model.get("valorsCommand"),
						request);
				if ("submit".equals(submit)) {
					Validator validator = new TascaFormValidator(
							expedientService,
							obtenirValorsRegistresSessio(request, camps));
					validator.validate(command, result);
					try {
						TascaFormUtil.getBeanValidatorForCommand(camps).validate(command, result);
					} catch (Exception ex) {
						missatgeError(request, getMessage("error.validacio"), ex.getLocalizedMessage());
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
					// Si l'expedient ha de demanar titol i/o número redirigeix al pas per demanar
					// aquestes dades
					if (tipus.getDemanaNumero().booleanValue() || tipus.getDemanaTitol().booleanValue() || tipus.isSeleccionarAny()) {
						guardarCommandSessio(request, command);
						if (definicioProcesId != null)
							return "redirect:/expedient/iniciarPasTitol.html?expedientTipusId=" + expedientTipusId + "&definicioProcesId=" + definicioProcesId;
						else
							return "redirect:/expedient/iniciarPasTitol.html?expedientTipusId=" + expedientTipusId;
					} else {
						try {
							Map<String, Object> valors = new HashMap<String, Object>();
							valors.putAll(obtenirValorsSessio(request));
							valors.putAll(TascaFormUtil.getValorsFromCommand(camps, command, true, false));
							ExpedientDto iniciat = iniciarExpedient(
									entorn.getId(),
									expedientTipusId,
									definicioProcesId,
									(String)request.getSession().getAttribute(ExpedientIniciarController.CLAU_SESSIO_NUMERO),
									(String)request.getSession().getAttribute(ExpedientIniciarController.CLAU_SESSIO_TITOL),
									(Integer)request.getSession().getAttribute(ExpedientIniciarController.CLAU_SESSIO_ANY),
									valors);
							missatgeInfo(
									request,
									getMessage(
											"info.expedient.iniciat",
											new Object[] {iniciat.getIdentificador()}));
							ExpedientIniciarController.netejarSessio(request);
							return "redirect:/expedient/iniciar.html";
						} catch (Exception ex) {
							missatgeError(
									request,
									getMessage("error.iniciar.expedient"),
									(ex.getCause() != null) ? ex.getCause().getMessage() : ex.getMessage());
							logger.error("No s'ha pogut iniciar l'expedient", ex);
							return "expedient/iniciarPasForm";
						}
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
						missatgeError(request, getMessage("error.proces.peticio"), ex.getLocalizedMessage());
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
							esborrarRegistre(
									request,
									camp.getCodi(),
									camp.isMultiple(),
									registreEsborrarIndex.intValue());
						} catch (Exception ex) {
				        	missatgeError(request, getMessage("error.esborrar.registre"), ex.getLocalizedMessage());
				        	logger.error("No s'ha pogut esborrar el registre", ex);
				        }
					}
					guardarCommandSessio(request, command);
					return "redirect:/expedient/iniciarPasForm.html?expedientTipusId=" + expedientTipusId;
				}
			} else {
				missatgeError(request, getMessage("error.permisos.iniciar.tipus.exp"));
				return "redirect:/expedient/iniciar.html";
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
					ExpedientIniciarController.getClauSessioCampRegistre(camp.getCodi()));
			if (obj != null)
				valors.put(camp.getCodi(), obj);
		}
		return valors;
	}
	private void esborrarRegistre(
			HttpServletRequest request,
			String campCodi,
			boolean multiple,
			int index) {
		if (multiple) {
			Object valor = request.getSession().getAttribute(
					ExpedientIniciarController.getClauSessioCampRegistre(campCodi));
			if (valor != null) {
				Object[] valorMultiple = (Object[])valor;
				if (valorMultiple.length > 0) {
					Object[] valorNou = new Object[valorMultiple.length - 1];
					for (int i = 0; i < valorNou.length; i++)
						valorNou[i] = (i < index) ? valorMultiple[i] : valorMultiple[i + 1];
						request.getSession().setAttribute(
								ExpedientIniciarController.getClauSessioCampRegistre(campCodi),
								valorNou);
				}
			}
		} else {
			request.getSession().removeAttribute(
					ExpedientIniciarController.getClauSessioCampRegistre(campCodi));
		}
	}

	private TascaDto obtenirTascaInicial(
			Long entornId,
			Long expedientTipusId,
			Long definicioProcesId,
			Map<String, Object> valors,
			HttpServletRequest request) {
		TascaDto tasca = expedientService.getStartTask(
				entornId,
				expedientTipusId,
				definicioProcesId,
				valors);
		tasca.setId(
				(String)request.getSession().getAttribute(
						ExpedientIniciarController.CLAU_SESSIO_TASKID));
		Object validat = request.getSession().getAttribute(
				ExpedientIniciarController.CLAU_SESSIO_FORM_VALIDAT); 
		if (validat != null)
			tasca.setValidada(true);
		return tasca;
	}

	private void guardarValorsFormExtern(
			HttpServletRequest request,
			String taskId,
			Map<String, Object> valorsCommand) {
		Map<String, Object> valors = tascaService.obtenirValorsFormulariExternInicial(taskId);
		if (valors != null) {
			valorsCommand.putAll(valors);
			request.getSession().setAttribute(
					ExpedientIniciarController.CLAU_SESSIO_FORM_VALIDAT,
					new Boolean(true));
		}
	}

	private Object obtenirCommandSessio(
			HttpServletRequest request) {
		return request.getSession().getAttribute(
				ExpedientIniciarController.CLAU_SESSIO_FORM_COMMAND);
	}
	private void guardarCommandSessio(
			HttpServletRequest request,
			Object command) {
		request.getSession().setAttribute(
				ExpedientIniciarController.CLAU_SESSIO_FORM_COMMAND,
				command);
	}
	@SuppressWarnings("unchecked")
	private Map<String, Object> obtenirValorsSessio(
			HttpServletRequest request) {
		return (Map<String, Object>)request.getSession().getAttribute(
				ExpedientIniciarController.CLAU_SESSIO_FORM_VALORS);
	}
	private void guardarValorsSessio(
			HttpServletRequest request,
			Map<String, Object> valors) {
		request.getSession().setAttribute(
				ExpedientIniciarController.CLAU_SESSIO_FORM_VALORS,
				valors);
	}

	private synchronized ExpedientDto iniciarExpedient(
			Long entornId,
			Long expedientTipusId,
			Long definicioProcesId,
			String numero,
			String titol,
			Integer any,
			Map<String, Object> valors) {
		return expedientService.iniciar(
				entornId,
				null,
				expedientTipusId,
				definicioProcesId,
				any,
				numero,
				titol,
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
	}



	private static final Log logger = LogFactory.getLog(ExpedientIniciarPasFormController.class);

}
