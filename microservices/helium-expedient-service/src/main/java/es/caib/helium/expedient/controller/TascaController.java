package es.caib.helium.expedient.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controlador que defineix la API REST de tasques per la consulta paginada i els
 * mètodes per crear, actualitzar o esborrar la informació de les tasques dels tasques.
 * 
 */
@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping(TascaController.API_PATH)
public class TascaController {

    public static final String API_PATH = "/api/v1/tasques";

//    private final TascaService tascaService;
//    private final ObjectMapper objectMapper;
//    private final SmartValidator smartValidator;
//
//    /**
//     *
//     * @param entornId Identificador de l'entorn al que pertanyen les tasques consultades
//     * @param filtre Filtre a aplicar en la consulta. El filtre té format RSQL. {@see https://github.com/jirutka/rsql-parser}.
//     *               Exemple: {@code nom=ic=*tasca*,codi==T1}
//     * @param expedientTipusId Expedient tipus al que pertanyen les tasques consultades.
//     * @param pageable Informació de paginació. Exemple: {@code page=0&size=25}
//     * @param sort Informació de ordre Exemple: {@code nom,desc}
//     * @return Retorna una pàgina del llistat de tasques, un cop aplicats els camps de filtre.
//     *<br/>
//     * La cerca pot rebre paràmetres per:
//     * <ul>
//     *     <li>ordenar</li>
//     *     <li>paginar</li>
//     *     <li>filtrar (utilitzant sintaxi rsql)</li>
//     * </ul>
//     */
//    @GetMapping(produces = { "application/json" })
//    public ResponseEntity<PagedList<TascaDto>> listTasquesV1(
//            @RequestParam(value = "entornId") Long entornId,
//            @RequestParam(value = "filtre", required = false) String filtre,
//            @RequestParam(value = "tascaTipusId", required = false) Long tascaTipusId,
//            final Pageable pageable,
//            final Sort sort) {
//
//        log.debug("[CTR] llistant tasques: \n" +
//                "entornId: " + entornId +
//                "tascaTipusId: " + tascaTipusId +
//                "filtre: " + filtre);
//
//        PagedList<TascaDto> tascaList = tascaService.listTasques(
//                entornId,
//                tascaTipusId,
//                filtre,
//                pageable,
//                sort);
//        if (tascaList.getTotalElements() == 0)
//            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//        return new ResponseEntity<>(tascaList, HttpStatus.OK);
//    }
//
//    @PostMapping(consumes = { "application/json" })
//    public ResponseEntity<Void> createTascaV1(
//            @Valid @RequestBody TascaDto tascaDto) {
//
//        log.debug("[CTR] create tasca: " + tascaDto.toString());
//        TascaDto savedDto;
//
//        if (tascaDto.getEntornId() == null) {
//            log.error("[CTR] Create: El camp Tasca.entornId és obligatori");
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El camp Tasca.entornId és obligatori");
//        }
//        try {
//            savedDto = tascaService.createTasca(tascaDto);
//        } catch (DataIntegrityViolationException ex) {
//            log.error("[CTR] Create: Ja existeix un tasca amb el mateix entorn, tipus d'tasca i codi", ex);
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ja existeix un domini amb el mateix entorn, tipus d'tasca i codi");
//        }
//
//        HttpHeaders httpHeaders = new HttpHeaders();
//        httpHeaders.add("Location", API_PATH + "/" + savedDto.getId());
//        return new ResponseEntity<>(httpHeaders, HttpStatus.CREATED);
//
//    }
//
//    @PutMapping(value = "/{tascaId}", consumes = { "application/json" })
//    public ResponseEntity<Void> updateTascaV1(
//            @PathVariable("tascaId") Long tascaId,
//            @Valid @RequestBody TascaDto tascaDto) {
//
//        log.debug("[CTR] update tasca: " + tascaDto.toString());
//
//        try {
//            tascaService.updateTasca(
//                    tascaId,
//                    tascaDto);
//        } catch (DataIntegrityViolationException ex) {
//            log.error("[CTR] Update: Ja existeix un tasca amb el mateix entorn, tipus d'tasca i codi", ex);
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ja existeix un domini amb el mateix entorn, tipus d'tasca i codi");
//        }
//        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//
//    }
//
//    @PatchMapping(value = "/{tascaId}", consumes = { "application/json" })
//    public ResponseEntity<Void> patchDominiV1(
////            HttpServletRequest request,
//            @PathVariable("tascaId") Long tascaId,
//            @RequestBody JsonNode tascaJson,
//            BindingResult bindingResult) {
//
//        log.debug("[CTR] patch tasca: " + tascaId);
//
//        Patch patch = new JsonPatchPatchConverter(objectMapper).convert(tascaJson);
//        TascaDto tascaDto = tascaService.getById(tascaId);
//        TascaDto patchedTascaDto = patch.apply(
//                tascaDto,
//                TascaDto.class
//        );
//        smartValidator.validate(patchedTascaDto, bindingResult);
//        if (bindingResult.hasErrors()) {
//            throw new ResponseStatusException(HttpStatus.CONFLICT, ControllerHelper.getValidationErrorMessage(bindingResult));
//        } else {
//            tascaService.updateTasca(
//                    tascaId,
//                    patchedTascaDto);
//        }
//
//        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//    }
//
//    @DeleteMapping(value = "/{tascaId}")
//    public ResponseEntity<Void> deleteDominiV1(
//            @PathVariable("tascaId") Long tascaId) {
//
//        log.debug("[CTR] delete tasca: " + tascaId);
//
//        tascaService.delete(tascaId);
//        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//
//    }
//
//    @GetMapping(value = "/{tascaId}")
//    public ResponseEntity<TascaDto> getTascaV1(
//            @PathVariable("tascaId") Long tascaId) {
//
//        log.debug("[CTR] get tasca: " + tascaId);
//        return new ResponseEntity<>(tascaService.getById(tascaId), HttpStatus.OK);
//
//    }
//    
}
