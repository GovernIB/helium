package net.conselldemallorca.helium.webapp.v3.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import net.conselldemallorca.helium.core.util.EntornActual;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusEstadisticaDto;
import net.conselldemallorca.helium.v3.core.api.dto.MostrarAnulatsDto;
import net.conselldemallorca.helium.v3.core.api.dto.ParellaCodiValorDto;
import net.conselldemallorca.helium.v3.core.api.service.DissenyService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientTipusService;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusEstadisticaCommand;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;

/**
 * Controlador per al manteniment d'entorns.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller(value = "entornEstadisticaControllerV3")
@RequestMapping("/v3/estadistica")
public class ExpedientTipusEstadisticaController extends BaseController {

	@Resource
	private ExpedientTipusService expedientTipusService;
	@Autowired
	protected DissenyService dissenyService;


	@RequestMapping(method = RequestMethod.GET)
	public String llistat(
			HttpServletRequest request,
			Model model) {
		model.addAttribute(new ExpedientTipusEstadisticaCommand());		
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		List<ExpedientTipusDto> expedientsTipus = expedientTipusService.findAmbEntornPermisDissenyar(entornActual.getId());
		model.addAttribute("expedientsTipus", expedientsTipus);
		return "v3/estadisticaEntorns";
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String form(
			HttpServletRequest request,
			ExpedientTipusEstadisticaCommand filtreCommand,
			BindingResult bindingResult,
			@RequestParam(value = "accio", required = false) String accio,
			Model model) {
		
		if ("netejar".equals(accio))
			filtreCommand = new ExpedientTipusEstadisticaCommand();
		
		Boolean anulats = null;
		if(filtreCommand.getMostrarAnulats().equals(MostrarAnulatsDto.NOMES_ANULATS))
			anulats = true;
		if(filtreCommand.getMostrarAnulats().equals(MostrarAnulatsDto.NO))
			anulats = false;
		List<ExpedientTipusEstadisticaDto> et = expedientTipusService.findEstadisticaByFiltre(
																				filtreCommand.getAnyInicial(), 
																				filtreCommand.getAnyFinal(), 
																				EntornActual.getEntornId(), 
																				filtreCommand.getExpedientTipusId(), 
																				anulats);
		TreeSet<String> anys = new TreeSet<String>();
		Map<String, String> titols = new HashMap<String, String>();  
		Map<String, Map<String, Object>> ete = new TreeMap<String, Map<String, Object>>();
		Map<String, Long> totalTipus = new  HashMap<String, Long>();
		for(ExpedientTipusEstadisticaDto element : et) {
			String nom = element.getCodi();
			if(!titols.containsKey(nom))
				titols.put(nom, element.getNom());
			if(!ete.containsKey(nom))
				ete.put(nom, new TreeMap<String,Object>());
			if(!anys.contains(element.getAnyInici()))
				anys.add(element.getAnyInici());
			if(!totalTipus.containsKey(nom)) {				
				totalTipus.put(nom, element.getN());
			}else {
				totalTipus.put(nom, totalTipus.get(nom) + element.getN());
			}
			ete.get(nom).put(element.getAnyInici(), element.getN());
		}
		
		//List<String> anys = generateSetOfAnys(filtreCommand.getDataIniciInicial(), filtreCommand.getDataIniciFinal());
		Map<String, Object> totalAny =  totalPerAny(ete);
		request.setAttribute("tableData", ete);
		request.setAttribute("totalTipus", totalTipus);
		request.setAttribute("anys", anys);
		request.setAttribute("totalAny", totalAny);
		request.setAttribute("titols", titols);
		
		// Torna a posar el command al model
		model.addAttribute(filtreCommand);
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		List<ExpedientTipusDto> expedientsTipus = expedientTipusService.findAmbEntorn(entornActual.getId());
		model.addAttribute("expedientsTipus", expedientsTipus);

		return "v3/estadisticaEntorns";
	}
	
	private Map<String, Object> totalPerAny(Map<String, Map<String, Object>> ete){
		Map<String, Object> totalAny = new HashMap<String, Object>();
		for(Map.Entry<String, Map<String, Object>> entry : ete.entrySet()) {
			for(Map.Entry<String, Object> any : entry.getValue().entrySet()) {
				
				if(!totalAny.containsKey(any.getKey())) {
					totalAny.put(any.getKey(), any.getValue());
				}else {
					totalAny.put(any.getKey(), ((Long)totalAny.get(any.getKey()) + ((Long) any.getValue())));
				}
			}
		}
		
		return totalAny;
	}
				
	@ModelAttribute("anulats")
	public List<ParellaCodiValorDto> populateEstats(HttpServletRequest request) {
		List<ParellaCodiValorDto> resposta = new ArrayList<ParellaCodiValorDto>();
		resposta.add(new ParellaCodiValorDto(getMessage(request, "enum.no"), MostrarAnulatsDto.NO));
		resposta.add(new ParellaCodiValorDto(getMessage(request, "enum.si"), MostrarAnulatsDto.SI));
		resposta.add(new ParellaCodiValorDto(getMessage(request, "enum.si.only"), MostrarAnulatsDto.NOMES_ANULATS));
		return resposta;
	}
}
