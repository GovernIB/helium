package es.caib.helium.api.interna.controller.v1.comanda;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.comanda.ms.log.model.FitxerContingut;
import es.caib.comanda.ms.log.model.FitxerInfo;
import es.caib.comanda.service.management.ApiException;
import es.caib.helium.api.interna.controller.dto.ApiResponse;
import es.caib.helium.api.security.RoleHelper;
import net.conselldemallorca.helium.v3.core.api.service.LogService;

@Controller
@RequestMapping("/v1/logs")
public class LogsController {
	@Autowired
	private LogService logService;
	
	/*
	 * Obtenir contingut complet d'un fitxer de log. 
	 * Retorna el contingut i detalls del fitxer de log que es troba dins la carpeta de logs del servidor, i que té el nom indicat
	 * @param nomFitxer Nom del firxer (required)
	 * @return a {@code List<FitxerInfo>}
	 * @throws ApiException
	 */
	@RequestMapping(method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntity<Object> llistarFitxers(
			HttpServletRequest request) throws ApiException {
		
		if(!RoleHelper.hasAnyRole("ROLE_COM"))
			return new ResponseEntity<Object>(new ApiResponse(HttpStatus.UNAUTHORIZED.value(), "Usuari no autoritzat"), HttpStatus.UNAUTHORIZED);
		
		try {
			List<FitxerInfo> fitxers = logService.llistarFitxers();
			return new ResponseEntity<Object>(fitxers, HttpStatus.OK);
		} catch(Exception e) {
			return new ResponseEntity<Object>(new ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	/**
	 * Obtenir les darreres línies d&#39;un fitxer de log
	 * Retorna les darreres linies del fitxer de log indicat per nom. Concretament es retorna el número de línies indicat al paràmetre nLinies.
	 * @param nomFitxer Nom del firxer (required)
	 * @param nLinies Número de línies a recuperar del firxer (required)
	 * @return a {@code FitxerContingut}
	 */
	@RequestMapping(value = "/{nomFitxer:.+}/linies/{nLinies}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Object> llegitUltimesLinies(
			@PathVariable String nomFitxer,
			@PathVariable Long nLinies,
			HttpServletRequest request) throws ApiException {
		if(!RoleHelper.hasAnyRole("ROLE_COM"))
			return new ResponseEntity<Object>(new ApiResponse(HttpStatus.UNAUTHORIZED.value(), "Usuari no autoritzat"), HttpStatus.UNAUTHORIZED);
		try {
			List<String> linies = logService.llegitUltimesLinies(nomFitxer, nLinies);
			return new ResponseEntity<Object>(linies, HttpStatus.OK);
		} catch(Exception e) {
			return new ResponseEntity<Object>(new ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	/**
	 * Obtenir contingut complet d&#39;un fitxer de log
	 * Retorna el contingut i detalls del fitxer de log que es troba dins la carpeta de logs del servidor, i que té el nom indicat
	 * @param nomFitxer Nom del firxer (required)
	 * @return a {@code FitxerContingut}
	 */
	@RequestMapping(value = "/{nomFitxer:.+}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Object> getFitxerByNom(
			@PathVariable String nomFitxer,
			HttpServletRequest request) throws ApiException {
		if(!RoleHelper.hasAnyRole("ROLE_COM"))
			return new ResponseEntity<Object>(new ApiResponse(HttpStatus.UNAUTHORIZED.value(), "Usuari no autoritzat"), HttpStatus.UNAUTHORIZED);
		try {
			FitxerContingut fitxer = logService.getFitxerByNom(nomFitxer);
			return new ResponseEntity<Object>(fitxer, HttpStatus.OK);
		} catch(Exception e) {
			return new ResponseEntity<Object>(new ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
