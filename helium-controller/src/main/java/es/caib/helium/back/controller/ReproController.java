/**
 * 
 */
package es.caib.helium.back.controller;

import es.caib.helium.back.helper.MissatgesHelper;
import es.caib.helium.back.helper.ObjectTypeEditorHelper;
import es.caib.helium.back.helper.SessionHelper;
import es.caib.helium.back.helper.TascaFormHelper;
import es.caib.helium.logic.intf.dto.EntornDto;
import es.caib.helium.logic.intf.dto.ExpedientTascaDto;
import es.caib.helium.logic.intf.dto.ExpedientTipusDto;
import es.caib.helium.logic.intf.dto.ReproDto;
import es.caib.helium.logic.intf.dto.TascaDadaDto;
import es.caib.helium.logic.intf.exception.NoTrobatException;
import es.caib.helium.logic.intf.service.DissenyService;
import es.caib.helium.logic.intf.service.ExpedientService;
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
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador per Repros
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/repro")
public class ReproController extends BaseController {
	
	@Autowired
	private ReproService reproService;
	@Autowired
	protected ExpedientService expedientService;
	@Autowired
	protected DissenyService dissenyService;
	@Autowired
	protected TascaService tascaService;
	
	@SuppressWarnings("unchecked")
	@ModelAttribute("command")
	protected Object populateCommand(
			HttpServletRequest request,
			@RequestParam(value = "tascaId", required = false) Long tascaId,
			@PathVariable Long expedientTipusId,
			@PathVariable Long definicioProcesId,
			Model model) {
		try {
			Map<String, Object> campsAddicionals = new HashMap<String, Object>();
			Map<String, Class<?>> campsAddicionalsClasses = new HashMap<String, Class<?>>();
			EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
			ExpedientTascaDto tasca;
			if(tascaId != null) {				
				tasca = this.tascaService.findAmbIdPerTramitacio(tascaId.toString());
			}else{
				tasca = obtenirTascaInicial(
					entorn.getId(),
					expedientTipusId,
					definicioProcesId,
					new HashMap<String, Object>(),
					request);
			}
			
			campsAddicionals.put("id", tasca.getId());
			campsAddicionals.put("entornId", entorn.getId());
			campsAddicionals.put("expedientTipusId", expedientTipusId);
			campsAddicionals.put("definicioProcesId", definicioProcesId);
			campsAddicionals.put("tascaId", (tascaId != null)?tascaId.toString():null);
			campsAddicionalsClasses.put("id", String.class);
			campsAddicionalsClasses.put("entornId", Long.class);
			campsAddicionalsClasses.put("expedientTipusId", Long.class);
			campsAddicionalsClasses.put("definicioProcesId", Long.class);
			campsAddicionalsClasses.put("tascaId", String.class);
			Map<String, Object> valorsFormulariExtern = null;
			if (tasca.isFormExtern()) {
				valorsFormulariExtern = tascaService.obtenirValorsFormulariExternInicial(tasca.getId());
				if (valorsFormulariExtern != null) {
					request.getSession().setAttribute(
							BaseExpedientIniciController.CLAU_SESSIO_FORM_VALORS,
							valorsFormulariExtern);
				} else {
					valorsFormulariExtern = (Map<String, Object>)request.getSession().getAttribute(
							BaseExpedientIniciController.CLAU_SESSIO_FORM_VALORS);
				}
			}
			return TascaFormHelper.getCommandForCamps(
					tascaService.findDadesPerTascaDto(expedientTipusId, tasca),
					valorsFormulariExtern,
					campsAddicionals,
					campsAddicionalsClasses,
					false);
		} catch (NoTrobatException ex) {
			MissatgesHelper.error(request, ex.getMessage());
			logger.error("No s'han pogut encontrar la tasca: " + ex.getMessage(), ex);
		}
		return null;
	}

	@RequestMapping(value = "/{expedientTipusId}/{definicioProcesId}/guardarRepro", method = RequestMethod.POST)
	public String guardarRepro(
			HttpServletRequest request, 
			@RequestParam(value = "nomRepro", required = true) String nomRepro,
			@RequestParam(value = "tascaId", required = false) Long tascaTipusId,
			@PathVariable Long expedientTipusId,
			@PathVariable Long definicioProcesId,
			@ModelAttribute("command") Object command,
			BindingResult result,
			SessionStatus status,
			Model model) {
		EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
		ExpedientTipusDto expedientTipus = dissenyService.getExpedientTipusById(expedientTipusId);
		ReproDto repro = null;
		try {
			ExpedientTascaDto tasca = obtenirTascaInicial(entorn.getId(), expedientTipusId, definicioProcesId, new HashMap<String, Object>(), request);
			List<TascaDadaDto> tascaDades = tascaService.findDadesPerTascaDto(expedientTipusId, tasca);
			Map<String, Object> valors = TascaFormHelper.getValorsFromCommand(
					tascaDades,
					command,
					false,
					true);
			repro = reproService.createTasca(expedientTipus.getId(), tasca.getTascaId(), nomRepro, valors);
			MissatgesHelper.success(request, getMessage(request, "repro.missatge.repro") + " '" + nomRepro + "' " + getMessage(request, "repro.missatge.creat"));
		} catch (Exception ex) {
			MissatgesHelper.error(
					request, 
					getMessage(request, "repro.missatge.repro") + 
					" " + nomRepro + " " + 
					getMessage(request, "repro.missatge.error.creat") +
					": " + ex.getMessage());
		}
		
		String referer = request.getHeader("Referer");
		UriComponents uriComponents = UriComponentsBuilder.fromUriString(referer).replaceQueryParam("reproId", repro != null ? repro.getId() : "").build();
		return "redirect:" + uriComponents.toUriString();
//	    return "redirect:"+ UriBuilder.fromUri(referer).replaceQueryParam("reproId", repro != null ? repro.getId() : "").build();
	}
	
	@RequestMapping(value = "/{expedientTipusId}/{definicioProcesId}/borrarRepro/{reproIdEsborrar}", method = RequestMethod.POST)
	public String deleteRepro(
			HttpServletRequest request, 
			@PathVariable Long expedientTipusId,
			@PathVariable Long definicioProcesId,
			@PathVariable Long reproIdEsborrar,
			@ModelAttribute("command") Object command,
			BindingResult result,
			SessionStatus status,
			Model model) {
				
		try {
			String nomRepro = reproService.deleteById(reproIdEsborrar);
			MissatgesHelper.success(request, getMessage(request, "repro.missatge.repro") + " '" + nomRepro + "' " + getMessage(request, "repro.missatge.eliminat"));
		} catch (Exception ex) {
			MissatgesHelper.error(request, getMessage(request, "repro.missatge.error.eliminat"));
		}
		String referer = request.getHeader("Referer");
		UriComponents uriComponents = UriComponentsBuilder.fromUriString(referer).replaceQueryParam("reproId", "").build();
		return "redirect:" + uriComponents.toUriString();
//	    return "redirect:"+ UriBuilder.fromUri(referer).replaceQueryParam("reproId", "").build();
	}
	
	@RequestMapping(value = "/{expedientTipusId}/{definicioProcesId}/deleteRepro/{reproId}", method = RequestMethod.POST)
	public String deleteReproTasca(
			HttpServletRequest request, 
			@PathVariable Long expedientTipusId,
			@PathVariable Long definicioProcesId,
			@PathVariable Long reproId,
			@ModelAttribute("command") Object command,
			BindingResult result,
			SessionStatus status,
			Model model) {
		try {
			String nomRepro = reproService.deleteById(reproId);
			MissatgesHelper.success(request, getMessage(request, "repro.missatge.repro") + " '" + nomRepro + "' " + getMessage(request, "repro.missatge.eliminat"));
		} catch (Exception ex) {
			MissatgesHelper.error(request, getMessage(request, "repro.missatge.error.eliminat"));
		}
		
		String referer = request.getHeader("Referer");
		UriComponents uriComponents = UriComponentsBuilder.fromUriString(referer).replaceQueryParam("reproId", "").build();
		return "redirect:" + uriComponents.toUriString();
//	    return "redirect:"+ UriBuilder.fromUri(referer).replaceQueryParam("reproId", "").build();
	}
	
	private ExpedientTascaDto obtenirTascaInicial(Long entornId, Long expedientTipusId, Long definicioProcesId, Map<String, Object> valors, HttpServletRequest request) {
		ExpedientTascaDto tasca = expedientService.getStartTask(entornId, expedientTipusId, definicioProcesId, valors);
		tasca.setId((String) request.getSession().getAttribute(BaseExpedientIniciController.CLAU_SESSIO_TASKID));
		Object validat = request.getSession().getAttribute(BaseExpedientIniciController.CLAU_SESSIO_FORM_VALIDAT);
		tasca.setValidada(validat != null);
		return tasca;
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
	
	@RequestMapping(value = "/{expedientTipusId}/{definicioProcesId}/saveRepro", method = RequestMethod.POST)
	public String guardarReproTasca(
			HttpServletRequest request,
			@RequestParam(value = "nomRepro", required = true) String nomRepro, 
			@RequestParam(value = "tascaId", required = false) Long tascaTipusId,
			@PathVariable Long expedientTipusId,
			@PathVariable Long definicioProcesId,
			@ModelAttribute("command") Object command,
			BindingResult result,
			SessionStatus status,
			Model model) {
		ExpedientTipusDto expedientTipus = dissenyService.getExpedientTipusById(expedientTipusId);
		
		ReproDto repro = null;
		ExpedientTascaDto tasca = null;
		try {
			tasca = tascaService.findAmbIdPerTramitacio(tascaTipusId.toString());
			List<TascaDadaDto> tascaDades = tascaService.findDadesPerTascaDto(expedientTipus.getId(), tasca);
			
			Map<String, Object> valors = TascaFormHelper.getValorsFromCommand(
					tascaDades,
					command,
					false,
					true);
			
			repro = reproService.createTasca(expedientTipus.getId(), tasca.getTascaId(), nomRepro, valors);
			MissatgesHelper.success(request, getMessage(request, "repro.missatge.repro") + " '" + nomRepro + "' " + getMessage(request, "repro.missatge.creat"));
		} catch (Exception ex) {
			MissatgesHelper.error(
					request, 
					getMessage(request, "repro.missatge.repro") + 
					" " + nomRepro + " " + 
					getMessage(request, "repro.missatge.error.creat") +
					": " + ex.getMessage());
		}
		String referer = request.getHeader("Referer");
		UriComponents uriComponents = UriComponentsBuilder.fromUriString(referer).replaceQueryParam("reproId", repro != null? repro.getId() : "").build();
		return "redirect:" + uriComponents.toUriString();
//	    return "redirect:"+ UriBuilder.fromUri(referer).replaceQueryParam("reproId", repro != null? repro.getId() : "").build();
	}
	
	private static final Log logger = LogFactory.getLog(ReproController.class);
}
