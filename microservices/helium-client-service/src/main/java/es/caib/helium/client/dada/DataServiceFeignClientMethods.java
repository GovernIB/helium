package es.caib.helium.client.dada;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import es.caib.helium.client.dada.model.Consulta;
import es.caib.helium.client.dada.model.Expedient;
import es.caib.helium.client.dada.model.PagedList;
import es.caib.helium.client.dada.model.ValidList;

public interface DataServiceFeignClientMethods {

	// Métodes de consulta
	
	@RequestMapping(method = RequestMethod.POST, value = DadaMsApiPath.CONSULTA_RESULTATS)
	public ResponseEntity<PagedList<Expedient>> consultaResultatsPaginats(
			@RequestParam("entornId") Integer entornId,
			@RequestParam("expedientTipusId") Integer expedientTipusId, 
			@RequestParam("page") Integer page,
			@RequestParam("size") Integer size, @RequestBody Consulta consulta);
	
	@RequestMapping(method = RequestMethod.POST, value = DadaMsApiPath.CONSULTA_RESULTATS_LLISTAT)
	public ResponseEntity<List<Expedient>> consultaResultatsLlistat(
			@RequestParam("entornId") Integer entornId,
			@RequestParam("expedientTipusId") Integer expedientTipusId, 
			@RequestBody Consulta consulta);
	
	// Gestió dades capçalera de l'expedient
	
	@RequestMapping(method = RequestMethod.GET, value = DadaMsApiPath.GET_BY_EXPEDIENT_ID)
	public ResponseEntity<Expedient> getExpedient(@PathVariable("expedientId") Long expedientId);

	@RequestMapping(method = RequestMethod.POST, value = DadaMsApiPath.POST_DADES_CAPCALERA)
	public ResponseEntity<Void> createExpedient(@Valid @RequestBody Expedient expedient);

	@RequestMapping(method = RequestMethod.POST, value = DadaMsApiPath.POST_CREAR_EXPEDIENTS_DADES_CAPCALERA)
	public ResponseEntity<Void> createExpedients(@Valid @RequestBody ValidList<Expedient> expedients);
	
	// Gestió de les dades de l'expedient
	
    @RequestMapping(method = RequestMethod.GET, value = DadaMsApiPath.GET_EXPEDIENT_ID_BY_PROCES_ID)
    ResponseEntity<Long> getExpedientIdByProcessInstanceId(@PathVariable String procesId);
    
}
