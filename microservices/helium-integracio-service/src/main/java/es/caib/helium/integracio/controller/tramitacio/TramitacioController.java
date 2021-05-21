package es.caib.helium.integracio.controller.tramitacio;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.caib.helium.integracio.domini.tramitacio.DadesTramit;
import es.caib.helium.integracio.service.tramitacio.TramitacioService;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping(TramitacioController.API_PATH)
public class TramitacioController {
	
	public static final String API_PATH = "/api/v1/tramitacio";

	@Autowired
	private TramitacioService tramitacioService;
	
	@ExceptionHandler({ Exception.class })
	public void handleException(Exception e) {
		e.printStackTrace();
	}

	@GetMapping(value = "{numRegistre}/justificant", produces = "application/json")
	public ResponseEntity<Void> getJustificant(@Valid @PathVariable("numRegistre") String numRegistre) throws Exception {

//		var justificant = tramitacioService.consultaUnitat(codi);
//		if (justificant == null) {
//			return new ResponseEntity<UnitatOrganica>(HttpStatus.NOT_FOUND);
//		}
//		return new ResponseEntity<UnitatOrganica>(justificant, HttpStatus.OK);
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@GetMapping(value = "{numRegistre}/justificant/detall", produces = "application/json")
	public ResponseEntity<Void> getJustificantDetall(@Valid @PathVariable("numRegistre") String numRegistre) throws Exception {
		
//		var unitat = unitatsService.consultaUnitat(codi);
//		if (unitat == null) {
//			return new ResponseEntity<UnitatOrganica>(HttpStatus.NOT_FOUND);
//		}
//		return new ResponseEntity<UnitatOrganica>(unitat, HttpStatus.OK);
		return new ResponseEntity<Void>(HttpStatus.OK);
	}
	
//	@GetMapping(value = "{numero}/dades/tramit", produces = "application/json")
	@GetMapping(value = "dades/tramit", produces = "application/json")
	public ResponseEntity<DadesTramit> getDadesTramit(@Valid @RequestParam("numero") String numero, @RequestParam("clau") String clau) throws Exception {

		var dadesTramit = tramitacioService.obtenirDadesTramit(numero, clau);
		if (dadesTramit == null) {
			return new ResponseEntity<DadesTramit>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<DadesTramit>(dadesTramit ,HttpStatus.OK);
	}

}
