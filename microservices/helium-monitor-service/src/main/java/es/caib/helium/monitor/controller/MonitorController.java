package es.caib.helium.monitor.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.caib.helium.jms.events.IntegracioEvent;
import es.caib.helium.monitor.domini.Consulta;
import es.caib.helium.monitor.domini.PagedList;
import es.caib.helium.monitor.service.BddService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@AllArgsConstructor
@RequestMapping(MonitorController.API_PATH)
@Slf4j
public class MonitorController {
	
	@Autowired
	private BddService bddService; 
	
	public static final String API_PATH = "/api/v1/monitor";
	
	@ExceptionHandler({ Exception.class })
	public ResponseEntity<Void> handleException(Exception e) {
		
		e.printStackTrace();
		return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
	}

    @GetMapping(value = "events", produces = "application/json") 
    public ResponseEntity<List<IntegracioEvent>> getEvents(@Valid Consulta consulta) throws Exception { //Filtres per l'accio: els de les columnes
    	
    	log.info("Obtinguent el llistat d'events");
    	var accions = bddService.findByFiltres(consulta);
    	if (accions.isEmpty()) {
    		return new ResponseEntity<List<IntegracioEvent>>(HttpStatus.NO_CONTENT);
    	}
    	return new ResponseEntity<List<IntegracioEvent>>(accions, HttpStatus.OK);
    }

    @GetMapping(value = "events/paginats", produces = "application/json") 
    public ResponseEntity<PagedList<IntegracioEvent>> getEventsPaginats(@Valid Consulta consulta) throws Exception { //Filtres per l'accio: els de les columnes
    	
    	log.info("Obtinguent el llistat d'events paginats");
    	var accions = bddService.findByFiltresPaginat(consulta);
    	if (accions == null || accions.isEmpty()) {
    		return new ResponseEntity<PagedList<IntegracioEvent>>(HttpStatus.NO_CONTENT);
    	}
    	return new ResponseEntity<PagedList<IntegracioEvent>>(accions, HttpStatus.OK);
    }
    

}
