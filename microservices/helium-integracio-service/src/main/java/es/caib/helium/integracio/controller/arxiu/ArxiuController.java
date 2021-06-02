package es.caib.helium.integracio.controller.arxiu;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.netflix.servo.util.Strings;

import es.caib.helium.integracio.domini.arxiu.ExpedientArxiu;
import es.caib.helium.integracio.service.arxiu.ArxiuService;
import es.caib.plugins.arxiu.api.Expedient;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping(ArxiuController.API_PATH)
public class ArxiuController {

	public static final String API_PATH = "/api/v1/arxiu";

	@Autowired
	private final ArxiuService arxiuService;
	
	  //...
    @ExceptionHandler({Exception.class})
    public void handleException(Exception e) {
        e.printStackTrace();
    }
	
	@GetMapping(value = "expedients/{uuId}", produces = "application/json")
	public ResponseEntity<Expedient> getExpedientsByUuId(@PathVariable("uuId") String uuId) throws Exception { 
		
		// 400 bad input parameter
		if (Strings.isNullOrEmpty(uuId)) {
			return new ResponseEntity<Expedient>(HttpStatus.BAD_REQUEST);
		}
		var expedient = arxiuService.getExpedientByUuId(uuId);
		if (expedient != null) {
			//200 ok
			return new ResponseEntity<Expedient>(expedient, HttpStatus.OK);
		}
		// 204 no content
		return new ResponseEntity<Expedient>(HttpStatus.NO_CONTENT);
	}
	
	@PostMapping(value = "expedients", consumes = "application/json")
	public ResponseEntity<Void> crearExpedient(@Valid @RequestBody ExpedientArxiu expedient, BindingResult errors) throws Exception {
		
		if (errors.hasErrors()) {
			return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
		}
		
		if (arxiuService.crearExpedient(expedient)) {
			return new ResponseEntity<Void>(HttpStatus.OK);
		}
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}
}
