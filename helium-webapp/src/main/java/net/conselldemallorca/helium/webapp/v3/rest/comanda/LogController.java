package net.conselldemallorca.helium.webapp.v3.rest.comanda;


import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import net.conselldemallorca.helium.v3.core.api.dto.comanda.ApiException;
import net.conselldemallorca.helium.v3.core.api.dto.comanda.FitxerContingut;
import net.conselldemallorca.helium.v3.core.api.dto.comanda.FitxerInfo;
import net.conselldemallorca.helium.v3.core.api.service.LogService;

@Controller
@RequestMapping("/rest/v1/logs")
public class LogController extends ComandaBaseController {
	
	@Autowired
	private LogService logService;

	/*
	 * Obtenir contingut complet d'un fitxer de log. 
	 * Retorna el contingut i detalls del fitxer de log que es troba dins la carpeta de logs del servidor, i que té el nom indicat
	 * @param nomFitxer Nom del firxer (required)
	 * @return a {@code List<FitxerInfo>}
	 * @throws ApiException
	 */
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Object> llistarFitxers(
			HttpServletRequest request) throws ApiException {
		try {
			List<FitxerInfo> fitxers = logService.llistarFitxers();
			return new ResponseEntity<Object>(fitxers, HttpStatus.OK);
		} catch(ApiException e) {
			return new ResponseEntity<Object>(e, HttpStatus.BAD_REQUEST);
		} catch(Exception e) {
			return new ResponseEntity<Object>(new ApiException(500, e.getMessage()), HttpStatus.BAD_REQUEST);
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
			HttpServletRequest request) {
		try {
			FitxerContingut fitxer = logService.llegitUltimesLinies(nomFitxer, nLinies);
			return new ResponseEntity<Object>(fitxer, HttpStatus.OK);
		} catch(ApiException e) {
			return new ResponseEntity<Object>(e, HttpStatus.BAD_REQUEST);
		} catch(Exception e) {
			return new ResponseEntity<Object>(new ApiException(500, e.getMessage()), HttpStatus.BAD_REQUEST);
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
		try {
			FitxerContingut fitxer = logService.getFitxerByNom(nomFitxer);
			return new ResponseEntity<Object>(fitxer, HttpStatus.OK);
		} catch(ApiException e) {
			return new ResponseEntity<Object>(e, HttpStatus.BAD_REQUEST);
		} catch(Exception e) {
			return new ResponseEntity<Object>(new ApiException(500, e.getMessage()), HttpStatus.BAD_REQUEST);
		}
	}
	
}
