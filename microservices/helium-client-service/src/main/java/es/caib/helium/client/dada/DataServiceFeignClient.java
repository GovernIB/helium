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
import es.caib.helium.client.dada.model.Dada;
import es.caib.helium.client.dada.model.Expedient;
import es.caib.helium.client.dada.model.ValidList;
import es.caib.helium.client.model.PagedList;

public interface DataServiceFeignClient {

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
	
//	// Gestió dades capçalera de l'expedient
	
	@RequestMapping(method = RequestMethod.GET, value = DadaMsApiPath.GET_BY_EXPEDIENT_ID)
	public ResponseEntity<Expedient> getExpedient(@PathVariable("expedientId") Long expedientId);

	@RequestMapping(method = RequestMethod.POST, value = DadaMsApiPath.POST_DADES_CAPCALERA)
	public ResponseEntity<Void> createExpedient(@Valid @RequestBody Expedient expedient);

	@RequestMapping(method = RequestMethod.POST, value = DadaMsApiPath.POST_CREAR_EXPEDIENTS_DADES_CAPCALERA)
	public ResponseEntity<Void> createExpedients(@Valid @RequestBody ValidList<Expedient> expedients);
	
	@RequestMapping(method = RequestMethod.DELETE, value = DadaMsApiPath.DELETE_BY_EXPEDIENT_ID)
	public ResponseEntity<Void> deleteExpedient(@PathVariable("expedientId") Long expedientId);
	
	@RequestMapping(method = RequestMethod.DELETE, value = DadaMsApiPath.DELETE_EXPEDIENTS)
	public ResponseEntity<Void> deleteExpedients(@RequestParam("expedients") List<Long> expedients);
	
	@RequestMapping(method = RequestMethod.PUT, value = DadaMsApiPath.PUT_BY_EXPEDIENT_ID)
	public ResponseEntity<Void> putExpedient(
			@Valid @RequestBody Expedient expedient,
			@PathVariable("expedientId") Long expedientId);
	
	@RequestMapping(method = RequestMethod.PUT, value = DadaMsApiPath.PUT_EXPEDIENTS)
	public ResponseEntity<Void> putExpedients(@Valid @RequestBody ValidList<Expedient> expedients); 
	
	@RequestMapping(method = RequestMethod.PATCH, value = DadaMsApiPath.PATCH_BY_EXPEDIENT_ID)
	public ResponseEntity<Void> patchExpedient(
			@Valid @RequestBody Expedient expedient,
			@PathVariable("expedientId") Long expedientId);
	
	@RequestMapping(method = RequestMethod.PATCH, value = DadaMsApiPath.PATCH_EXPEDIENTS)
	public ResponseEntity<Void> patchExpedients(
			@Valid @RequestBody ValidList<Expedient> expedients); 
	
	// Gestió de les dades de l'expedient
	
	@RequestMapping(method = RequestMethod.GET, value = DadaMsApiPath.GET_DADES)
	public ResponseEntity<List<Dada>> getDades(@PathVariable("expedientId") Long expedientId);
	
	@RequestMapping(method = RequestMethod.GET, value = DadaMsApiPath.GET_DADA_BY_EXPEDIENT_ID_AND_CODI)
	public ResponseEntity<Dada> getDadaByCodi(
			@PathVariable("expedientId") Long expedientId,
			@Valid @PathVariable("codi") String codi);
	
	@RequestMapping(method = RequestMethod.GET, value = DadaMsApiPath.GET_DADA_BY_EXPEDIENT_ID_AND_PROCES_ID)
	public ResponseEntity<List<Dada>> getDadesByProces(
			@PathVariable("expedientId") Long expedientId,
			@PathVariable("procesId") Long procesId);
	
	@RequestMapping(method = RequestMethod.GET, value = DadaMsApiPath.GET_DADA_BY_EXPEDIENT_ID_AND_PROCES_ID_AND_CODI)
	public ResponseEntity<Dada> getDadaByExpedientIdProcesAndCodi(
			@PathVariable("expedientId") Long expedientId,
			@PathVariable("procesId") Long procesId,
			@PathVariable("codi") String codi);
	
	@RequestMapping(method = RequestMethod.GET, value = DadaMsApiPath.GET_DADA_BY_PROCES_ID_AND_CODI)
	public ResponseEntity<Dada> getDadaByProcesAndCodi(
			@PathVariable("procesId") Long procesId,
			@PathVariable("codi") String codi);
	
	@RequestMapping(method = RequestMethod.GET, value = DadaMsApiPath.GET_EXPEDIENT_ID_BY_PROCES_ID)
	public ResponseEntity<Long> getDadaExpedientIdByProcesId(@PathVariable("procesId") Long procesId);
	
	@RequestMapping(method = RequestMethod.POST, value = DadaMsApiPath.POST_DADES_BY_EXPEDIENT_ID)
	public ResponseEntity<Void> postDadesByExpedientId(
			@PathVariable("expedientId") Long expedientId,
			@RequestParam("procesId") Long procesId,
			@Valid @RequestBody ValidList<Dada> dada);
	
	@RequestMapping(method = RequestMethod.PUT, value = DadaMsApiPath.PUT_DADES_BY_EXPEDIENT_ID_AND_CODI)
	public ResponseEntity<Void> putDadaByExpedientIdAndCodi(
			@PathVariable("expedientId") Long expedientId,
			@PathVariable("codi") String codi,
			@Valid @RequestBody Dada dada);
	
	@RequestMapping(method = RequestMethod.DELETE, value = DadaMsApiPath.DELETE_DADES_BY_EXPEDIENT_ID_AND_CODI)
	public ResponseEntity<Void> deleteDadaByExpedientIdAndCodi(
			@PathVariable("expedientId") Long expedientId,
			@PathVariable("codi") String codi);
	
	@RequestMapping(method = RequestMethod.POST, value = DadaMsApiPath.POST_DADES_BY_EXPEDIENT_ID_AND_PROCES_ID)
	public ResponseEntity<Void> postDadaByExpedientIdProcesId(
			@PathVariable("expedientId") Long expedientId,
			@PathVariable("procesId") Long procesId,
			@Valid @RequestBody ValidList<Dada> dades);
	
	@RequestMapping(method = RequestMethod.PUT, value = DadaMsApiPath.PUT_DADA_BY_EXPEDIENT_ID_AND_PROCES_ID_AND_CODI)
	public ResponseEntity<Void> putDadaByExpedientIdProcesIdAndCodi(
			@PathVariable("expedientId") Long expedientId,
			@PathVariable("procesId") Long procesId,
			@PathVariable("codi") String codi, 
			@Valid @RequestBody Dada dada);
	
	@RequestMapping(method = RequestMethod.DELETE, value = DadaMsApiPath.DELETE_DADA_BY_EXPEDIENT_ID_AND_PROCES_ID_AND_CODI)
	public ResponseEntity<Void> deleteDadaByExpedientIdAndProcesIdAndCodi(
			@PathVariable("expedientId") Long expedientId,
			@PathVariable("procesId") Long procesId,
			@PathVariable("codi") String codi); 
	
}
