package es.caib.helium.base.controller;

import es.caib.helium.client.dada.DadaClient;
import es.caib.helium.client.dada.model.Consulta;
import es.caib.helium.client.dada.model.Dada;
import es.caib.helium.client.dada.model.Expedient;
import es.caib.helium.client.dada.model.ValidList;
import es.caib.helium.client.model.PagedList;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.ws.rs.QueryParam;
import java.util.List;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping(DadaController.API_PATH)
public class DadaController {

    public static final String API_PATH = "/api/v1/base/dada";
    private final DadaClient dadaClient;
    
	@PostMapping(value = "consulta/resultats/paginat", consumes = "application/json")
	public ResponseEntity<PagedList<Expedient>> consultaResultats(
			@RequestParam("entornId") Integer entornId,
			@RequestParam("expedientTipusId") Integer expedientTipusId, 
			@RequestParam("page") Integer page,
			@RequestParam("size") Integer size, @RequestBody Consulta consulta) throws Exception {
		
		var resultat = dadaClient.consultaResultats(entornId, expedientTipusId, page, size, consulta);
		return new ResponseEntity<>(resultat, HttpStatus.OK);
	}

	@PostMapping(value = "consulta/resultats/llistat", consumes = "application/json")
	public ResponseEntity<List<Expedient>> consultaResultatsLlistat(
			@RequestParam("entornId") Integer entornId,
			@RequestParam("expedientTipusId") Integer expedientTipusId, 
			@RequestBody Consulta consulta) throws Exception {
		
		var resultat = dadaClient.consultaResultatsLlistat(entornId, expedientTipusId, consulta);
		return new ResponseEntity<>(resultat, HttpStatus.OK);
	}


    @GetMapping(value = "expedient/{expedientId}", produces = { "application/json" })
    public ResponseEntity<Expedient> getExpedientByExpedientId(@PathVariable(value = "expedientId") Long expedientId) throws Exception {
    	
		var expedient = dadaClient.findByExpedientId(expedientId);
		if (expedient == null) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    	}
    	return new ResponseEntity<>(expedient, HttpStatus.OK);
    }
    
    @PostMapping(value = "expedient/crear/dades/capcalera")
    public ResponseEntity<Void> crearExpedientDadesCapcalera(@Valid @RequestBody Expedient expedient) {
    	
    	dadaClient.crearExpedient(expedient);
    	return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "expedients/crear/dades/capcalera")
    public ResponseEntity<Void> crearExpedientsDadesCapcalera(@Valid @RequestBody List<Expedient> expedients) {
    	
    	dadaClient.crearExpedients(expedients);
    	return new ResponseEntity<>(HttpStatus.OK);
    }
    
    
    @DeleteMapping(value = "{expedientId}")
	public ResponseEntity<Void> deleteExpedient(@PathVariable("expedientId") Long expedientId) {
    	
    	dadaClient.deleteExpedient(expedientId);
    	return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping(value = "borrar/expedients")
	public ResponseEntity<Void> deleteExpedients(@RequestParam("expedients") List<Long> expedients)  {

		dadaClient.deleteExpedients(expedients);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PutMapping(value = "{expedientId}")
	public ResponseEntity<Void> putExpedient(
			@Valid @RequestBody Expedient expedient,
			@PathVariable("expedientId") Long expedientId) {

		dadaClient.putExpedient(expedient, expedientId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PutMapping(value = "put/expedients")
	public ResponseEntity<Void> putExpedients(@Valid @RequestBody List<Expedient> expedients) {

		dadaClient.putExpedients(expedients);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PatchMapping(value = "{expedientId}")
	public ResponseEntity<Void> patchExpedient(
			@Valid @RequestBody Expedient expedient,
			@PathVariable("expedientId") Long expedientId) {

		dadaClient.patchExpedient(expedient, expedientId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PatchMapping(value = "patch/expedients")
	public ResponseEntity<Void> patchExpedients(@Valid @RequestBody ValidList<Expedient> expedients) {

		dadaClient.patchExpedients(expedients);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	
	
	// Gestió dades de l'expedient
	
    
	@GetMapping(value = "{expedientId}/dades")
	public ResponseEntity<List<Dada>> getDades(
			@PathVariable("expedientId") Long expedientId) throws Exception {

		var dades = dadaClient.getDades(expedientId);
		if (!dades.isEmpty()) {
			return new ResponseEntity<>(dades, HttpStatus.OK);
		}

		if (dadaClient.findByExpedientId(expedientId) == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<>(dades, HttpStatus.NO_CONTENT);
	}

	@GetMapping(value = "{expedientId}/dades/{codi}")
	public ResponseEntity<Dada> getDadaByCodi(
			@PathVariable("expedientId") Long expedientId,
			@Valid @PathVariable("codi") String codi) throws Exception {

		var dada = dadaClient.getDadaByCodi(expedientId, codi);
		if (dada != null) {
			return new ResponseEntity<>(dada, HttpStatus.OK);
		}

		if (dadaClient.findByExpedientId(expedientId) == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@GetMapping(value = "{expedientId}/proces/{procesId}/dades")
	public ResponseEntity<List<Dada>> getDadesByProces(
			@PathVariable("expedientId") Long expedientId,
			@PathVariable("procesId") String procesId) throws Exception {

		var dades = dadaClient.getDadesByProces(expedientId, procesId);
		if (!dades.isEmpty()) {
			return new ResponseEntity<>(dades, HttpStatus.OK);
		}

		if (dadaClient.findByExpedientId(expedientId) == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(dades, HttpStatus.NO_CONTENT);
	}

	@GetMapping(value = "{expedientId}/proces/{procesId}/dades/{codi}")
	public ResponseEntity<Dada> getDadaByExpedientIdProcesAndCodi(
			@PathVariable("expedientId") Long expedientId,
			@PathVariable("procesId") String procesId,
			@PathVariable("codi") String codi) throws Exception {

		var dada = dadaClient.getDadaByExpedientIdProcesAndCodi(expedientId, procesId, codi);
		if (dada != null) {
			return new ResponseEntity<>(dada, HttpStatus.OK);
		}

		if (dadaClient.findByExpedientId(expedientId) == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<>(dada, HttpStatus.NO_CONTENT);
	}
	
	@GetMapping(value = "proces/{procesId}/dades/{codi}")
	public ResponseEntity<Dada> getDadaByProcesAndCodi(
			@PathVariable("procesId") String procesId,
			@PathVariable("codi") String codi) throws Exception {
		
		var dada = dadaClient.getDadaByProcesAndCodi(procesId, codi);
		if (dada != null) {
			return new ResponseEntity<>(dada, HttpStatus.OK);
		}
		
		return new ResponseEntity<>(dada, HttpStatus.NO_CONTENT);
	}

	@GetMapping(value = "proces/{procesId}/dades/expedient/id")
	public ResponseEntity<Long> getDadaExpedientIdByProcesId(
			@PathVariable("procesId") String procesId) throws Exception {
		
		var expedientId = dadaClient.getDadaExpedientIdByProcesId(procesId);
		if (expedientId != null) {
			return new ResponseEntity<>(expedientId, HttpStatus.OK);
		}
		
		return new ResponseEntity<>(expedientId, HttpStatus.NO_CONTENT);
	}

	@PostMapping(value = "{expedientId}/dades", consumes = "application/json")
	public ResponseEntity<Void> postDadesByExpedientId(
			@PathVariable("expedientId") Long expedientId,
			@QueryParam("procesId") String procesId,
			@Valid @RequestBody List<Dada> dades, BindingResult errors) {

		if (errors.hasErrors()) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		var valid = new ValidList<Dada>();
		valid.setList(dades);
		
		dadaClient.postDadesByExpedientId(expedientId, procesId, valid);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@PutMapping(value = "{expedientId}/dades/{codi}", consumes = "application/json")
	public ResponseEntity<Void> putDadaByExpedientIdAndCodi(
			@PathVariable("expedientId") Long expedientId,
			@PathVariable("codi") String codi,
			@Valid @RequestBody Dada dada) {

		dadaClient.putDadaByExpedientIdAndCodi(expedientId, codi, dada);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping(value = "{expedientId}/dades/{codi}")
	public ResponseEntity<Void> deleteDadaByExpedientIdAndCodi(
			@PathVariable("expedientId") Long expedientId,
			@PathVariable("codi") String codi) {

		dadaClient.deleteDadaByExpedientIdAndCodi(expedientId, codi);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping(value = "{expedientId}/proces/{procesId}/dades", consumes = "application/json")
	public ResponseEntity<Void> postDadaByExpedientIdProcesId(
			@PathVariable("expedientId") Long expedientId,
			@PathVariable("procesId") String procesId,
			@Valid @RequestBody List<Dada> dades) {

		if (dades.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		dadaClient.postDadesByExpedientIdProcesId(expedientId, procesId, dades);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@PutMapping(value = "{expedientId}/proces/{procesId}/dades/{codi}", consumes = "application/json")
	public ResponseEntity<Void> putDadaByExpedientIdProcesIdAndCodi(
			@PathVariable("expedientId") Long expedientId,
			@PathVariable("procesId") String procesId,
			@PathVariable("codi") String codi, 
			@RequestBody Dada dada) {

		dadaClient.putDadaByExpedientIdProcesIdAndCodi(expedientId, procesId, codi, dada);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping(value = "{expedientId}/proces/{procesId}/dades/{codi}")
	public ResponseEntity<Void> deleteDadaByExpedientIdAndProcesIdAndCodi(
			@PathVariable("expedientId") Long expedientId,
			@PathVariable("procesId") String procesId,
			@PathVariable("codi") String codi) {

		dadaClient.deleteDadaByExpedientIdAndProcesIdAndCodi(expedientId, procesId, codi);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
