/**
 * 
 */
package es.caib.helium.api.interna.controller;

import com.wordnik.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controlador pel servei REST de recobriment.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v1/test")
public class RecobrimentRestController {
	@ApiOperation(value = "Mètode pe comprovar si la API està activa", hidden = true)
	@RequestMapping(method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<String> test() {
		return new ResponseEntity<String>("Test successful", HttpStatus.OK);
	}

}
