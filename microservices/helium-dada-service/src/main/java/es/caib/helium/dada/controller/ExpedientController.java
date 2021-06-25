package es.caib.helium.dada.controller;

import es.caib.helium.dada.domain.Dada;
import es.caib.helium.dada.domain.Expedient;
import es.caib.helium.dada.model.Consulta;
import es.caib.helium.dada.model.PagedList;
import es.caib.helium.dada.model.ValidList;
import es.caib.helium.dada.service.ExpedientService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.ws.rs.QueryParam;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(ExpedientController.API_PATH)
public class ExpedientController {

	public static final String API_PATH = "/api/v1/dades/expedients";

	private final ExpedientService expedientService;

	@PostMapping(value = "consulta/resultats", consumes = "application/json")
	public ResponseEntity<PagedList<Expedient>> consultaResultats(
			@RequestParam("entornId") Integer entornId,
			@RequestParam("expedientTipusId") Integer expedientTipusId, 
			@RequestParam("page") Integer page,
			@RequestParam("size") Integer size, @RequestBody Consulta body) {

		body.setEntornId(entornId);
		body.setExpedientTipusId(expedientTipusId);
		body.setPage(page);
		body.setSize(size);
		return new ResponseEntity<PagedList<Expedient>>(expedientService.consultaResultats(body), HttpStatus.OK);
	}

	@PostMapping(value = "consulta/resultats/llistat", consumes = "application/json")
	public ResponseEntity<List<Expedient>> consultaResultatsLlistat(
			@RequestParam("entornId") Integer entornId,
			@RequestParam("expedientTipusId") Integer expedientTipusId, 
			@RequestBody Consulta body) {

		body.setEntornId(entornId);
		body.setExpedientTipusId(expedientTipusId);
		return new ResponseEntity<List<Expedient>>(expedientService.consultaResultatsLlistat(body), HttpStatus.OK);
	}

	// Gestió dades capçalera de l'expedient

	@GetMapping(value = "{expedientId}", produces = { "application/json" })
	public ResponseEntity<Expedient> getExpedient(@PathVariable("expedientId") Long expedientId) {

		var expedient = expedientService.findByExpedientId(expedientId);
		if (expedient == null) {
			return new ResponseEntity<Expedient>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Expedient>(expedient, HttpStatus.OK);
	}

	@PostMapping(consumes = { "application/json" })
	public ResponseEntity<Void> createExpedient(
			@Valid @RequestBody Expedient expedient,
			BindingResult errors) {

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
			BindingResult errors) {

		if (errors.hasErrors()) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		if (!expedientService.createExpedients(expedients)) {
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@DeleteMapping(value = "{expedientId}")
	public ResponseEntity<Void> deleteExpedient(@PathVariable("expedientId") Long expedientId) {

		if (!expedientService.deleteExpedient(expedientId)) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping(value = "borrar/expedients")
	public ResponseEntity<Void> deleteExpedients(@RequestParam("expedients") List<Long> expedients) {

		if (!expedientService.deleteExpedients(expedients)) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PutMapping(value = "{expedientId}")
	public ResponseEntity<Void> putExpedient(
			@Valid @RequestBody Expedient expedient,
			@PathVariable("expedientId") Long expedientId) {

		if (!expedientService.putExpedient(expedientId, expedient)) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		// TODO swagger 409 conflicte?
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PutMapping(value = "put/expedients")
	public ResponseEntity<Void> putExpedients(
			@Valid @RequestBody ValidList<Expedient> expedients,
			BindingResult errors) {

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
			BindingResult errors) {

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
			BindingResult errors) {

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
			@PathVariable("expedientId") Long expedientId) {

		var dades = expedientService.getDades(expedientId);
		if (!dades.isEmpty()) {
			return new ResponseEntity<List<Dada>>(dades, HttpStatus.OK);
		}

		if (expedientService.findByExpedientId(expedientId) == null) {
			return new ResponseEntity<List<Dada>>(HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<List<Dada>>(dades, HttpStatus.NO_CONTENT);
	}

	@GetMapping(value = "{expedientId}/dades/{codi}")
	public ResponseEntity<Dada> getDadaByCodi(
			@PathVariable("expedientId") Long expedientId,
			@Valid @PathVariable("codi") String codi) {

		var dada = expedientService.getDadaByCodi(expedientId, codi);
		if (dada != null) {
			return new ResponseEntity<Dada>(dada, HttpStatus.OK);
		}

		if (expedientService.findByExpedientId(expedientId) == null) {
			return new ResponseEntity<Dada>(HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<Dada>(dada, HttpStatus.NO_CONTENT);
	}

	@GetMapping(value = "{expedientId}/proces/{procesId}/dades")
	public ResponseEntity<List<Dada>> getDadesByProces(
			@PathVariable("expedientId") Long expedientId,
			@PathVariable("procesId") Long procesId) {

		var dades = expedientService.getDadesByProces(expedientId, procesId);
		if (!dades.isEmpty()) {
			return new ResponseEntity<List<Dada>>(dades, HttpStatus.OK);
		}

		if (expedientService.findByExpedientId(expedientId) == null) {
			return new ResponseEntity<List<Dada>>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<List<Dada>>(dades, HttpStatus.NO_CONTENT);
	}

	@GetMapping(value = "{expedientId}/proces/{procesId}/dades/{codi}")
	public ResponseEntity<Dada> getDadaByExpedientIdProcesAndCodi(
			@PathVariable("expedientId") Long expedientId,
			@PathVariable("procesId") Long procesId,
			@PathVariable("codi") String codi) {

		var dada = expedientService.getDadaByExpedientIdProcesAndCodi(expedientId, procesId, codi);
		if (dada != null) {
			return new ResponseEntity<Dada>(dada, HttpStatus.OK);
		}

		if (expedientService.findByExpedientId(expedientId) == null) {
			return new ResponseEntity<Dada>(HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<Dada>(dada, HttpStatus.NO_CONTENT);
	}
	
	@GetMapping(value = "proces/{procesId}/dades/{codi}")
	public ResponseEntity<Dada> getDadaByProcesAndCodi(
			@PathVariable("procesId") Long procesId,
			@PathVariable("codi") String codi) {
		
		var dada = expedientService.getDadaByProcesAndCodi(procesId, codi);
		if (dada != null) {
			return new ResponseEntity<Dada>(dada, HttpStatus.OK);
		}
		
		return new ResponseEntity<Dada>(dada, HttpStatus.NO_CONTENT);
	}

	@PostMapping(value = "{expedientId}/dades", consumes = "application/json")
	public ResponseEntity<Void> postDadesByExpedientId(
			@PathVariable("expedientId") Long expedientId,
			@QueryParam("procesId") Long procesId,
			@Valid @RequestBody ValidList<Dada> dada, BindingResult errors) {

		if (errors.hasErrors()) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		if (!expedientService.createDades(expedientId, procesId, dada)) {
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@PutMapping(value = "{expedientId}/dades/{codi}", consumes = "application/json")
	public ResponseEntity<Void> putDadaByExpedientIdAndCodi(
			@PathVariable("expedientId") Long expedientId,
			@PathVariable("codi") String codi,
			@Valid @RequestBody Dada dada) {

		if (!expedientService.putDadaByExpedientIdAndCodi(expedientId, codi, dada)) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		// TODO Swagger 409 Conflict?
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping(value = "{expedientId}/dades/{codi}")
	public ResponseEntity<Void> deleteDadaByExpedientIdAndCodi(
			@PathVariable("expedientId") Long expedientId,
			@PathVariable("codi") String codi) {

		if (!expedientService.deleteDadaByExpedientIdAndCodi(expedientId, codi)) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping(value = "{expedientId}/proces/{procesId}/dades", consumes = "application/json")
	public ResponseEntity<Void> postDadaByExpedientIdProcesId(
			@PathVariable("expedientId") Long expedientId,
			@PathVariable("procesId") Long procesId,
			@Valid @RequestBody ValidList<Dada> dades) {

		if (dades.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		expedientService.postDadesByExpedientIdProcesId(expedientId, procesId, dades);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@PutMapping(value = "{expedientId}/proces/{procesId}/dades/{codi}", consumes = "application/json")
	public ResponseEntity<Void> putDadaByExpedientIdProcesIdAndCodi(
			@PathVariable("expedientId") Long expedientId,
			@PathVariable("procesId") Long procesId,
			@PathVariable("codi") String codi, @
					Valid @RequestBody Dada dada) {

		if (!expedientService.putDadaByExpedientIdProcesIdAndCodi(expedientId, procesId, codi, dada)) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		// TODO 409 Conflicte al swagger?
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping(value = "{expedientId}/proces/{procesId}/dades/{codi}")
	public ResponseEntity<Void> deleteDadaByExpedientIdAndProcesIdAndCodi(
			@PathVariable("expedientId") Long expedientId,
			@PathVariable("procesId") Long procesId,
			@PathVariable("codi") String codi) {

		if (!expedientService.deleteDadaByExpedientIdAndProcesIdAndCodi(expedientId, procesId, codi)) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
