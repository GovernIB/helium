package es.caib.helium.client.dada.dades;

import es.caib.helium.client.dada.dades.model.Consulta;
import es.caib.helium.client.dada.dades.model.Dada;
import es.caib.helium.client.dada.dades.model.Expedient;
import es.caib.helium.client.dada.dades.model.ValidList;
import es.caib.helium.client.model.PagedList;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.List;

public interface DadaFeignClient {

	// Métodes de consulta

	@RequestMapping(method = RequestMethod.POST, value = DadaMsApiPath.CONSULTA_RESULTATS)
	ResponseEntity<PagedList<Expedient>> consultaResultatsPaginats(
			@RequestParam("entornId") Integer entornId,
			@RequestParam("expedientTipusId") Integer expedientTipusId,
			@RequestParam("page") Integer page,
			@RequestParam("size") Integer size, @RequestBody Consulta consulta);

	@RequestMapping(method = RequestMethod.POST, value = DadaMsApiPath.CONSULTA_RESULTATS_LLISTAT)
	ResponseEntity<List<Expedient>> consultaResultatsLlistat(
			@RequestParam("entornId") Integer entornId,
			@RequestParam("expedientTipusId") Integer expedientTipusId,
			@RequestBody Consulta consulta);

//	// Gestió dades capçalera de l'expedient

	//	@CircuitBreaker(name = DadaMsApiPath.NOM_SERVEI, fallbackMethod = "getExpedientFallback")
//	@Retry(name = DadaMsApiPath.NOM_SERVEI, fallbackMethod = "getExpedientFallback")
	@RequestMapping(method = RequestMethod.GET, value = DadaMsApiPath.GET_BY_EXPEDIENT_ID)
	ResponseEntity<Expedient> getExpedient(@PathVariable("expedientId") Long expedientId);

	@RequestMapping(method = RequestMethod.POST, value = DadaMsApiPath.POST_DADES_CAPCALERA)
	ResponseEntity<Void> createExpedient(@Valid @RequestBody Expedient expedient);

	@RequestMapping(method = RequestMethod.POST, value = DadaMsApiPath.POST_CREAR_EXPEDIENTS_DADES_CAPCALERA)
	ResponseEntity<Void> createExpedients(@Valid @RequestBody ValidList<Expedient> expedients);

	@RequestMapping(method = RequestMethod.DELETE, value = DadaMsApiPath.DELETE_BY_EXPEDIENT_ID)
	ResponseEntity<Void> deleteExpedient(@PathVariable("expedientId") Long expedientId);

	@RequestMapping(method = RequestMethod.DELETE, value = DadaMsApiPath.DELETE_EXPEDIENTS)
	ResponseEntity<Void> deleteExpedients(@RequestParam("expedients") List<Long> expedients);

	@RequestMapping(method = RequestMethod.PUT, value = DadaMsApiPath.PUT_BY_EXPEDIENT_ID)
	ResponseEntity<Void> putExpedient(
			@Valid @RequestBody Expedient expedient,
			@PathVariable("expedientId") Long expedientId);

	@RequestMapping(method = RequestMethod.PUT, value = DadaMsApiPath.PUT_EXPEDIENTS)
	ResponseEntity<Void> putExpedients(@Valid @RequestBody ValidList<Expedient> expedients);

	@RequestMapping(method = RequestMethod.PATCH, value = DadaMsApiPath.PATCH_BY_EXPEDIENT_ID)
	ResponseEntity<Void> patchExpedient(
			@Valid @RequestBody Expedient expedient,
			@PathVariable("expedientId") Long expedientId);

	@RequestMapping(method = RequestMethod.PATCH, value = DadaMsApiPath.PATCH_EXPEDIENTS)
	ResponseEntity<Void> patchExpedients(
			@Valid @RequestBody ValidList<Expedient> expedients);

	// Gestió de les dades de l'expedient

	@RequestMapping(method = RequestMethod.GET, value = DadaMsApiPath.GET_DADES)
	ResponseEntity<List<Dada>> getDades(@PathVariable("expedientId") Long expedientId);

	@RequestMapping(method = RequestMethod.GET, value = DadaMsApiPath.GET_DADA_BY_EXPEDIENT_ID_AND_CODI)
	ResponseEntity<Dada> getDadaByCodi(
			@PathVariable("expedientId") Long expedientId,
			@Valid @PathVariable("codi") String codi);

	@RequestMapping(method = RequestMethod.GET, value = DadaMsApiPath.GET_DADA_BY_EXPEDIENT_ID_AND_PROCES_ID)
	ResponseEntity<List<Dada>> getDadesByExpedientIdProcesId(
			@PathVariable("expedientId") Long expedientId,
			@PathVariable("procesId") String procesId);

	@RequestMapping(method = RequestMethod.GET, value = DadaMsApiPath.GET_DADA_BY_EXPEDIENT_ID_AND_PROCES_ID_AND_CODI)
	ResponseEntity<Dada> getDadaByExpedientIdProcesAndCodi(
			@PathVariable("expedientId") Long expedientId,
			@PathVariable("procesId") String procesId,
			@PathVariable("codi") String codi);

	@RequestMapping(method = RequestMethod.GET, value = DadaMsApiPath.GET_DADA_BY_PROCES_ID_AND_CODI)
	ResponseEntity<Dada> getDadaByProcesAndCodi(
			@PathVariable("procesId") String procesId,
			@PathVariable("codi") String codi);

	//	@CircuitBreaker(name = DadaMsApiPath.NOM_SERVEI, fallbackMethod = "getDadaExpedientIdByProcesIdFallback")
//	@Retry(name = DadaMsApiPath.NOM_SERVEI, fallbackMethod = "getDadaExpedientIdByProcesIdFallback")
	@RequestMapping(method = RequestMethod.GET, value = DadaMsApiPath.GET_EXPEDIENT_ID_BY_PROCES_ID)
	ResponseEntity<Long> getDadaExpedientIdByProcesId(@PathVariable("procesId") String procesId);

	@RequestMapping(method = RequestMethod.POST, value = DadaMsApiPath.POST_DADES_BY_EXPEDIENT_ID)
	ResponseEntity<Void> postDadesByExpedientId(
			@PathVariable("expedientId") Long expedientId,
			@RequestParam("procesId") String procesId,
			@Valid @RequestBody ValidList<Dada> dada);

	@RequestMapping(method = RequestMethod.PUT, value = DadaMsApiPath.PUT_DADES_BY_EXPEDIENT_ID_AND_CODI)
	ResponseEntity<Void> putDadaByExpedientIdAndCodi(
			@PathVariable("expedientId") Long expedientId,
			@PathVariable("codi") String codi,
			@Valid @RequestBody Dada dada);

	@RequestMapping(method = RequestMethod.DELETE, value = DadaMsApiPath.DELETE_DADES_BY_EXPEDIENT_ID_AND_CODI)
	ResponseEntity<Void> deleteDadaByExpedientIdAndCodi(
			@PathVariable("expedientId") Long expedientId,
			@PathVariable("codi") String codi);

	@RequestMapping(method = RequestMethod.POST, value = DadaMsApiPath.POST_DADES_BY_EXPEDIENT_ID_AND_PROCES_ID)
	ResponseEntity<Void> postDadaByExpedientIdProcesId(
			@PathVariable("expedientId") Long expedientId,
			@PathVariable("procesId") String procesId,
			@Valid @RequestBody ValidList<Dada> dades);

	@RequestMapping(method = RequestMethod.PUT, value = DadaMsApiPath.PUT_DADA_BY_EXPEDIENT_ID_AND_PROCES_ID_AND_CODI)
	ResponseEntity<Void> putDadaByExpedientIdProcesIdAndCodi(
			@PathVariable("expedientId") Long expedientId,
			@PathVariable("procesId") String procesId,
			@PathVariable("codi") String codi,
			@Valid @RequestBody Dada dada);

	@RequestMapping(method = RequestMethod.DELETE, value = DadaMsApiPath.DELETE_DADA_BY_EXPEDIENT_ID_AND_PROCES_ID_AND_CODI)
	ResponseEntity<Void> deleteDadaByExpedientIdAndProcesIdAndCodi(
			@PathVariable("expedientId") Long expedientId,
			@PathVariable("procesId") String procesId,
			@PathVariable("codi") String codi);


	@RequestMapping(method = RequestMethod.GET, value = DadaMsApiPath.GET_DADES_BY_PROCES_INSTANCE_ID)
	ResponseEntity<List<Dada>> getDadesByProcessInstanceId(
			@PathVariable("procesId") String procesId);

	@RequestMapping(method = RequestMethod.POST, value = DadaMsApiPath.POST_DADA_BY_PROCES_ID)
	ResponseEntity<Boolean> postDadaByProcesId(
			@PathVariable("procesId") String procesId,
			@Valid @RequestBody List<Dada> dades);

	@RequestMapping(method = RequestMethod.DELETE, value = DadaMsApiPath.DELETE_DADA_BY_PROCESS_INSTANCE_ID_AND_CODI)
	void deleteDadaByProcessInstanceIdAndCodi(
			@PathVariable("procesId") String procesId,
			@PathVariable("codi") String codi);

	@RequestMapping(method = RequestMethod.GET, value = DadaMsApiPath.FIND_ROOT_PROCESS_INSTANCES)
	ResponseEntity<List<Expedient>> findRootProcessInstances(@RequestParam("procesIds") List<String> procesIds);

	@RequestMapping(method = RequestMethod.GET, value = DadaMsApiPath.FIND_ROOT_PROCESS_INSTANCE)
	ResponseEntity<Expedient> findRootProcessInstances(@RequestParam("procesId") String procesId);
}