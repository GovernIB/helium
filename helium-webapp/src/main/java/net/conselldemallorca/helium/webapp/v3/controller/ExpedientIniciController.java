/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.jbpm3.handlers.exception.ValidationException;
import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto.IniciadorTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.FormulariExternDto;
import net.conselldemallorca.helium.v3.core.api.dto.ParellaCodiValorDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDadaDto;
import net.conselldemallorca.helium.v3.core.api.exception.TascaNotFoundException;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.v3.core.api.service.TascaService;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientInicioPasTitolCommand;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.ObjectTypeEditorHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;
import net.conselldemallorca.helium.webapp.v3.helper.TascaFormHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controlador per iniciar un expedient
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedient")
public class ExpedientIniciController extends BaseExpedientController {
	public static final String CLAU_SESSIO_ANY = "iniciexp_any";
	public static final String CLAU_SESSIO_TASKID = "iniciexp_taskId";
	public static final String CLAU_SESSIO_TITOL = "iniciexp_titol";
	public static final String CLAU_SESSIO_NUMERO = "iniciexp_numero";
	public static final String CLAU_SESSIO_FORM_VALIDAT = "iniciexp_form_validat";
	private static final String CLAU_SESSIO_FORM_COMMAND = "iniciexp_form_command";
	public static final String CLAU_SESSIO_FORM_VALORS = "iniciexp_form_registres";
	private static final String CLAU_SESSIO_PREFIX_REGISTRE = "ExpedientIniciarController_reg_";

	@Autowired
	private ExpedientService expedientService;

	@Autowired
	private TascaService tascaService;
	
	@Autowired
	private net.conselldemallorca.helium.core.model.service.TascaService tascaInicialService;
	
	@ModelAttribute("command")
	protected Object populateCommand(
			HttpServletRequest request, 
			Long expedientTipusId,
			Long definicioProcesId,
			Model model) {
		try {
			Map<String, Object> campsAddicionals = new HashMap<String, Object>();
			Map<String, Class<?>> campsAddicionalsClasses = new HashMap<String, Class<?>>();
			EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
			ExpedientTascaDto tasca = obtenirTascaInicial(entorn.getId(), expedientTipusId, definicioProcesId, new HashMap<String, Object>(), request);
			campsAddicionals.put("id", tasca.getId());
			campsAddicionals.put("entornId", entorn.getId());
			campsAddicionals.put("expedientTipusId", expedientTipusId);
			campsAddicionals.put("definicioProcesId", definicioProcesId);
			campsAddicionalsClasses.put("id", String.class);
			campsAddicionalsClasses.put("entornId", Long.class);
			campsAddicionalsClasses.put("expedientTipusId", Long.class);
			campsAddicionalsClasses.put("definicioProcesId", Long.class);
			return TascaFormHelper.getCommandForCamps(
					tascaService.findDadesPerTascaDto(tasca),
					tascaInicialService.obtenirValorsFormulariExternInicial(tasca.getId()),
					campsAddicionals,
					campsAddicionalsClasses,
					false);
		} catch (TascaNotFoundException ex) {
			MissatgesHelper.error(request, ex.getMessage());
			logger.error("No s'han pogut encontrar la tasca: " + ex.getMessage(), ex);
		} catch (Exception ignored) {} 
		return null;
	}

	@RequestMapping(value = "/iniciar", method = RequestMethod.GET)
	public String iniciarGet(HttpServletRequest request, Model model) {
		EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
		List<ExpedientTipusDto> tipus = dissenyService.findExpedientTipusAmbEntorn(entorn);
		Map<Long, DefinicioProcesExpedientDto> definicionsProces = new HashMap<Long, DefinicioProcesExpedientDto>();
		Iterator<ExpedientTipusDto> it = tipus.iterator();
		while (it.hasNext()) {
			ExpedientTipusDto expedientTipus = it.next();
			DefinicioProcesExpedientDto definicioProcesIniciExpedientDto = dissenyService.getDefinicioProcesByTipusExpedientById(expedientTipus.getId());
			if (definicioProcesIniciExpedientDto != null)
				definicionsProces.put(expedientTipus.getId(), definicioProcesIniciExpedientDto);
			else
				it.remove();
		}
		model.addAttribute("expedientTipus", tipus);
		model.addAttribute("definicionsProces", definicionsProces);
		return "v3/expedient/iniciar";
	}
	
	@RequestMapping(value = "/iniciar", method = RequestMethod.POST)
	public String iniciarNetejarPost(
			HttpServletRequest request, 
			@RequestParam(value = "expedientTipusId", required = true) Long expedientTipusId, 
			@RequestParam(value = "definicioProcesId", required = true) Long definicioProcesId, 
			Model model) {
		
		netejarSessio(request);
		request.getSession().setAttribute(ExpedientIniciController.CLAU_SESSIO_TASKID, "TIE_" + System.currentTimeMillis());
		
		EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
		ExpedientTipusDto expedientTipus = dissenyService.getExpedientTipusById(expedientTipusId);
		
		// Si l'expedient requereix dades inicials redirigeix al pas per demanar aquestes dades
		DefinicioProcesDto definicioProces = null;
		if (definicioProcesId != null) {
			definicioProces = dissenyService.getById(definicioProcesId);
		} else {
			definicioProces = dissenyService.findDarreraDefinicioProcesForExpedientTipus(expedientTipusId);
		}
		
		if (definicioProces.isHasStartTask()) {
			return redirectByModal(request, "/v3/expedient/iniciarForm/" + expedientTipusId + "/" + definicioProces.getId());
		} else if (expedientTipus.isDemanaNumero() || expedientTipus.isDemanaTitol() || expedientTipus.isSeleccionarAny()) {
			return redirectByModal(request, "/v3/expedient/iniciarTitol/" + expedientTipusId + "/" + definicioProces.getId());
		} else {
			// Si no requereix cap pas addicional inicia l'expedient directament
			try {
				ExpedientDto iniciat = iniciarExpedient(entorn.getId(), expedientTipusId, definicioProces.getId());
				MissatgesHelper.success(request, getMessage(request, "info.expedient.iniciat", new Object[] { iniciat.getIdentificador() }));
				ExpedientIniciController.netejarSessio(request);
				return modalUrlTancar();
			} catch (Exception ex) {
				if (ex.getCause() != null && (ex instanceof ValidationException || ex.getCause() instanceof ValidationException)) {
					MissatgesHelper.error(
		        			request,
		        			getMessage(request, "error.validacio.tasca") + " : " + ex.getCause().getMessage());
				} else {
					MissatgesHelper.error(
		        			request,
		        			getMessage(request, "error.iniciar.expedient") + ": " + 
		        					(ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage()));
					logger.error("No s'ha pogut iniciar l'expedient", ex);
		        }
			}
		}
		return iniciarGet(request, model);
	}

	@RequestMapping(value = "/iniciarForm/{expedientTipusId}/{definicioProcesId}", method = RequestMethod.GET)
	public String iniciarFormGet(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long definicioProcesId,
			Model model) {
		
		definicioProcesToModel(expedientTipusId, definicioProcesId, model);
		
		EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
		ExpedientTipusDto expedientTipus = dissenyService.getExpedientTipusById(expedientTipusId);
		
		if (!model.containsAttribute("command") || model.asMap().get("command") == null)
			model.addAttribute("command", populateCommand(request, expedientTipusId, definicioProcesId, model));
		
		ExpedientTascaDto tasca = obtenirTascaInicial(entorn.getId(), expedientTipusId, definicioProcesId, new HashMap<String, Object>(), request);
		List<TascaDadaDto> dades = tascaService.findDadesPerTascaDto(tasca);
		
		model.addAttribute("tasca", tasca);
		model.addAttribute("dades", dades);
		model.addAttribute("entornId", entorn.getId());
		model.addAttribute("expedientTipus", expedientTipus);
		model.addAttribute("responsableCodi", expedientTipus.getResponsableDefecteCodi());
		return "v3/expedient/iniciarPasForm";
	}
	
	@RequestMapping(value = "/iniciarTitol/{expedientTipusId}/{definicioProcesId}", method = RequestMethod.GET)
	public String iniciarTitolGet(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long definicioProcesId,
			Model model) {
		
		definicioProcesToModel(expedientTipusId, definicioProcesId, model);
		
		EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
		ExpedientTipusDto expedientTipus = dissenyService.getExpedientTipusById(expedientTipusId);
		
		if (!model.containsAttribute("command") || model.asMap().get("command") == null)
			model.addAttribute("command", populateCommand(request, expedientTipusId, definicioProcesId, model));
		
		// Si l'expedient no requereix dades inicials però ha de demanar titol i/o número redirigeix al pas per demanar aquestes dades
		ExpedientInicioPasTitolCommand command = new ExpedientInicioPasTitolCommand();
		command.setAny(Calendar.getInstance().get(Calendar.YEAR));
		command.setExpedientTipusId(expedientTipusId);
		command.setNumero(expedientService.getNumeroExpedientActual(entorn.getId(), expedientTipusId, command.getAny()));
		command.setResponsableCodi(expedientTipus.getResponsableDefecteCodi());
		command.setExpedientTipusId(expedientTipus.getId());
		model.addAttribute("anysSeleccionables", getAnysSeleccionables());
		command.setEntornId(entorn.getId());
		model.addAttribute(command);
		model.addAttribute("expedientTipus", expedientTipus);
		return "v3/expedient/iniciarPasTitol";
	}
	
	@RequestMapping(value = "/tasca/{expedientTipusId}/{definicioProcesId}/{tascaId}/formExtern", method = RequestMethod.GET)
	@ResponseBody
	public FormulariExternDto formExtern(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long definicioProcesId,
			@PathVariable String tascaId,
			Model model) {
		
		return tascaService.iniciarFormulariExtern(
				tascaId,
				expedientTipusId,
				definicioProcesId);
		
//		try {
//			FormulariExternDto formExtern = tascaService.iniciarFormulariExtern(
//					tascaId,
//					expedientTipusId,
//					definicioProcesId);
//			String[] resposta = new String[] {
//					formExtern.getUrl(),
//					Integer.toString(formExtern.getWidth()),
//					Integer.toString(formExtern.getHeight())};
//			return resposta;
//		} catch (Exception ex) {
//			logger.error("No s'ha pogut iniciar el formulari extern", ex);
//		}
//		return null;
	}

	@SuppressWarnings("unchecked")
	public static void netejarSessio(HttpServletRequest request) {
		Enumeration<String> atributs = request.getSession().getAttributeNames();
		while (atributs.hasMoreElements()) {
			String atribut = atributs.nextElement();
			if (atribut.startsWith(ExpedientIniciController.CLAU_SESSIO_PREFIX_REGISTRE))
				request.getSession().removeAttribute(atribut);
		}
		request.getSession().removeAttribute(ExpedientIniciController.CLAU_SESSIO_TASKID);
		request.getSession().removeAttribute(ExpedientIniciController.CLAU_SESSIO_NUMERO);
		request.getSession().removeAttribute(ExpedientIniciController.CLAU_SESSIO_TITOL);
		request.getSession().removeAttribute(ExpedientIniciController.CLAU_SESSIO_FORM_VALIDAT);
		request.getSession().removeAttribute(ExpedientIniciController.CLAU_SESSIO_FORM_COMMAND);
		request.getSession().removeAttribute(ExpedientIniciController.CLAU_SESSIO_FORM_VALORS);
	}

	public List<ParellaCodiValorDto> getAnysSeleccionables() {
		List<ParellaCodiValorDto> anys = new ArrayList<ParellaCodiValorDto>();
		int anyActual = Calendar.getInstance().get(Calendar.YEAR);
		for (int i = 0; i < 5; i++) {
			anys.add(new ParellaCodiValorDto(String.valueOf(anyActual - i), anyActual - i));
		}
		return anys;
	}

	private synchronized ExpedientDto iniciarExpedient(Long entornId, Long expedientTipusId, Long definicioProcesId) {
		return expedientService.create(entornId, null, expedientTipusId, definicioProcesId, null, null, null, null, null, null, null, false, null, null, null, null, null, null, false, null, null, false, null, null, IniciadorTipusDto.INTERN, null, null, null, null);
	}

	protected ExpedientTascaDto obtenirTascaInicial(Long entornId, Long expedientTipusId, Long definicioProcesId, Map<String, Object> valors, HttpServletRequest request) {
		ExpedientTascaDto tasca = expedientService.getStartTask(entornId, expedientTipusId, definicioProcesId, valors);
		tasca.setId((String) request.getSession().getAttribute(ExpedientIniciController.CLAU_SESSIO_TASKID));
		Object validat = request.getSession().getAttribute(ExpedientIniciController.CLAU_SESSIO_FORM_VALIDAT);
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

	private static final Log logger = LogFactory.getLog(ExpedientIniciController.class);
}
