/**
 * 
 */
package es.caib.helium.back.controller;

import es.caib.helium.back.datatables.DatatablesPagina;
import es.caib.helium.back.helper.MissatgesHelper;
import es.caib.helium.back.helper.ObjectTypeEditorHelper;
import es.caib.helium.back.helper.PaginacioHelper;
import es.caib.helium.back.helper.SessionHelper;
import es.caib.helium.back.helper.SessionHelper.SessionManager;
import es.caib.helium.back.helper.TascaFormHelper;
import es.caib.helium.logic.intf.dto.CampTipusDto;
import es.caib.helium.logic.intf.dto.ConsultaDto;
import es.caib.helium.logic.intf.dto.EstatDto;
import es.caib.helium.logic.intf.dto.ExpedientConsultaDissenyDto;
import es.caib.helium.logic.intf.dto.MostrarAnulatsDto;
import es.caib.helium.logic.intf.dto.PaginaDto;
import es.caib.helium.logic.intf.dto.ParellaCodiValorDto;
import es.caib.helium.logic.intf.dto.TascaDadaDto;
import es.caib.helium.logic.intf.service.ExpedientReindexacioService;
import es.caib.helium.logic.intf.util.Constants;
import org.apache.commons.beanutils.PropertyUtils;
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
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Controlador per al llistat d'expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedient/consulta")
public class ExpedientConsultaLlistatController extends BaseExpedientController {

	@Autowired
	ExpedientReindexacioService expedientReindexacioService;

	@ModelAttribute("expedientConsultaCommand")
	public Object getFiltreCommand(
			HttpServletRequest request,
			Long consultaId) {
		if (consultaId == null) 
			return null;
		Object filtreCommand = SessionHelper.getAttribute(
				request,
				SessionHelper.VARIABLE_FILTRE_CONSULTA_TIPUS + consultaId);
		ConsultaDto consulta = dissenyService.findConsulteById(consultaId);
		List<TascaDadaDto> campsFiltre = expedientService.findConsultaFiltre(consultaId);
		if (filtreCommand != null 
				&& TascaFormHelper.commandForCampsValid( filtreCommand, campsFiltre)) {
			return filtreCommand;
		}
		Map<String, Object> campsAddicionals = new HashMap<String, Object>();
		Map<String, Class<?>> campsAddicionalsClasses = new HashMap<String, Class<?>>();
		campsAddicionals.put("consultaId", consultaId);		
		campsAddicionals.put("nomesMeves", false);
		campsAddicionals.put("nomesAlertes", false);
		campsAddicionals.put("mostrarAnulats", false);
		campsAddicionals.put("nomesTasquesPersonals", false);
		campsAddicionals.put("nomesTasquesGrup", false);
		campsAddicionalsClasses.put("nomesMeves", Boolean.class);
		campsAddicionalsClasses.put("nomesAlertes", Boolean.class);
		campsAddicionalsClasses.put("mostrarAnulats", Boolean.class);
		campsAddicionalsClasses.put("consultaId", Long.class);
		campsAddicionalsClasses.put("nomesTasquesPersonals", Boolean.class);
		campsAddicionalsClasses.put("nomesTasquesGrup", Boolean.class);
		return TascaFormHelper.getCommandBuitForCamps(
				campsFiltre,
				campsAddicionals,
				campsAddicionalsClasses,
				consulta.getMapValorsPredefinits().size() > 0 ? consulta.getMapValorsPredefinits() : null,
				true);
	}

	@RequestMapping(value = "/{consultaId}", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long consultaId,
			Model model) {
		ConsultaDto consulta;
		try {
			consulta = dissenyService.findConsulteById(consultaId);			
		} catch (Exception e) {
			MissatgesHelper.error(request, "Error accedint a la consulta amb id " + consultaId + ": " + e.getMessage() + ". Si és la consulta per defecte revisi el seu perfil.");
			return "redirect:/v3/expedient";
		}
		
		model.addAttribute(
				"consulta",
				consulta);
		model.addAttribute(
				"campsFiltre",
				expedientService.findConsultaFiltre(consultaId));	
		model.addAttribute(
				"campsInforme",
				expedientService.findConsultaInforme(consultaId));
		model.addAttribute(
				"campsInformeParams",
				expedientService.findConsultaInformeParams(consultaId));
		List<EstatDto> estats = expedientTipusService.estatFindAll(
				consulta.getExpedientTipus().getId(),
				true);
		estats.add(
				0,
				new EstatDto(
						0L,
						"0",
						getMessage(
								request,
								"expedient.consulta.iniciat")));
		estats.add(
				new EstatDto(
						-1L,
						"-1",
						getMessage(
								request,
								"expedient.consulta.finalitzat")));
		model.addAttribute(
				"estats",
				estats);
		Object filtreCommand = getFiltreCommand(request, consultaId);
		SessionHelper.setAttribute(
				request,
				SessionHelper.VARIABLE_FILTRE_CONSULTA_TIPUS + consultaId,
				filtreCommand);
		model.addAttribute("expedientConsultaCommand", filtreCommand);
		
		return "v3/expedientConsultaLlistat";
	}

	@RequestMapping(value = "/{consultaId}", method = RequestMethod.POST)
	public String post(
			HttpServletRequest request,
			@PathVariable Long consultaId,
			@Valid @ModelAttribute("expedientConsultaCommand") Object filtreCommand,			
			BindingResult bindingResult,
			@RequestParam(value = "accio", required = false) String accio,
			Model model) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException  {
		if ("netejar".equals(accio)) {
			SessionHelper.removeAttribute(request, SessionHelper.VARIABLE_FILTRE_CONSULTA_TIPUS + consultaId);
			filtreCommand = getFiltreCommand(request, consultaId);
		}
		SessionHelper.setAttribute(
				request,
				SessionHelper.VARIABLE_FILTRE_CONSULTA_TIPUS + consultaId,
				filtreCommand);
		return "redirect:/v3/expedient/consulta/" + consultaId;
	}

	@RequestMapping(value = "/{consultaId}/datatable")
	@ResponseBody
	public DatatablesPagina<ExpedientConsultaDissenyDto> datatable(
			HttpServletRequest request,
			@PathVariable Long consultaId,
			Model model) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {

		Object filtreCommand = SessionHelper.getAttribute(
				request,
				SessionHelper.VARIABLE_FILTRE_CONSULTA_TIPUS + consultaId);
		List<TascaDadaDto> campsFiltre = expedientService.findConsultaFiltre(consultaId);
		Map<String, Object> filtreValors = TascaFormHelper.getValorsFromCommand(
				campsFiltre,
				filtreCommand,
				true);
		PaginaDto<ExpedientConsultaDissenyDto> paginaExpedients = expedientService.consultaFindPaginat(
				consultaId,
				processarValorsFiltre(filtreCommand, campsFiltre, filtreValors),
				null,
				(Boolean)PropertyUtils.getSimpleProperty(filtreCommand, "nomesTasquesPersonals"),
				(Boolean)PropertyUtils.getSimpleProperty(filtreCommand, "nomesTasquesGrup"),
				(Boolean)PropertyUtils.getSimpleProperty(filtreCommand, "nomesMeves"),
				(Boolean)PropertyUtils.getSimpleProperty(filtreCommand, "nomesAlertes"),
				false, //nomesErrors
				MostrarAnulatsDto.NO, //mostrarAnulats
				PaginacioHelper.getPaginacioDtoFromDatatable(request));
		SessionHelper.setAttribute(
				request,
				SessionHelper.VARIABLE_SESSIO_COMMAND_VALUES + consultaId,
				filtreValors);
		return PaginacioHelper.getPaginaPerDatatables(
				request,
				paginaExpedients);
	}

	@RequestMapping(value = "/{consultaId}/selection", method = RequestMethod.POST)
	@ResponseBody
	public Set<Long> selection(
			HttpServletRequest request,
			@PathVariable Long consultaId,
			@RequestParam(value = "ids", required = false) String ids) {
		SessionManager sessionManager = SessionHelper.getSessionManager(request);
		Set<Long> seleccio = sessionManager.getSeleccioInforme(consultaId);
		if (seleccio == null) {
			seleccio = new HashSet<Long>();
			sessionManager.setSeleccioInforme(seleccio, consultaId);
		}
		if (ids != null) {
			String[] idsparts = (ids.contains(",")) ? ids.split(",") : new String[] {ids};
			for (String id: idsparts) {
				try {
					long l = Long.parseLong(id.trim());
					if (l >= 0) {
						seleccio.add(l);
					} else {
						seleccio.remove(-l);
					}
				} catch (NumberFormatException ex) {}
			}
		}
		return seleccio;
	}

	@RequestMapping(value = "/{consultaId}/selectionAll")
	@ResponseBody
	public Set<Long> selectionAll(
			HttpServletRequest request,
			@PathVariable Long consultaId) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException  {
		Object filtreCommand = getFiltreCommand(request, consultaId);
		List<TascaDadaDto> dadesFiltre = expedientService.findConsultaFiltre(consultaId);
		Map<String, Object> filtreValors = TascaFormHelper.getValorsFromCommand(
				dadesFiltre,
				filtreCommand,
				true);
		PaginaDto<Long> paginaIds = expedientService.consultaFindNomesIdsPaginat(
				consultaId,
				processarValorsFiltre(filtreCommand, dadesFiltre, filtreValors),
				(Boolean)PropertyUtils.getSimpleProperty(filtreCommand, "nomesTasquesPersonals"),
				(Boolean)PropertyUtils.getSimpleProperty(filtreCommand, "nomesTasquesGrup"),
				(Boolean)PropertyUtils.getSimpleProperty(filtreCommand, "nomesMeves"),
				(Boolean)PropertyUtils.getSimpleProperty(filtreCommand, "nomesAlertes"),
				false, //nomesErrors
				MostrarAnulatsDto.NO, //mostrarAnulats
				PaginacioHelper.getPaginacioDtoTotsElsResultats());
		List<Long> ids = paginaIds.getContingut();
		SessionManager sessionManager = SessionHelper.getSessionManager(request);
		Set<Long> seleccio = sessionManager.getSeleccioInforme(consultaId);
		if (seleccio == null) {
			seleccio = new HashSet<Long>();
			sessionManager.setSeleccioInforme(seleccio, consultaId);
		}
		seleccio.clear();
		seleccio.addAll(ids);
		return seleccio;
	}

	@RequestMapping(value = "/{consultaId}/selectionNone")
	@ResponseBody
	public Set<Long> seleccioNetejar(
			HttpServletRequest request,
			@PathVariable Long consultaId) {
		SessionManager sessionManager = SessionHelper.getSessionManager(request);
		Set<Long> ids = sessionManager.getSeleccioInforme(consultaId);
		ids.clear();
		return ids;
	}

	@ModelAttribute("listTerminis")
	public List<ParellaCodiValorDto> valors12(HttpServletRequest request) {
		List<ParellaCodiValorDto> resposta = new ArrayList<ParellaCodiValorDto>();
		for (int i = 0; i <= 12; i++) {
			resposta.add(
					new ParellaCodiValorDto(
							String.valueOf(i),
							i));
		}
		return resposta;
	}
	@ModelAttribute("valorsBoolea")
	public List<ParellaCodiValorDto> valorsBoolea(HttpServletRequest request) {
		List<ParellaCodiValorDto> resposta = new ArrayList<ParellaCodiValorDto>();
		resposta.add(new ParellaCodiValorDto("true", getMessage(request, "comuns.si")));
		resposta.add(new ParellaCodiValorDto("false", getMessage(request, "comuns.no")));
		return resposta;
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.setAutoGrowNestedPaths(false);
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


	/** Mètode per consultar via Ajax el contingut de les alertes per si el tipus 
	 * d'expedient de la consutla conté expedients pendents de reindexar o amb errors
	 * de reindexació. Retorna el contingut per pintar l'HTML de les alertes de reindexació
	 * en la pàgina de consultes.
	 * Les alertes poden carregar la llista d'expedients amb error o pendents de reindexació
	 * via ajax a partir del ReindexacioController
	 * @param request
	 * @param consultaId
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/{consultaId}/alertes")	
	public String alertes(
			HttpServletRequest request,
			@PathVariable Long consultaId,
			Model model) {

		ConsultaDto consulta = dissenyService.findConsulteById(consultaId);

		// Consulta si hi ha expedients amb error de reindexació o pendents de reindexar
		Long errorsReindexacio = expedientReindexacioService.consultaCountErrorsReindexacio(consulta.getExpedientTipus().getId());
		Long pendentsReindexacio = expedientReindexacioService.consultaCountPendentsReindexacio(consulta.getExpedientTipus().getId());
		model.addAttribute("tipusId", consulta.getExpedientTipus().getId());
		model.addAttribute("errorsReindexacio", errorsReindexacio);
		model.addAttribute("pendentsReindexacio", pendentsReindexacio);

		return "v3/expedientConsultaAlertes";
	}
	
	private Map<String, Object> processarValorsFiltre(
			Object filtreCommand,
			List<TascaDadaDto> dadesFiltre,
			Map<String, Object> valors) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Map<String, Object> valorsPerService = new HashMap<String, Object>();
		for (TascaDadaDto dada: dadesFiltre) {
			String clau = (dada.getDefinicioProcesKey() == null) ? dada.getVarCodi() : dada.getDefinicioProcesKey() + "." + dada.getVarCodi();
			clau = clau.replace(
					Constants.EXPEDIENT_PREFIX_JSP,
					Constants.EXPEDIENT_PREFIX);
			if (CampTipusDto.BOOLEAN.equals(dada.getCampTipus()) && PropertyUtils.isReadable(filtreCommand, dada.getVarCodi())) {
				Boolean valor = (Boolean) PropertyUtils.getSimpleProperty(
						filtreCommand,
						dada.getVarCodi());
				valors.put(
						dada.getVarCodi(),
						valor);
			}
			valorsPerService.put(
					clau,
					valors.get(dada.getVarCodi()));
		}
		return valorsPerService;
	}
}
