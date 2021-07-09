package es.caib.helium.back.controller;

import es.caib.helium.back.helper.MissatgesHelper;
import es.caib.helium.logic.intf.dto.AccioDto;
import es.caib.helium.logic.intf.dto.ExpedientDto;
import es.caib.helium.logic.intf.dto.InstanciaProcesDto;
import es.caib.helium.logic.intf.exception.PermisDenegatException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador per a la pàgina d'accions de l'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedient")
public class ExpedientAccioController extends BaseExpedientController {

	@RequestMapping(value = "/{expedientId}/accio", method = RequestMethod.GET)
	public String accions(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {
		ExpedientDto expedient = expedientService.findAmbIdAmbPermis(expedientId);		
		List<InstanciaProcesDto> arbreProcessos = expedientService.getArbreInstanciesProces(Long.parseLong(expedient.getProcessInstanceId()));
		Map<InstanciaProcesDto, List<AccioDto>> accions = new LinkedHashMap<InstanciaProcesDto, List<AccioDto>>();
		for (InstanciaProcesDto instanciaProces: arbreProcessos) {
			List<AccioDto> accionsTrobades = expedientService.accioFindVisiblesAmbProcessInstanceId(
					expedientId,
					instanciaProces.getId());
			accions.put(instanciaProces, accionsTrobades);
		}
		model.addAttribute("inicialProcesInstanceId", expedient.getProcessInstanceId());		
		model.addAttribute("expedient", expedient);
		model.addAttribute("accions", accions);
		return "v3/expedientAccio";
	}

	@RequestMapping(value = "/{expedientId}/proces/{procesId}/accio", method = RequestMethod.GET)
	public String accionsProces(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String procesId,
			Model model) {
		ExpedientDto expedient = expedientService.findAmbIdAmbPermis(expedientId);
		InstanciaProcesDto instanciaProces = expedientService.getInstanciaProcesById(procesId);
		Map<InstanciaProcesDto, List<AccioDto>> accions = new LinkedHashMap<InstanciaProcesDto, List<AccioDto>>();
		accions.put(
				instanciaProces,
				expedientService.accioFindVisiblesAmbProcessInstanceId(
						expedientId,
						instanciaProces.getId()));
		model.addAttribute("inicialProcesInstanceId", expedient.getProcessInstanceId());
		model.addAttribute("expedient", expedient);
		model.addAttribute("accions", accions);	
		return "v3/procesAccions";
	}

	@RequestMapping(value = "/{expedientId}/proces/{procesId}/accio/{accioId}/executar", method = RequestMethod.GET)
	public String executar(
			HttpServletRequest request,
			@PathVariable Long expedientId, 
			@PathVariable Long accioId, 
			@PathVariable String procesId,
			Model model) {
		try {
			InstanciaProcesDto instanciaProces = expedientService.getInstanciaProcesById(procesId);
			expedientService.accioExecutar(
					expedientId,
					instanciaProces.getId(),
					accioId);
			MissatgesHelper.success(request, getMessage(request, "info.accio.executat"));
		} catch (PermisDenegatException ex) {
			MissatgesHelper.error(
	    			request,
	    			getMessage(request, "error.executar.accio") + ": " + getMessage(request, "error.permisos.modificar.expedient"));
			logger.error(getMessage(request, "error.executar.accio") +" "+ accioId + ": "+ ex.getLocalizedMessage(), ex);
		} catch (Exception ex) {
			String nomAccio = accioId.toString();
			AccioDto accio = expedientService.accioFindAmbId(
					expedientId,
					procesId,
					accioId);
			nomAccio = accio.getNom();
			Throwable t = ExceptionUtils.getRootCause(ex);
			MissatgesHelper.error(
	    			request,
	    			getMessage(request, "error.executar.accio") + " " + nomAccio + ": " + t.getClass().getSimpleName() + ": "+ t.getMessage());
			logger.error(getMessage(request, "error.executar.accio") +" "+ accioId + ": "+ t, ex);
		}
		model.addAttribute("pipellaActiva", "accions");
		return "redirect:/v3/expedient/" + expedientId;
	}

	protected static final Log logger = LogFactory.getLog(ExpedientAccioController.class);

}
