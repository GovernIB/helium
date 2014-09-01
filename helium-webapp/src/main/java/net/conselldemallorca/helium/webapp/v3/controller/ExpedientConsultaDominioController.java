/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.v3.core.api.dto.ParellaCodiValorDto;
import net.conselldemallorca.helium.v3.core.api.dto.SeleccioOpcioDto;
import net.conselldemallorca.helium.v3.core.api.service.DissenyService;
import net.conselldemallorca.helium.v3.core.api.service.TascaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controlador per fer consultes als dominis de dins d'un expedient
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/domini")
public class ExpedientConsultaDominioController extends BaseExpedientController {
	@Autowired
	private TascaService tascaService;
	@Autowired
	private DissenyService dissenyService;

	@RequestMapping(value = "/consulta", method = RequestMethod.GET)
	@ResponseBody
	public List<ParellaCodiValorDto> consultaCamp(
			HttpServletRequest request,
			@RequestParam(value = "taskId", required = false) String taskId,
			@RequestParam(value = "processInstanceId", required = false) String processInstanceId,
			@RequestParam(value = "campId", required = true) Long campId,
			@RequestParam(value = "q", required = false) String textInicial,
			@RequestParam(value = "valors", required = false) String valors,
			ModelMap model) {
		List<ParellaCodiValorDto> resultat = new ArrayList<ParellaCodiValorDto>();
		List<SeleccioOpcioDto> opcions = tascaService.findllistaValorsPerCampDesplegable(
				taskId,
				campId,
				textInicial,
				getMapDelsValors(valors));
		for (SeleccioOpcioDto opcio: opcions) {
			resultat.add(
					new ParellaCodiValorDto(
							opcio.getCodi(),
							opcio.getText().replaceAll("\\p{Cntrl}", "").trim()));
		}
		return resultat;
	}

	private Map<String, Object> getMapDelsValors(String valors) {
		if (valors == null)
			return null;
		Map<String, Object> resposta = new HashMap<String, Object>();
		String[] parelles = valors.split(",");
		for (int i = 0; i < parelles.length; i++) {
			String[] parts = parelles[i].split(":");
			if (parts.length == 2)
				resposta.put(parts[0], parts[1]);
		}
		return resposta;
	}
	
	@RequestMapping(value = "/consulta/inicial/{taskId}/{campId}/{codi}", method = RequestMethod.GET)
	@ResponseBody
	public ParellaCodiValorDto consultaCampInicial(
			HttpServletRequest request,
			@PathVariable(value = "taskId") String taskId,
			@PathVariable(value = "campId") Long campId,
			@PathVariable(value = "codi") String codi,
			ModelMap model) {
		List<ParellaCodiValorDto> resultat = new ArrayList<ParellaCodiValorDto>();
		List<SeleccioOpcioDto> opcions = tascaService.findllistaValorsPerCampDesplegable(
				taskId,
				campId,
				null,
				null);
		for (SeleccioOpcioDto opcio: opcions) {
			resultat.add(
					new ParellaCodiValorDto(
							opcio.getCodi(),
							opcio.getText().replaceAll("\\p{Cntrl}", "").trim()));
		}
		if (resultat.isEmpty())
			return new ParellaCodiValorDto();
		return resultat.get(0);
	}
	
	@RequestMapping(value = "/consulta/{taskId}/{campId}/{valor}", method = RequestMethod.GET)
	@ResponseBody
	public List<ParellaCodiValorDto> consultaCampValor(
			HttpServletRequest request,
			@RequestParam(value = "taskId", required = false) String taskId,
			@RequestParam(value = "campId", required = true) Long campId,
			@RequestParam(value = "valor", required = false) String textInicial,
			ModelMap model) {
		List<ParellaCodiValorDto> resultat = new ArrayList<ParellaCodiValorDto>();
		List<SeleccioOpcioDto> opcions = tascaService.findllistaValorsPerCampDesplegable(
				taskId,
				campId,
				textInicial,
				null);
		for (SeleccioOpcioDto opcio: opcions) {
			resultat.add(
					new ParellaCodiValorDto(
							opcio.getCodi(),
							opcio.getText().replaceAll("\\p{Cntrl}", "").trim()));
		}
		return resultat;
	}

}
