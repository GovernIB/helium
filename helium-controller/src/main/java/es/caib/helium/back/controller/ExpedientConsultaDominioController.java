/**
 * 
 */
package es.caib.helium.back.controller;

import es.caib.helium.logic.intf.dto.SeleccioOpcioDto;
import es.caib.helium.logic.intf.service.TascaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador per fer consultes als dominis de dins d'un expedient.
 * 
 * Emprat pels camps de tipus suggest dels formularis de les tasques
 * i dels filtres de consultes per tipus.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/domini")
public class ExpedientConsultaDominioController extends BaseExpedientController {
	@Autowired
	private TascaService tascaService;

	@RequestMapping(value = "/consulta", method = RequestMethod.GET)
	@ResponseBody
	public List<SeleccioOpcioDto> consultaCamp(
			HttpServletRequest request,
			@RequestParam(value = "taskId", required = false) String taskId,
			@RequestParam(value = "processInstanceId", required = false) String processInstanceId,
			@RequestParam(value = "campId", required = true) Long campId,
			@RequestParam(value = "q", required = false) String textInicial,
			@RequestParam(value = "valors", required = false) String valors,
			ModelMap model) {
		return tascaService.findValorsPerCampDesplegable(
				taskId,
				null,
				campId,
				null,
				textInicial,
				null,
				null,
				getMapDelsValors(valors));
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
	public SeleccioOpcioDto consultaCampInicial(
			@PathVariable(value = "taskId") String taskId,
			@PathVariable(value = "campId") Long campId,
			@PathVariable(value = "codi") String codi,
			ModelMap model) {
		List<SeleccioOpcioDto> opcions = tascaService.findValorsPerCampDesplegable(
				taskId,
				null,
				campId,
				null,
				null,
				null,
				null,
				null);
		for (SeleccioOpcioDto sel : opcions) {
			if (sel.getCodi().equals(codi)) {
				return sel;
			}
		}
		return new SeleccioOpcioDto();
	}

	@RequestMapping(value = "/consulta/{taskId}/{campId}/{valor}", method = RequestMethod.GET)
	@ResponseBody
	public List<SeleccioOpcioDto> consultaCampValor(
			@PathVariable (value = "taskId") String taskId,
			@PathVariable (value = "campId") Long campId,
			@PathVariable (value = "valor") String textInicial,
			ModelMap model) {
		return tascaService.findValorsPerCampDesplegable(
				taskId,
				null,
				campId,
				null,
				textInicial,
				null,
				null,
				null);
	}

	@RequestMapping(value = "/consulta/inicial/{campId}/{codi}", method = RequestMethod.GET)
	@ResponseBody
	public SeleccioOpcioDto consultaCampInicial(
			@PathVariable (value = "campId") Long campId,
			@PathVariable (value = "codi") String codi,
			ModelMap model) {
		List<SeleccioOpcioDto> opcions = tascaService.findValorsPerCampDesplegable(
				null,
				null,
				campId,
				codi,
				null,
				null,
				null,
				null);
		for (SeleccioOpcioDto sel : opcions) {
			if (sel.getCodi().equals(codi)) {
				return sel;
			}
		}
		return new SeleccioOpcioDto();
	}

	@RequestMapping(value = "/consulta/{campId}/{valor}", method = RequestMethod.GET)
	@ResponseBody
	public List<SeleccioOpcioDto> consultaCampValor(
			@PathVariable (value = "campId") Long campId,
			@PathVariable (value = "valor") String valor,
			ModelMap model) {
		return tascaService.findValorsPerCampDesplegable(
				null,
				null,
				campId,
				null,
				valor,
				null,
				null,
				null);
	}

}