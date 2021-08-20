package es.caib.helium.dada.controller;

import es.caib.helium.dada.model.Consulta;
import es.caib.helium.dada.model.Dada;
import es.caib.helium.dada.model.Expedient;
import es.caib.helium.dada.model.PagedList;
import es.caib.helium.dada.model.ValidList;
import es.caib.helium.dada.service.ExpedientService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(ExpedientController.API_PATH)
public class ExpedientController {

	public static final String API_PATH = "/api/v1/dades/expedients";

	private final ExpedientService expedientService;
	
	@ExceptionHandler({ Exception.class })
	public ResponseEntity<Void> handleException(Exception e) {
		e.printStackTrace();
		return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@PostMapping(value = "consulta/resultats", consumes = "application/json")
	public ResponseEntity<PagedList<Expedient>> consultaResultats(
			@RequestParam("entornId") Integer entornId,
			@RequestParam("expedientTipusId") Integer expedientTipusId, 
			@RequestParam("page") Integer page,
			@RequestParam("size") Integer size, @RequestBody Consulta body) throws Exception {

		body.setEntornId(entornId);
		body.setExpedientTipusId(expedientTipusId);
		body.setPage(page);
		body.setSize(size);
		return new ResponseEntity<>(expedientService.consultaResultats(body), HttpStatus.OK);
	}

	@PostMapping(value = "consulta/resultats/llistat", consumes = "application/json")
	public ResponseEntity<List<Expedient>> consultaResultatsLlistat(
			@RequestParam("entornId") Integer entornId,
			@RequestParam("expedientTipusId") Integer expedientTipusId, 
			@RequestBody Consulta body) throws Exception {

		body.setEntornId(entornId);
		body.setExpedientTipusId(expedientTipusId);
		return new ResponseEntity<>(expedientService.consultaResultatsLlistat(body), HttpStatus.OK);
	}

	// Gestió dades capçalera de l'expedient

	@GetMapping(value = "{expedientId}", produces = { "application/json" })
	public ResponseEntity<Expedient> getExpedient(
			@PathVariable("expedientId") Long expedientId) throws Exception {

		var expedient = expedientService.findByExpedientId(expedientId);
		if (expedient == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(expedient, HttpStatus.OK);
	}

	@PostMapping(consumes = { "application/json" })
	public ResponseEntity<Void> createExpedient(
			@Valid @RequestBody Expedient expedient,
			BindingResult errors) throws Exception {

		if (errors.hasErrors()) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		if (!expedientService.createExpedient(expedient)) {
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@PostMapping(value = "crear/expedients", consumes = { "application/json" })
	public ResponseEntity<Void> createExpedients(
			@Valid @RequestBody ValidList<Expedient> expedients,
			BindingResult errors) throws Exception {

		if (errors.hasErrors()) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		if (!expedientService.createExpedients(expedients)) {
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@DeleteMapping(value = "{expedientId}")
	public ResponseEntity<Void> deleteExpedient(
			@PathVariable("expedientId") Long expedientId) throws Exception {

		if (!expedientService.deleteExpedient(expedientId)) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping(value = "borrar/expedients")
	public ResponseEntity<Void> deleteExpedients(
			@RequestParam("expedients") List<Long> expedients) throws Exception {

		if (!expedientService.deleteExpedients(expedients)) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PutMapping(value = "{expedientId}")
	public ResponseEntity<Void> putExpedient(
			@Valid @RequestBody Expedient expedient,
			@PathVariable("expedientId") Long expedientId) throws Exception {

		if (!expedientService.putExpedient(expedientId, expedient)) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		// TODO swagger 409 conflicte?
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PutMapping(value = "put/expedients")
	public ResponseEntity<Void> putExpedients(
			@Valid @RequestBody ValidList<Expedient> expedients,
			BindingResult errors) throws Exception {

		if (errors.hasErrors()) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		if (expedientService.putExpedients(expedients)) {
			return new ResponseEntity<>(HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.CONFLICT);
	}

	@PatchMapping(value = "{expedientId}")
	public ResponseEntity<Void> patchExpedient(
			@Valid @RequestBody Expedient expedient,
			@PathVariable("expedientId") Long expedientId,
			BindingResult errors) throws Exception {

		if (errors.hasErrors()) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		if (expedientService.patchExpedient(expedientId, expedient)) {
			return new ResponseEntity<>(HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	@PatchMapping(value = "patch/expedients")
	public ResponseEntity<Void> patchExpedients(
			@Valid @RequestBody ValidList<Expedient> expedients,
			BindingResult errors) throws Exception {

		if (errors.hasErrors()) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		if (expedientService.patchExpedients(expedients)) {
			return new ResponseEntity<>(HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.CONFLICT);
	}

	// Gestió dades de l'expedient

	@GetMapping(value = "{expedientId}/dades")
	public ResponseEntity<List<Dada>> getDades(
			@PathVariable("expedientId") Long expedientId) throws Exception {

		var dades = expedientService.getDades(expedientId);
		if (!dades.isEmpty()) {
			return new ResponseEntity<>(dades, HttpStatus.OK);
		}

		if (expedientService.findByExpedientId(expedientId) == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<>(dades, HttpStatus.NO_CONTENT);
	}

	@GetMapping(value = "{expedientId}/dades/{codi}")
	public ResponseEntity<Dada> getDadaByCodi(
			@PathVariable("expedientId") Long expedientId,
			@Valid @PathVariable("codi") String codi) throws Exception {

		var dada = expedientService.getDadaByCodi(expedientId, codi);
		if (dada != null) {
			return new ResponseEntity<>(dada, HttpStatus.OK);
		}

		if (expedientService.findByExpedientId(expedientId) == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@GetMapping(value = "{expedientId}/proces/{procesId}/dades")
	public ResponseEntity<List<Dada>> getDadesByProces(
			@PathVariable("expedientId") Long expedientId,
			@PathVariable("procesId") String procesId) throws Exception {

		var dades = expedientService.getDadesByExpedientIdAndProcesId(expedientId, procesId);
		if (!dades.isEmpty()) {
			return new ResponseEntity<>(dades, HttpStatus.OK);
		}

		if (expedientService.findByExpedientId(expedientId) == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(dades, HttpStatus.NO_CONTENT);
	}

	@GetMapping(value = "{expedientId}/proces/{procesId}/dades/{codi}")
	public ResponseEntity<Dada> getDadaByExpedientIdProcesAndCodi(
			@PathVariable("expedientId") Long expedientId,
			@PathVariable("procesId") String procesId,
			@PathVariable("codi") String codi) throws Exception {

		var dada = expedientService.getDadaByExpedientIdProcesAndCodi(expedientId, procesId, codi);
		if (dada != null) {
			return new ResponseEntity<>(dada, HttpStatus.OK);
		}

		if (expedientService.findByExpedientId(expedientId) == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	@GetMapping(value = "proces/{procesId}/dades/{codi}")
	public ResponseEntity<Dada> getDadaByProcesAndCodi(
			@PathVariable("procesId") String procesId,
			@PathVariable("codi") String codi) throws Exception {
		
		var dada = expedientService.getDadaByProcesAndCodi(procesId, codi);
		if (dada != null) {
			return new ResponseEntity<>(dada, HttpStatus.OK);
		}
		
		return new ResponseEntity<>(dada, HttpStatus.NO_CONTENT);
	}

	@GetMapping(value = "proces/{procesId}/dades/expedient/id")
	public ResponseEntity<Long> getDadaExpedientIdByProcesId(
			@PathVariable("procesId") String procesId) throws Exception {
		
		var expedientId = expedientService.getDadaExpedientIdByProcesId(procesId);
		if (expedientId != null) {
			return new ResponseEntity<>(expedientId, HttpStatus.OK);
		}
		
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	@PostMapping(value = "{expedientId}/dades", consumes = "application/json")
	public ResponseEntity<Void> postDadesByExpedientId(
			@PathVariable("expedientId") Long expedientId,
			@RequestParam("procesId") String procesId,
			@Valid @RequestBody ValidList<Dada> dades, BindingResult errors) throws Exception {

		if (errors.hasErrors()) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		if (!expedientService.createDades(expedientId, procesId, dades)) {
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@PutMapping(value = "{expedientId}/dades/{codi}", consumes = "application/json")
	public ResponseEntity<Void> putDadaByExpedientIdAndCodi(
			@PathVariable("expedientId") Long expedientId,
			@PathVariable("codi") String codi,
			@Valid @RequestBody Dada dada) throws Exception {

		if (!expedientService.putDadaByExpedientIdAndCodi(expedientId, codi, dada)) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		// TODO Swagger 409 Conflict?
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping(value = "{expedientId}/dades/{codi}")
	public ResponseEntity<Void> deleteDadaByExpedientIdAndCodi(
			@PathVariable("expedientId") Long expedientId,
			@PathVariable("codi") String codi) throws Exception {

		if (!expedientService.deleteDadaByExpedientIdAndCodi(expedientId, codi)) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping(value = "{expedientId}/proces/{procesId}/dades", consumes = "application/json")
	public ResponseEntity<Void> postDadaByExpedientIdProcesId(
			@PathVariable("expedientId") Long expedientId,
			@PathVariable("procesId") String procesId,
			@Valid @RequestBody ValidList<Dada> dades) throws Exception {

		if (dades.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		expedientService.postDadesByExpedientIdProcesId(expedientId, procesId, dades);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@PutMapping(value = "{expedientId}/proces/{procesId}/dades/{codi}", consumes = "application/json")
	public ResponseEntity<Void> putDadaByExpedientIdProcesIdAndCodi(
			@PathVariable("expedientId") Long expedientId,
			@PathVariable("procesId") String procesId,
			@PathVariable("codi") String codi, 
			@Valid @RequestBody Dada dada) throws Exception {

		if (!expedientService.putDadaByExpedientIdProcesIdAndCodi(expedientId, procesId, codi, dada)) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		// TODO 409 Conflicte al swagger?
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping(value = "{expedientId}/proces/{procesId}/dades/{codi}")
	public ResponseEntity<Void> deleteDadaByExpedientIdAndProcesIdAndCodi(
			@PathVariable("expedientId") Long expedientId,
			@PathVariable("procesId") String procesId,
			@PathVariable("codi") String codi) throws Exception {

		if (!expedientService.deleteDadaByExpedientIdAndProcesIdAndCodi(expedientId, procesId, codi)) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping(value = "proces/{procesId}/dades", produces = "application/json")
	public ResponseEntity<List<Dada>> getDadesByProcessInstanceId(@PathVariable("procesId") String procesId) throws Exception {

		var consulta = new Consulta();
		var dades = expedientService.getDadesByProcesId(procesId);
		if (dades.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(dades, HttpStatus.OK);
	}

	@PostMapping(value = "proces/{procesId}/dades", consumes = "application/json")
	public ResponseEntity<Boolean> postDadaByProcesId(
			@PathVariable("procesId") String procesId,
			@Valid @RequestBody List<Dada> dades) throws Exception {

		if (dades.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		var created = expedientService.createDades(procesId, dades);
		if (created) {
			return new ResponseEntity<>(created, HttpStatus.CREATED);
		}
		return new ResponseEntity<>(created, HttpStatus.CONFLICT);
	}

	@DeleteMapping(value = "proces/{procesId}/dades/{codi}", produces = "application/json")
	public ResponseEntity<Void> deleteDadaByProcessInstanceIdAndCodi(
			@PathVariable("procesId") String procesId,
			@PathVariable("codi") String codi) throws Exception {

		var dades = expedientService.deleteDadaByProcesIdAndCodi(procesId, codi);
		if (dades) {
			return new ResponseEntity<>(HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	@GetMapping(value = "find/root/process/instances", produces = "application/json" )
	public ResponseEntity<List<Expedient>> findRootProcessInstances(@RequestParam("procesIds") List<String> procesIds) throws Exception {

		var rootProcesIds = expedientService.findRootProcessInstance(procesIds);
		if (rootProcesIds.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(rootProcesIds, HttpStatus.OK);
	}

	@GetMapping(value = "find/root/process/instance", produces = "application/json" )
	public ResponseEntity<Expedient> findRootProcessInstances(@RequestParam("procesId") String procesId) throws Exception {

		var rootProcesId = expedientService.findRootProcessInstance(procesId);
		if (rootProcesId == null) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(rootProcesId, HttpStatus.OK);
	}
}
