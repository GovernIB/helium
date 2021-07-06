/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.emiserv.logic.intf.exception.SistemaExternException;
import es.caib.helium.logic.intf.dto.SeleccioOpcioDto;
import es.caib.helium.logic.intf.service.TascaService;

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
				null,
				null,
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
			ModelMap model,
			HttpServletResponse response) throws IOException  {
		List<SeleccioOpcioDto> resposta = null;
		try {
		resposta = tascaService.findValorsPerCampDesplegable(
				null,
				processInstanceId,
				campId,
				null,
				textFiltre,
				null,
				null,
				getMapDelsValors(valors));
		} catch (SistemaExternException see) {
			response.setStatus(500);
			response.getWriter().write(see.getPublicMessage());
			logger.error(see.getMessage());
		} catch (Exception ex) {
			response.setStatus(500);
			String errorText = "Error en la comunicació amb el sistema extern per un error intern: ";
			if (ex.getCause() != null && ex.getCause().getMessage() != null)
				errorText += ex.getCause().getMessage();
			else
				errorText += ex.toString();
			response.getWriter().write(errorText);
			logger.error("Error en la comunicació amb el sistema extern per un error intern: ", ex);
		}
		return resposta;
	}

	@RequestMapping(value = "/{campId}/tasca/{tascaId}/valors", method = RequestMethod.GET)
	@ResponseBody
	public List<SeleccioOpcioDto> consultaAmbTasca(
			HttpServletRequest request,
			@PathVariable(value = "campId") Long campId,
			@PathVariable(value = "tascaId") String tascaId,
			@RequestParam(value = "q", required = false) String textFiltre,
			@RequestParam(value = "valors", required = false) String valors,
			ModelMap model, 
			HttpServletResponse response) throws IOException {
		
		List<SeleccioOpcioDto> resposta = null;
		try {
		resposta = tascaService.findValorsPerCampDesplegable(
				tascaId,
				null,
				campId,
				null,
				textFiltre,
				null,
				null,
				getMapDelsValors(valors));
		} catch (SistemaExternException see) {
			response.setStatus(500);
			response.getWriter().write(see.getPublicMessage());
			logger.error(see.getMessage());
		} catch (Exception ex) {
			response.setStatus(500);
			String errorText = "Error en la comunicació amb el sistema extern per un error intern: ";
			if (ex.getCause() != null && ex.getCause().getMessage() != null)
				errorText += ex.getCause().getMessage();
			else
				errorText += ex.toString();
			response.getWriter().write(errorText);
			logger.error("Error en la comunicació amb el sistema extern per un error intern: ", ex);
		}
		return resposta;
	}

	@RequestMapping(value = "/{campId}/tasca/{tascaId}/valors/valor", method = RequestMethod.GET)
	@ResponseBody
	public List<SeleccioOpcioDto> consultaAmbTascaValor(
			HttpServletRequest request,
			@PathVariable(value = "campId") Long campId,
			@PathVariable(value = "tascaId") String tascaId,
			@RequestParam(value = "valor") String textFiltre,
			@RequestParam(value = "registreCampId", required = false) Long registreCampId,
			@RequestParam(value = "registreIndex", required = false) Integer registreIndex,
			ModelMap model) {
		return tascaService.findValorsPerCampDesplegable(
				tascaId,
				null,
				campId,
				null,
				textFiltre,
				registreCampId,
				registreIndex,
				null);
	}

	@RequestMapping(value = "/{campId}/valors", method = RequestMethod.GET)
	@ResponseBody
	public List<SeleccioOpcioDto> consulta(
			HttpServletRequest request,
			@PathVariable(value = "campId") Long campId,
			@RequestParam(value = "q", required = false) String textFiltre,
			@RequestParam(value = "valors", required = false) String valors,
			ModelMap model,
			HttpServletResponse response) throws IOException {
		
		List<SeleccioOpcioDto> resposta = null;
		try {
			return tascaService.findValorsPerCampDesplegable(
					null,
					null,
					campId,
					null,
					textFiltre,
					null,
					null,
					getMapDelsValors(valors));
		} catch (SistemaExternException see) {
			response.setStatus(500);
			response.getWriter().write(see.getPublicMessage());
			logger.error(see.getMessage());
		} catch (Exception ex) {
			response.setStatus(500);
			String errorText = "Error en la comunicació amb el sistema extern per un error intern: ";
			if (ex.getCause() != null && ex.getCause().getMessage() != null)
				errorText += ex.getCause().getMessage();
			else
				errorText += ex.toString();
			response.getWriter().write(errorText);
			logger.error("Error en la comunicació amb el sistema extern per un error intern: ", ex);
		}
		return resposta;
	}

	@RequestMapping(value = "/{campId}/valors/valor", method = RequestMethod.GET)
	@ResponseBody
	public List<SeleccioOpcioDto> consultaValor(
			HttpServletRequest request,
			@PathVariable(value = "campId") Long campId,
			@RequestParam(value = "valor") String textFiltre,
			ModelMap model) {
		return tascaService.findValorsPerCampDesplegable(
				null,
				null,
				campId,
				null,
				textFiltre,
				null,
				null,
				null);
	}

	@RequestMapping(value = "/{campId}/context/{processInstanceId}/{tascaId}/valor", method = RequestMethod.GET)
	@ResponseBody
	public List<SeleccioOpcioDto> consultaValorContext(
			HttpServletRequest request,
			@PathVariable(value = "campId") Long campId,
			@PathVariable(value = "processInstanceId") String processInstanceId,
			@PathVariable(value = "tascaId") String tascaId,
			@RequestParam(value = "valor") String valor,
			@RequestParam(value = "q", required = false) String textFiltre,
			@RequestParam(value = "registreCampId", required = false) Long registreCampId,
			@RequestParam(value = "registreIndex", required = false) Integer registreIndex,
			@RequestParam(value = "valors", required = false) String valors,
			ModelMap model) {
		return tascaService.findValorsPerCampDesplegable(
				tascaId,
				processInstanceId,
				campId,
				valor,
				textFiltre,
				registreCampId,
				registreIndex,
				getMapDelsValors(valors));
	}

	@RequestMapping(value = "/{campId}/proces/{processInstanceId}/valor", method = RequestMethod.GET)
	@ResponseBody
	public List<SeleccioOpcioDto> consultaValorProces(
			HttpServletRequest request,
			@PathVariable(value = "campId") Long campId,
			@PathVariable(value = "processInstanceId") String processInstanceId,
			@RequestParam(value = "valor") String valor,
			@RequestParam(value = "q", required = false) String textFiltre,
			@RequestParam(value = "registreCampId", required = false) Long registreCampId,
			@RequestParam(value = "registreIndex", required = false) Integer registreIndex,
			@RequestParam(value = "valors", required = false) String valors,
			ModelMap model) {
		return tascaService.findValorsPerCampDesplegable(
				null,
				processInstanceId,
				campId,
				valor,
				textFiltre,
				registreCampId,
				registreIndex,
				getMapDelsValors(valors));
	}

	@RequestMapping(value = "/{campId}/tasca/{tascaId}/valor", method = RequestMethod.GET)
	@ResponseBody
	public List<SeleccioOpcioDto> consultaValorTasca(
			HttpServletRequest request,
			@PathVariable(value = "campId") Long campId,
			@PathVariable(value = "tascaId") String tascaId,
			@RequestParam(value = "valor") String valor,
			@RequestParam(value = "q", required = false) String textFiltre,
			@RequestParam(value = "registreCampId", required = false) Long registreCampId,
			@RequestParam(value = "registreIndex", required = false) Integer registreIndex,
			@RequestParam(value = "valors", required = false) String valors,
			ModelMap model) {
				
		return tascaService.findValorsPerCampDesplegable(
				tascaId,
				null,
				campId,
				valor,
				textFiltre,
				registreCampId,
				registreIndex,
				getMapDelsValors(valors));
	}
	
	@RequestMapping(value = "/{campId}/valor", method = RequestMethod.GET)
	@ResponseBody
	public List<SeleccioOpcioDto> consultaValor(
			HttpServletRequest request,
			@PathVariable(value = "campId") Long campId,
			@RequestParam(value = "valor") String valor,
			@RequestParam(value = "q", required = false) String textFiltre,
			@RequestParam(value = "valors", required = false) String valors,
			ModelMap model) {
		
		return tascaService.findValorsPerCampDesplegable(
				null,
				null,
				campId,
				valor,
				textFiltre,
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

	private static final Logger logger = LoggerFactory.getLogger(CampValorsController.class);
}