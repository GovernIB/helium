package es.caib.helium.dada.controller;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;
import javax.ws.rs.QueryParam;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import es.caib.helium.dada.domain.Dada;
import es.caib.helium.dada.domain.ExpedientCapcalera;
import es.caib.helium.dada.model.PagedList;
import es.caib.helium.dada.service.ExpedientService;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping(ExpedientController.API_PATH)
public class ExpedientController {

	public static final String API_PATH = "/api/v1/expedients";

//    private final ObjectMapper objectMapper;
//    private final HttpServletRequest request;
//    private final SmartValidator smartValidator;
	private final ExpedientService expedientService;

	// Consulta paginada

	@PostMapping(value = "consulta/resultats", consumes = "application/json")
	public ResponseEntity<PagedList> consultaResultats(
//			@@RequestParam("entornId") Integer entornId,
//			@@RequestParam("expedientTipusId") Integer expedientTipusId,
//
//			@RequestParam("filtreValors") String filtreValors,
//			@RequestParam("filtreValors") String filtreValors,
//			@@RequestParam("columnes") List<String> columnes,
//			@@RequestParam("filtre") String filtre,
//			
			@RequestParam("page") Integer page,
			@RequestParam("size") Integer size,
//			@@RequestParam("sort") String sort
			@RequestBody Map<String,Object> filtreValors
			) {
			var foo = filtreValors;
		return new ResponseEntity<PagedList>(expedientService.consultaResultats(page, size), HttpStatus.OK);
	}

	// Gestió dades capçalera de l'expedient

	@GetMapping(value = "{expedientId}", produces = { "application/json" })
	public ResponseEntity<ExpedientCapcalera> getExpedient(@PathVariable("expedientId") Long expedientId) {
		return new ResponseEntity<ExpedientCapcalera>(expedientService.findByExpedientId(expedientId), HttpStatus.OK);
	}

	@PostMapping(consumes = { "application/json" })
	public ResponseEntity<Void> createExpedient(@Valid @RequestBody ExpedientCapcalera expedient) {
		expedientService.createExpedient(expedient);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@DeleteMapping(value = "{expedientId}")
	public ResponseEntity<Void> deleteExpedient(@PathVariable("expedientId") Long expedientId) {
		// TODO si es borra la informació del a capçalera s'ha de fer quelcom més?
		expedientService.deleteExpedient(expedientId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PutMapping(value = "{expedientId}")
	public ResponseEntity<Void> putExpedient(@Valid @RequestBody ExpedientCapcalera expedient,
			@PathVariable("expedientId") Long expedientId) {
		expedientService.putExpedient(expedientId, expedient);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PatchMapping(value = "{expedientId}")
	public ResponseEntity<Void> patchExpedient(@RequestBody ExpedientCapcalera expedient,
			@PathVariable("expedientId") Long expedientId) {
		expedientService.patchExpedient(expedientId, expedient);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	// Gestió dades de l'expedient

	@GetMapping(value = "{expedientId}/dades")
	public ResponseEntity<List<Dada>> getDades(@PathVariable("expedientId") Long expedientId) {
		return new ResponseEntity<List<Dada>>(expedientService.getDades(expedientId), HttpStatus.OK);
	}

	@GetMapping(value = "{expedientId}/dades/{codi}")
	public ResponseEntity<Dada> getDades(@PathVariable("expedientId") Long expedientId,
			@PathVariable("codi") String codi) {
		return new ResponseEntity<Dada>(expedientService.getDadaByCodi(expedientId, codi), HttpStatus.OK);
	}

	@GetMapping(value = "{expedientId}/proces/{procesId}/dades")
	public ResponseEntity<List<Dada>> getDadesByProces(@PathVariable("expedientId") Long expedientId,
			@PathVariable("procesId") Long procesId) {
		return new ResponseEntity<List<Dada>>(expedientService.getDadesByProces(expedientId, procesId), HttpStatus.OK);
	}

	@GetMapping(value = "{expedientId}/proces/{procesId}/dades/{codi}")
	public ResponseEntity<Dada> getDadesByProcesAndCodi(@PathVariable("expedientId") Long expedientId,
			@PathVariable("procesId") Long procesId, @PathVariable("codi") String codi) {
		return new ResponseEntity<Dada>(expedientService.getDadaByProcesAndCodi(expedientId, procesId, codi),
				HttpStatus.OK);
	}

	@PostMapping(value = "{expedientId}/dades", consumes = "application/json")
	public ResponseEntity<Void> createDadaByExpedientId(@PathVariable("expedientId") Long expedientId,
			@QueryParam("procesId") Long procesId, @RequestBody List<Dada> dada) {
		expedientService.createDades(expedientId, procesId, dada);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PutMapping(value = "{expedientId}/dades/{codi}", consumes = "application/json")
	public ResponseEntity<Void> putDadaByExpedientIdAndCodi(@PathVariable("expedientId") Long expedientId,
			@PathVariable("codi") String codi, @RequestBody Dada dada) {
		expedientService.putDadaByExpedientIdAndCodi(expedientId, codi, dada);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping(value = "{expedientId}/dades/{codi}")
	public ResponseEntity<Void> deleteDadaByExpedientIdAndCodi(@PathVariable("expedientId") Long expedientId,
			@PathVariable("codi") String codi) {
		expedientService.deleteDadaByExpedientIdAndCodi(expedientId, codi);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping(value = "{expedientId}/proces/{procesId}/dades", consumes = "application/json")
	public ResponseEntity<Void> postDadaByExpedientIdProcesIdAndCodi(@PathVariable("expedientId") Long expedientId,
			@PathVariable("procesId") Long procesId, @RequestBody List<Dada> dades) {
		expedientService.postDadesByExpedientIdProcesId(expedientId, procesId, dades);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PutMapping(value = "{expedientId}/proces/{procesId}/dades/{codi}", consumes = "application/json")
	public ResponseEntity<Void> putDadaByExpedientIdProcesIdAndCodi(@PathVariable("expedientId") Long expedientId,
			@PathVariable("procesId") Long procesId, @PathVariable("codi") String codi, @RequestBody Dada dada) {
		expedientService.putDadaByExpedientIdProcesIdAndCodi(expedientId, procesId, codi, dada);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping(value = "{expedientId}/proces/{procesId}/dades/{codi}")
	public ResponseEntity<Void> deleteDadaByExpedientIdAndProcesIdAndCodi(
			@PathVariable("expedientId") Long expedientId, @PathVariable("procesId") Long procesId,
			@PathVariable("codi") String codi) {
		expedientService.deleteDadaByExpedientIdAndProcesIdAndCodi(expedientId, procesId, codi);
		return new ResponseEntity<>(HttpStatus.OK);
	}

//    @GetMapping(produces = { "application/json" })
//    public ResponseEntity<PagedList> listDadesV1(
//            @RequestParam(value = "filtre", required = false) String filtre,
//            final Pageable pageable,
//            final Sort sort) {
//
//        PagedList<DadaDto> baseList = baseService.listDades(
//                filtre,
//                pageable,
//                sort);
//        if (baseList.getTotalElements() == 0) {
//            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//        }
//        return new ResponseEntity<PagedList>(baseList, HttpStatus.OK);
//    }
//    

//    @PostMapping(consumes = { "application/json" })
//    public ResponseEntity<Void> createBaseV1(
//            @Valid @RequestBody BaseDto baseDto) {
//
//        if (baseDto.getCodi() == null)
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El camp Base.codi és obligatori");
//        BaseDto savedDto = baseService.createBase(baseDto);
//        HttpHeaders httpHeaders = new HttpHeaders();
//        httpHeaders.add("Location", API_PATH + "/" + savedDto.getId());
//        return new ResponseEntity<>(HttpStatus.CREATED);
//
//    }
//
//    @PutMapping(value = "/{baseId}", consumes = { "application/json" })
//    public ResponseEntity<Void> updateBaseV1(
//            @PathVariable("baseId") Long baseId,
//            @Valid @RequestBody BaseDto baseDto) {
//
//        baseService.updateBase(
//                baseId,
//                baseDto);
//        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//
//    }
//
//    @PatchMapping(value = "/{baseId}", consumes = { "application/json" })
//    public ResponseEntity<Void> patchBaseV1(
//            HttpServletRequest request,
//            @PathVariable("baseId") Long baseId,
//            @RequestBody JsonNode baseJson,
//            BindingResult bindingResult) {
////        String accept = request.getHeader("Accept");
//
//        Patch patch = new JsonPatchPatchConverter(objectMapper).convert(baseJson);
//        BaseDto baseDto = baseService.getById(baseId);
//        BaseDto patchedBaseDto = patch.apply(
//                baseDto,
//                BaseDto.class
//        );
//        smartValidator.validate(patchedBaseDto, bindingResult);
//        if (bindingResult.hasErrors()) {
//            throw new ResponseStatusException(HttpStatus.CONFLICT, ControllerHelper.getValidationErrorMessage(bindingResult));
//        } else {
//            baseService.updateBase(
//                    baseId,
//                    patchedBaseDto);
//        }
//
//        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//    }
//
//    @DeleteMapping(value = "/{baseId}")
//    public ResponseEntity<Void> deleteBaseV1(
//            @PathVariable("baseId") Long baseId) {
//
//        baseService.delete(baseId);
//        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//
//    }
//
//    @GetMapping(value = "/{baseId}")
//    public ResponseEntity<BaseDto> getBaseV1(
//            @PathVariable("baseId") Long baseId) {
//
//        return new ResponseEntity<>(baseService.getById(baseId), HttpStatus.OK);
//
//    }

}
