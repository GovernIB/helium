package es.caib.helium.base.controller;

import java.util.List;

import javax.validation.Valid;
import javax.ws.rs.QueryParam;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.caib.helium.client.dada.DataService;
import es.caib.helium.client.dada.model.Consulta;
import es.caib.helium.client.dada.model.Dada;
import es.caib.helium.client.dada.model.Expedient;
import es.caib.helium.client.model.PagedList;
import es.caib.helium.client.dada.model.ValidList;
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
    	
		var expedient = dataService.findByExpedientId(expedientId);
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
    
    
    @DeleteMapping(value = "{expedientId}")
	public ResponseEntity<Void> deleteExpedient(@PathVariable("expedientId") Long expedientId) {
    	
    	dataService.deleteExpedient(expedientId);
    	return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@DeleteMapping(value = "borrar/expedients")
	public ResponseEntity<Void> deleteExpedients(@RequestParam("expedients") List<Long> expedients)  {

		dataService.deleteExpedients(expedients);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PutMapping(value = "{expedientId}")
	public ResponseEntity<Void> putExpedient(
			@Valid @RequestBody Expedient expedient,
			@PathVariable("expedientId") Long expedientId) {

		dataService.putExpedient(expedient, expedientId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PutMapping(value = "put/expedients")
	public ResponseEntity<Void> putExpedients(@Valid @RequestBody List<Expedient> expedients) {

		dataService.putExpedients(expedients);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PatchMapping(value = "{expedientId}")
	public ResponseEntity<Void> patchExpedient(
			@Valid @RequestBody Expedient expedient,
			@PathVariable("expedientId") Long expedientId) {

		dataService.patchExpedient(expedient, expedientId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PatchMapping(value = "patch/expedients")
	public ResponseEntity<Void> patchExpedients(@Valid @RequestBody ValidList<Expedient> expedients) {

		dataService.patchExpedients(expedients);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	
	
	// Gestió dades de l'expedient
	
    
	@GetMapping(value = "{expedientId}/dades")
	public ResponseEntity<List<Dada>> getDades(
			@PathVariable("expedientId") Long expedientId) throws Exception {

		var dades = dataService.getDades(expedientId);
		if (!dades.isEmpty()) {
			return new ResponseEntity<List<Dada>>(dades, HttpStatus.OK);
		}

		if (dataService.findByExpedientId(expedientId) == null) {
			return new ResponseEntity<List<Dada>>(HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<List<Dada>>(dades, HttpStatus.NO_CONTENT);
	}

	@GetMapping(value = "{expedientId}/dades/{codi}")
	public ResponseEntity<Dada> getDadaByCodi(
			@PathVariable("expedientId") Long expedientId,
			@Valid @PathVariable("codi") String codi) throws Exception {

		var dada = dataService.getDadaByCodi(expedientId, codi);
		if (dada != null) {
			return new ResponseEntity<Dada>(dada, HttpStatus.OK);
		}

		if (dataService.findByExpedientId(expedientId) == null) {
			return new ResponseEntity<Dada>(HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<Dada>(dada, HttpStatus.NO_CONTENT);
	}

	@GetMapping(value = "{expedientId}/proces/{procesId}/dades")
	public ResponseEntity<List<Dada>> getDadesByProces(
			@PathVariable("expedientId") Long expedientId,
			@PathVariable("procesId") Long procesId) throws Exception {

		var dades = dataService.getDadesByProces(expedientId, procesId);
		if (!dades.isEmpty()) {
			return new ResponseEntity<List<Dada>>(dades, HttpStatus.OK);
		}

		if (dataService.findByExpedientId(expedientId) == null) {
			return new ResponseEntity<List<Dada>>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<List<Dada>>(dades, HttpStatus.NO_CONTENT);
	}

	@GetMapping(value = "{expedientId}/proces/{procesId}/dades/{codi}")
	public ResponseEntity<Dada> getDadaByExpedientIdProcesAndCodi(
			@PathVariable("expedientId") Long expedientId,
			@PathVariable("procesId") Long procesId,
			@PathVariable("codi") String codi) throws Exception {

		var dada = dataService.getDadaByExpedientIdProcesAndCodi(expedientId, procesId, codi);
		if (dada != null) {
			return new ResponseEntity<Dada>(dada, HttpStatus.OK);
		}

		if (dataService.findByExpedientId(expedientId) == null) {
			return new ResponseEntity<Dada>(HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<Dada>(dada, HttpStatus.NO_CONTENT);
	}
	
	@GetMapping(value = "proces/{procesId}/dades/{codi}")
	public ResponseEntity<Dada> getDadaByProcesAndCodi(
			@PathVariable("procesId") Long procesId,
			@PathVariable("codi") String codi) throws Exception {
		
		var dada = dataService.getDadaByProcesAndCodi(procesId, codi);
		if (dada != null) {
			return new ResponseEntity<Dada>(dada, HttpStatus.OK);
		}
		
		return new ResponseEntity<Dada>(dada, HttpStatus.NO_CONTENT);
	}

	@GetMapping(value = "proces/{procesId}/dades/expedient/id")
	public ResponseEntity<Long> getDadaExpedientIdByProcesId(
			@PathVariable("procesId") Long procesId) throws Exception {
		
		var expedientId = dataService.getDadaExpedientIdByProcesId(procesId);
		if (expedientId != null) {
			return new ResponseEntity<Long>(expedientId, HttpStatus.OK);
		}
		
		return new ResponseEntity<Long>(expedientId, HttpStatus.NO_CONTENT);
	}

	@PostMapping(value = "{expedientId}/dades", consumes = "application/json")
	public ResponseEntity<Void> postDadesByExpedientId(
			@PathVariable("expedientId") Long expedientId,
			@QueryParam("procesId") Long procesId,
			@Valid @RequestBody List<Dada> dades, BindingResult errors) throws Exception {

		if (errors.hasErrors()) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		var valid = new ValidList<Dada>();
		valid.setList(dades);
		
		dataService.postDadesByExpedientId(expedientId, procesId, valid);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@PutMapping(value = "{expedientId}/dades/{codi}", consumes = "application/json")
	public ResponseEntity<Void> putDadaByExpedientIdAndCodi(
			@PathVariable("expedientId") Long expedientId,
			@PathVariable("codi") String codi,
			@Valid @RequestBody Dada dada) throws Exception {

		dataService.putDadaByExpedientIdAndCodi(expedientId, codi, dada);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping(value = "{expedientId}/dades/{codi}")
	public ResponseEntity<Void> deleteDadaByExpedientIdAndCodi(
			@PathVariable("expedientId") Long expedientId,
			@PathVariable("codi") String codi) throws Exception {

		dataService.deleteDadaByExpedientIdAndCodi(expedientId, codi);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping(value = "{expedientId}/proces/{procesId}/dades", consumes = "application/json")
	public ResponseEntity<Void> postDadaByExpedientIdProcesId(
			@PathVariable("expedientId") Long expedientId,
			@PathVariable("procesId") Long procesId,
			@Valid @RequestBody List<Dada> dades) throws Exception {

		if (dades.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		dataService.postDadesByExpedientIdProcesId(expedientId, procesId, dades);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@PutMapping(value = "{expedientId}/proces/{procesId}/dades/{codi}", consumes = "application/json")
	public ResponseEntity<Void> putDadaByExpedientIdProcesIdAndCodi(
			@PathVariable("expedientId") Long expedientId,
			@PathVariable("procesId") Long procesId,
			@PathVariable("codi") String codi, 
			@Valid @RequestBody Dada dada) throws Exception {

		dataService.putDadaByExpedientIdProcesIdAndCodi(expedientId, procesId, codi, dada);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping(value = "{expedientId}/proces/{procesId}/dades/{codi}")
	public ResponseEntity<Void> deleteDadaByExpedientIdAndProcesIdAndCodi(
			@PathVariable("expedientId") Long expedientId,
			@PathVariable("procesId") Long procesId,
			@PathVariable("codi") String codi) throws Exception {

		dataService.deleteDadaByExpedientIdAndProcesIdAndCodi(expedientId, procesId, codi);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
