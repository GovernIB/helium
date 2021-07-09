package es.caib.helium.back.controller;

import es.caib.helium.back.command.AnotacioAcceptarCommand;
import es.caib.helium.back.helper.MissatgesHelper;
import es.caib.helium.back.helper.ObjectTypeEditorHelper;
import es.caib.helium.back.helper.SessionHelper;
import es.caib.helium.back.helper.TascaFormHelper;
import es.caib.helium.back.helper.TascaFormValidatorHelper;
import es.caib.helium.logic.intf.dto.DefinicioProcesDto;
import es.caib.helium.logic.intf.dto.EntornDto;
import es.caib.helium.logic.intf.dto.ExpedientTascaDto;
import es.caib.helium.logic.intf.dto.ExpedientTipusDto;
import es.caib.helium.logic.intf.dto.ReproDto;
import es.caib.helium.logic.intf.dto.TascaDadaDto;
import es.caib.helium.logic.intf.exception.NoTrobatException;
import es.caib.helium.logic.intf.exception.TramitacioHandlerException;
import es.caib.helium.logic.intf.exception.TramitacioValidacioException;
import es.caib.helium.logic.intf.exception.ValidacioException;
import es.caib.helium.logic.intf.service.ReproService;
import es.caib.helium.logic.intf.service.TascaService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador pel pas de formulari de l'inici d'expedient
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedient")
public class ExpedientInicioPasFormController extends BaseExpedientIniciController {

	@Autowired
	private ReproService reproService;
	@Autowired
	private TascaService tascaInicialService;



	@SuppressWarnings("unchecked")
	@ModelAttribute("command")
	protected Object populateCommand(
			HttpServletRequest request, 
			@PathVariable Long expedientTipusId,
			@PathVariable Long definicioProcesId,
			Model model,
			Map<String, Object> valorsRepro) {
		try {
			Map<String, Object> campsAddicionals = new HashMap<String, Object>();
			Map<String, Class<?>> campsAddicionalsClasses = new HashMap<String, Class<?>>();
			EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
			ExpedientTascaDto tasca = obtenirTascaInicial(
					entorn.getId(),
					expedientTipusId,
					definicioProcesId,
					new HashMap<String, Object>(),
					request);
			campsAddicionals.put("id", tasca.getId());
			campsAddicionals.put("entornId", entorn.getId());
			campsAddicionals.put("expedientTipusId", expedientTipusId);
			campsAddicionals.put("definicioProcesId", definicioProcesId);
			campsAddicionalsClasses.put("id", String.class);
			campsAddicionalsClasses.put("entornId", Long.class);
			campsAddicionalsClasses.put("expedientTipusId", Long.class);
			campsAddicionalsClasses.put("definicioProcesId", Long.class);
			Map<String, Object> valorsFormulariExtern = null;
			if (tasca.isFormExtern()) {
				valorsFormulariExtern = tascaInicialService.obtenirValorsFormulariExternInicial(tasca.getId());
				if (valorsFormulariExtern != null) {
					request.getSession().setAttribute(
							CLAU_SESSIO_FORM_VALORS,
							valorsFormulariExtern);
				} else {
					valorsFormulariExtern = (Map<String, Object>)request.getSession().getAttribute(
							CLAU_SESSIO_FORM_VALORS);
				}
			}
			if (valorsRepro == null || valorsRepro.isEmpty()) {
				return TascaFormHelper.getCommandForCamps(
						tascaService.findDadesPerTascaDto(expedientTipusId, tasca),
						valorsFormulariExtern,
						campsAddicionals,
						campsAddicionalsClasses,
						false);
			} else {
				return TascaFormHelper.getCommandForCamps(
						tascaService.findDadesPerTascaDto(expedientTipusId, tasca),
						valorsRepro,
						campsAddicionals,
						campsAddicionalsClasses,
						false);
			}
		} catch (NoTrobatException ex) {
			MissatgesHelper.error(request, ex.getMessage());
			logger.error("No s'han pogut encontrar la tasca: " + ex.getMessage(), ex);
		}
		return null;
	}

	@RequestMapping(value = "/iniciarForm/{expedientTipusId}/{definicioProcesId}", method = RequestMethod.GET)
	public String iniciarFormGet(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long definicioProcesId,
			@RequestParam(required=false) Long reproId,
			Model model) {
		if (reproId != null) {
			try {
				Map<String,Object> valors = reproService.findValorsById(reproId);
				model.addAttribute("command", populateCommand(request, expedientTipusId, definicioProcesId, model, valors));
			} catch (Exception e) {
				MissatgesHelper.error(request, getMessage(request, "repro.missatge.error.carregat"));
			}
		}
		definicioProcesToModel(expedientTipusId, definicioProcesId, model);
		EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
		ExpedientTipusDto expedientTipus = dissenyService.getExpedientTipusById(expedientTipusId);
		if (!model.containsAttribute("command") || model.asMap().get("command") == null)
			model.addAttribute("command", populateCommand(request, expedientTipusId, definicioProcesId, model,null));
		ExpedientTascaDto tasca = obtenirTascaInicial(entorn.getId(), expedientTipusId, definicioProcesId, new HashMap<String, Object>(), request);
		List<TascaDadaDto> dades = tascaService.findDadesPerTascaDto(expedientTipusId, tasca);
		List<ReproDto> repros = reproService.findReprosByUsuariTipusExpedient(expedientTipus.getId(), tasca.getJbpmName());
		model.addAttribute("tasca", tasca);
		model.addAttribute("dades", dades);
		model.addAttribute("repros", repros);
		model.addAttribute("entornId", entorn.getId());
		model.addAttribute("expedientTipus", expedientTipus);
		model.addAttribute("responsableCodi", expedientTipus.getResponsableDefecteCodi());
		// Pot ser que vingui del formulari d'acceptar i crear un expedient per a una anotació de Distribució
		model.addAttribute("anotacioAcceptarCommand", (AnotacioAcceptarCommand) request.getSession().getAttribute(CLAU_SESSIO_ANOTACIO));
		return "v3/expedient/iniciarPasForm";
	}

	@RequestMapping(value = "/iniciarForm/{expedientTipusId}/{definicioProcesId}", method = RequestMethod.POST)
	public String iniciarFormPost(
			HttpServletRequest request, 
			@RequestParam(value = "id", required = false) String id, 
			@PathVariable Long expedientTipusId,
			@PathVariable Long definicioProcesId,
			@Valid @ModelAttribute("command") Object command,
			BindingResult result, 
			SessionStatus status, 
			Model model) {
		EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
		ExpedientTipusDto expedientTipus = dissenyService.getExpedientTipusById(expedientTipusId);
		ExpedientTascaDto tasca = obtenirTascaInicial(entorn.getId(), expedientTipusId, definicioProcesId, new HashMap<String, Object>(), request);
		List<TascaDadaDto> tascaDades = tascaService.findDadesPerTascaDto(expedientTipusId, tasca);
		TascaFormValidatorHelper validator = new TascaFormValidatorHelper(
				tascaService,
				tascaDades);
		Map<String, Object> valors = TascaFormHelper.getValorsFromCommand(
				tascaDades,
				command,
				false,
				true);
		validator.setValidarObligatoris(true);
		validator.setValidarExpresions(true);
		validator.validate(command, result);
		DefinicioProcesDto definicioProces = null;
		if (definicioProcesId != null) {
			definicioProces = dissenyService.getById(definicioProcesId);
		} else {
			definicioProces = dissenyService.findDarreraDefinicioProcesForExpedientTipus(expedientTipusId);
		}
		model.addAttribute("definicioProces", definicioProces);
		AnotacioAcceptarCommand anotacioAcceptarCommand = (AnotacioAcceptarCommand) request.getSession().getAttribute(CLAU_SESSIO_ANOTACIO);
		if (result.hasErrors()) {
			MissatgesHelper.error(request, getMessage(request, "error.validacio"));
			model.addAttribute(command);
			model.addAttribute("tasca", tasca);
			model.addAttribute("dades", tascaDades);
			model.addAttribute("repros", reproService.findReprosByUsuariTipusExpedient(expedientTipus.getId(), tasca.getJbpmName()));
			model.addAttribute("entornId", entorn.getId());
			model.addAttribute("expedientTipus", expedientTipus);
			model.addAttribute("responsableCodi", expedientTipus.getResponsableDefecteCodi());
			model.addAttribute("anotacioAcceptarCommand", (AnotacioAcceptarCommand) request.getSession().getAttribute(CLAU_SESSIO_ANOTACIO));
			return "v3/expedient/iniciarPasForm";
		}
		// Si l'expedient ha de demanar titol i/o número redirigeix al pas per demanar aquestes dades
		if (expedientTipus.isDemanaNumero() || expedientTipus.isDemanaTitol() || expedientTipus.isSeleccionarAny()) {
			// Passa els valors per sessió
			request.getSession().setAttribute(
					ExpedientIniciController.CLAU_SESSIO_FORM_VALORS,
					valors);
			// Redirigeix al formulari del títol
			return redirectByModal(request, "/v3/expedient/iniciarTitol/" + expedientTipusId + "/" + definicioProces.getId());
		} else {
			try {
				super.iniciarExpedient(
						request,
						entorn.getId(),
						expedientTipusId,
						definicioProcesId,
						(String)request.getSession().getAttribute(CLAU_SESSIO_NUMERO),
						(String)request.getSession().getAttribute(CLAU_SESSIO_TITOL),
						(Integer)request.getSession().getAttribute(CLAU_SESSIO_ANY),
						valors,
						anotacioAcceptarCommand);
				
			} catch (Exception ex) {
				if (ex instanceof ValidacioException) {
					MissatgesHelper.error(
		        			request,
		        			getMessage(request, "error.validacio.tasca") + " : " + ex.getMessage());
				}  else if (ex instanceof TramitacioValidacioException) {
					MissatgesHelper.error(
		        			request,
		        			getMessage(request, "error.validacio.tasca") + " : " + ex.getMessage());
				} else if (ex instanceof TramitacioHandlerException) {
					MissatgesHelper.error(
		        			request,
		        			getMessage(request, "error.iniciar.expedient") + " : " + ((TramitacioHandlerException)ex).getPublicMessage());
				} else {
					MissatgesHelper.error(
		        			request,
		        			getMessage(request, "error.iniciar.expedient") + ": " + 
		        					(ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage()));
		        }
				
				logger.error("No s'ha pogut iniciar l'expedient", ex);
				model.addAttribute(command);
				model.addAttribute("tasca", tasca);
				model.addAttribute("dades", tascaDades);
				model.addAttribute("repros", reproService.findReprosByUsuariTipusExpedient(expedientTipus.getId(), tasca.getJbpmName()));
				model.addAttribute("entornId", entorn.getId());
				model.addAttribute("expedientTipus", expedientTipus);
				model.addAttribute("responsableCodi", expedientTipus.getResponsableDefecteCodi());
				return "v3/expedient/iniciarPasForm";
			}
		}
		return modalUrlTancar(false);
	}

	protected ExpedientTascaDto obtenirTascaInicial(
			Long entornId,
			Long expedientTipusId,
			Long definicioProcesId,
			Map<String, Object> valors,
			HttpServletRequest request) {
		ExpedientTascaDto tasca = expedientService.getStartTask(entornId, expedientTipusId, definicioProcesId, valors);
		tasca.setId((String) request.getSession().getAttribute(CLAU_SESSIO_TASKID));
		Object validat = request.getSession().getAttribute(CLAU_SESSIO_FORM_VALIDAT);
		tasca.setValidada(validat != null);
		return tasca;
	}

	private void definicioProcesToModel(Long expedientTipusId, Long definicioProcesId, Model model){
		// Si l'expedient requereix dades inicials redirigeix al pas per demanar aquestes dades
		DefinicioProcesDto definicioProces = null;
		if (definicioProcesId != null) {
			definicioProces = dissenyService.getById(definicioProcesId);
		} else {
			definicioProces = dissenyService.findDarreraDefinicioProcesForExpedientTipus(expedientTipusId);
		}
		model.addAttribute("definicioProces", definicioProces);
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
				new CustomBooleanEditor(true));
		binder.registerCustomEditor(
				Date.class,
				new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true));
		binder.registerCustomEditor(
				Object.class,
				new ObjectTypeEditorHelper());
	}
	
	/** Mètode per retornar l'HTML d'una nova fila per una variable de tipus registre múltiple
	 * 
	 */
	@RequestMapping(value = "/iniciarForm/{expedientTipusId}/afegir/{campId}", method = RequestMethod.GET)
	public String afegir(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long definicioProcesId,
			@PathVariable String varCodi,
			@PathVariable String campId,
			Model model) {
		
//		try {
//			Map<String, Object> campsAddicionals = new HashMap<String, Object>();
//			Map<String, Class<?>> campsAddicionalsClasses = new HashMap<String, Class<?>>();
//			List<TascaDadaDto> llistTasca = new ArrayList<TascaDadaDto>();
//			TascaDadaDto tascaDada = TascaFormHelper.getTascaDadaDtoFromExpedientDadaDto(
//					expedientDadaService.findOnePerInstanciaProces(expedientId, procesId, varCodi));
//			if (tascaDada.getError() != null)
//				MissatgesHelper.error(request, tascaDada.getError());
//			llistTasca.add(tascaDada);
//			model.addAttribute("varCodi", varCodi);
//			model.addAttribute("dada", tascaDada);
//			Object command = TascaFormHelper.getCommandForCamps(llistTasca, null, campsAddicionals, campsAddicionalsClasses,
//					false);
//			model.addAttribute("modificarVariableCommand", command);
//		} catch (UnsupportedEncodingException ex) {
//			MissatgesHelper.error(request, ex.getMessage());
//			logger.error("No s'ha pogut obtenir la informació de la dada " + varCodi + ": " + ex.getMessage(), ex);
//		}
		return "v3/campsTascaRegistreRow";
	}

	private static final Log logger = LogFactory.getLog(ExpedientIniciController.class);
}
