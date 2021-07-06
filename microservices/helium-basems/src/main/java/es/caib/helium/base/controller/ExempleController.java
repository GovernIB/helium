package es.caib.helium.base.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping(ExempleController.API_PATH)
public class ExempleController {

    public static final String API_PATH = "/api/v1/bases";

//    private final BaseService baseService;
//    private final ObjectMapper objectMapper;
//    private final HttpServletRequest request;
//    private final SmartValidator smartValidator;
//
//    // Exemple de CRUD
//
//    @GetMapping(produces = { "application/json" })
//    public ResponseEntity<PagedList> listBasesV1(
//            @RequestParam(value = "filtre", required = false) String filtre,
//            final Pageable pageable,
//            final Sort sort) {
//
//        PagedList<ExempleDto> baseList = baseService.listBases(
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
//            @Valid @RequestBody ExempleDto baseDto) {
//
//        if (baseDto.getCodi() == null) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El camp Base.codi és obligatori");
//        }
//        ExempleDto savedDto = baseService.createBase(baseDto);
//        HttpHeaders httpHeaders = new HttpHeaders();
//        httpHeaders.add("Location", API_PATH + "/" + savedDto.getId());
//        return new ResponseEntity<>(HttpStatus.CREATED);
//
//    }
//
//    @PutMapping(value = "/{baseId}", consumes = { "application/json" })
//    public ResponseEntity<Void> updateBaseV1(
//            @PathVariable("baseId") Long baseId,
//            @Valid @RequestBody ExempleDto baseDto) {
//
//        baseService.updateBase(baseId, baseDto);
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
//        ExempleDto baseDto = baseService.getById(baseId);
//        ExempleDto patchedBaseDto = patch.apply(baseDto, ExempleDto.class);
//        smartValidator.validate(patchedBaseDto, bindingResult);
//        if (bindingResult.hasErrors()) {
//            throw new ResponseStatusException(HttpStatus.CONFLICT, ControllerHelper.getValidationErrorMessage(bindingResult));
//        } 
//        
//        baseService.updateBase(baseId, patchedBaseDto);
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
//    public ResponseEntity<ExempleDto> getBaseV1(
//            @PathVariable("baseId") Long baseId) {
//
//        return new ResponseEntity<>(baseService.getById(baseId), HttpStatus.OK);
//
//    }

}
