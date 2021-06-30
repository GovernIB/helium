package es.caib.helium.base.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.caib.helium.client.dada.DataService;
import es.caib.helium.client.dada.model.Consulta;
import es.caib.helium.client.dada.model.Expedient;
import es.caib.helium.client.dada.model.PagedList;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping(DadaController.API_PATH)
public class DadaController {

    public static final String API_PATH = "/api/v1/base/dada";
    private final DataService dataService;
    
	@PostMapping(value = "consulta/resultats/paginat", consumes = "application/json")
	public ResponseEntity<PagedList<Expedient>> consultaResultats(
			@RequestParam("entornId") Integer entornId,
			@RequestParam("expedientTipusId") Integer expedientTipusId, 
			@RequestParam("page") Integer page,
			@RequestParam("size") Integer size, @RequestBody Consulta consulta) throws Exception {
		
		var resultat = dataService.consultaResultats(entornId, expedientTipusId, page, size, consulta);
		return new ResponseEntity<PagedList<Expedient>>(resultat, HttpStatus.OK);
	}

	@PostMapping(value = "consulta/resultats/llistat", consumes = "application/json")
	public ResponseEntity<List<Expedient>> consultaResultatsLlistat(
			@RequestParam("entornId") Integer entornId,
			@RequestParam("expedientTipusId") Integer expedientTipusId, 
			@RequestBody Consulta consulta) throws Exception {
		
		var resultat = dataService.consultaResultatsLlistat(entornId, expedientTipusId, consulta);
		return new ResponseEntity<List<Expedient>>(resultat, HttpStatus.OK);
	}


    @GetMapping(value = "expedient/{expedientId}", produces = { "application/json" })
    public ResponseEntity<Expedient> getExpedientByExpedientId(@PathVariable(value = "expedientId") Long expedientId) {
    	var expedient = dataService.getExpedient(expedientId);
        if (expedient == null) {
            return new ResponseEntity<Expedient>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<Expedient>(expedient, HttpStatus.OK);
    }
    
    @PostMapping(value = "expedient/crear/dades/capcalera")
    public ResponseEntity<Void> crearExpedientDadesCapcalera(@Valid @RequestBody Expedient expedient) {
    	
    	dataService.crearExpedient(expedient);
    	return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @PostMapping(value = "expedients/crear/dades/capcalera")
    public ResponseEntity<Void> crearExpedientsDadesCapcalera(@Valid @RequestBody List<Expedient> expedients) {
    	
    	dataService.crearExpedients(expedients);
    	return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @GetMapping(value = "{procesInstanceId}/dades/expedient/id", produces = { "application/json" })
    public ResponseEntity<Long> getExpedientIdByProcesInstanceId(@PathVariable(value = "procesInstanceId") String procesInstanceId) {
    	var expedient = dataService.getExpedientIdByProcessInstanceId(procesInstanceId);
    	if (expedient == null) {
    		return new ResponseEntity<Long>(HttpStatus.NO_CONTENT);
    	}
    	return new ResponseEntity<Long>(expedient, HttpStatus.OK);
    }
}
