/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

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
 * Controlador per a consultar els valors possibles d'un camp
 * desplegable de l'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/camp")
public class CampValorsController extends BaseExpedientController {

	@Autowired
	private TascaService tascaService;
	@Autowired
	private DissenyService dissenyService;



	@RequestMapping(value = "/{campId}/context/{processInstanceId}/{tascaId}/valors", method = RequestMethod.GET)
	@ResponseBody
	public List<SeleccioOpcioDto> consultaAmbContext(
			HttpServletRequest request,
			@PathVariable(value = "campId") Long campId,
			@PathVariable(value = "processInstanceId") String processInstanceId,
			@PathVariable(value = "tascaId") String tascaId,
			@RequestParam(value = "q", required = false) String textFiltre,
			@RequestParam(value = "valors", required = false) String valors,
			ModelMap model) {
		return tascaService.findValorsPerCampDesplegable(
				tascaId,
				processInstanceId,
				campId,
				null,
				textFiltre,
				getMapDelsValors(valors));
	}
	@RequestMapping(value = "/{campId}/proces/{processInstanceId}/valors", method = RequestMethod.GET)
	@ResponseBody
	public List<SeleccioOpcioDto> consultaAmbProces(
			HttpServletRequest request,
			@PathVariable(value = "campId") Long campId,
			@PathVariable(value = "processInstanceId") String processInstanceId,
			@RequestParam(value = "q", required = false) String textFiltre,
			@RequestParam(value = "valors", required = false) String valors,
			ModelMap model) {
		return tascaService.findValorsPerCampDesplegable(
				null,
				processInstanceId,
				campId,
				null,
				textFiltre,
				getMapDelsValors(valors));
	}
	@RequestMapping(value = "/{campId}/tasca/{tascaId}/valors", method = RequestMethod.GET)
	@ResponseBody
	public List<SeleccioOpcioDto> consultaAmbTasca(
			HttpServletRequest request,
			@PathVariable(value = "campId") Long campId,
			@PathVariable(value = "tascaId") String tascaId,
			@RequestParam(value = "q", required = false) String textFiltre,
			@RequestParam(value = "valors", required = false) String valors,
			ModelMap model) {
		return tascaService.findValorsPerCampDesplegable(
				tascaId,
				null,
				campId,
				null,
				textFiltre,
				getMapDelsValors(valors));
	}
	@RequestMapping(value = "/{campId}/valors", method = RequestMethod.GET)
	@ResponseBody
	public List<SeleccioOpcioDto> consulta(
			HttpServletRequest request,
			@PathVariable(value = "campId") Long campId,
			@RequestParam(value = "q", required = false) String textFiltre,
			@RequestParam(value = "valors", required = false) String valors,
			ModelMap model) {
		return tascaService.findValorsPerCampDesplegable(
				null,
				null,
				campId,
				null,
				textFiltre,
				getMapDelsValors(valors));
	}

	@RequestMapping(value = "/{campId}/context/{processInstanceId}/{tascaId}/valor/{valor}", method = RequestMethod.GET)
	@ResponseBody
	public List<SeleccioOpcioDto> consultaValorContext(
			HttpServletRequest request,
			@PathVariable(value = "campId") Long campId,
			@PathVariable(value = "processInstanceId") String processInstanceId,
			@PathVariable(value = "tascaId") String tascaId,
			@PathVariable(value = "valor") String valor,
			@RequestParam(value = "q", required = false) String textFiltre,
			@RequestParam(value = "valors", required = false) String valors,
			ModelMap model) {
		return tascaService.findValorsPerCampDesplegable(
				tascaId,
				processInstanceId,
				campId,
				valor,
				textFiltre,
				getMapDelsValors(valors));
	}
	@RequestMapping(value = "/{campId}/proces/{processInstanceId}/valor/{valor}", method = RequestMethod.GET)
	@ResponseBody
	public List<SeleccioOpcioDto> consultaValorProces(
			HttpServletRequest request,
			@PathVariable(value = "campId") Long campId,
			@PathVariable(value = "processInstanceId") String processInstanceId,
			@PathVariable(value = "valor") String valor,
			@RequestParam(value = "q", required = false) String textFiltre,
			@RequestParam(value = "valors", required = false) String valors,
			ModelMap model) {
		return tascaService.findValorsPerCampDesplegable(
				null,
				processInstanceId,
				campId,
				valor,
				textFiltre,
				getMapDelsValors(valors));
	}
	@RequestMapping(value = "/{campId}/tasca/{tascaId}/valor/{valor}", method = RequestMethod.GET)
	@ResponseBody
	public List<SeleccioOpcioDto> consultaValorTasca(
			HttpServletRequest request,
			@PathVariable(value = "campId") Long campId,
			@PathVariable(value = "tascaId") String tascaId,
			@PathVariable(value = "valor") String valor,
			@RequestParam(value = "q", required = false) String textFiltre,
			@RequestParam(value = "valors", required = false) String valors,
			ModelMap model) {
		return tascaService.findValorsPerCampDesplegable(
				tascaId,
				null,
				campId,
				valor,
				textFiltre,
				getMapDelsValors(valors));
	}
	@RequestMapping(value = "/{campId}/valor/{valor}", method = RequestMethod.GET)
	@ResponseBody
	public List<SeleccioOpcioDto> consultaValor(
			HttpServletRequest request,
			@PathVariable(value = "campId") Long campId,
			@PathVariable(value = "valor") String valor,
			@RequestParam(value = "q", required = false) String textFiltre,
			@RequestParam(value = "valors", required = false) String valors,
			ModelMap model) {
		return tascaService.findValorsPerCampDesplegable(
				null,
				null,
				campId,
				valor,
				textFiltre,
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

}