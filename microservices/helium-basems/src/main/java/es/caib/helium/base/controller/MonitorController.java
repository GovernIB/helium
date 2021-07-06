package es.caib.helium.base.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.caib.helium.client.model.PagedList;
import es.caib.helium.client.monitor.MonitorService;
import es.caib.helium.client.monitor.model.Consulta;
import es.caib.helium.jms.events.IntegracioEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping(MonitorController.API_PATH)
public class MonitorController {

	public static final String API_PATH = "/api/v1/base/monitor";
    private final MonitorService monitorService;
    
    @GetMapping(value = "events", produces = "application/json") 
    public ResponseEntity<List<IntegracioEvent>> getEvents(@Valid Consulta consulta) throws Exception {
    	
    	log.info("Obtinguent el llistat d'events");
    	var events = monitorService.getEvents(consulta);
    	return new ResponseEntity<List<IntegracioEvent>>(events, HttpStatus.OK);
    }

    @GetMapping(value = "events/paginat", produces = "application/json") 
    public ResponseEntity<PagedList<IntegracioEvent>> getEventsPaginats(@Valid Consulta consulta) throws Exception {
    	
    	log.info("Obtinguent el llistat d'events");
    	var events = monitorService.getEventsPaginats(consulta);
    	return new ResponseEntity<PagedList<IntegracioEvent>>(events, HttpStatus.OK);
    }
    
}
