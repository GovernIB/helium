package es.caib.helium.base.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.caib.helium.base.model.BaseDto;
import es.caib.helium.base.model.PagedList;
import es.caib.helium.base.service.BaseService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.rest.webmvc.json.patch.JsonPatchPatchConverter;
import org.springframework.data.rest.webmvc.json.patch.Patch;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.SmartValidator;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping(BaseController.API_PATH)
public class BaseController {

    public static final String API_PATH = "/api/v1/bases";

    private final BaseService baseService;
    private final ObjectMapper objectMapper;
    private final HttpServletRequest request;
    private final SmartValidator smartValidator;

    // Exemple de CRUD

    @GetMapping(produces = { "application/json" })
    public ResponseEntity<PagedList> listBasesV1(
            @RequestParam(value = "filtre", required = false) String filtre,
            final Pageable pageable,
            final Sort sort) {

        PagedList<BaseDto> baseList = baseService.listBases(
                filtre,
                pageable,
                sort);
        if (baseList.getTotalElements() == 0)
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<PagedList>(baseList, HttpStatus.OK);
    }

    @PostMapping(consumes = { "application/json" })
    public ResponseEntity<Void> createBaseV1(
            @Valid @RequestBody BaseDto baseDto) {

        if (baseDto.getCodi() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El camp Base.codi és obligatori");
        BaseDto savedDto = baseService.createBase(baseDto);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Location", API_PATH + "/" + savedDto.getId());
        return new ResponseEntity<>(HttpStatus.CREATED);

    }

    @PutMapping(value = "/{baseId}", consumes = { "application/json" })
    public ResponseEntity<Void> updateBaseV1(
            @PathVariable("baseId") Long baseId,
            @Valid @RequestBody BaseDto baseDto) {

        baseService.updateBase(
                baseId,
                baseDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }

    @PatchMapping(value = "/{baseId}", consumes = { "application/json" })
    public ResponseEntity<Void> patchBaseV1(
            HttpServletRequest request,
            @PathVariable("baseId") Long baseId,
            @RequestBody JsonNode baseJson,
            BindingResult bindingResult) {
//        String accept = request.getHeader("Accept");

        Patch patch = new JsonPatchPatchConverter(objectMapper).convert(baseJson);
        BaseDto baseDto = baseService.getById(baseId);
        BaseDto patchedBaseDto = patch.apply(
                baseDto,
                BaseDto.class
        );
        smartValidator.validate(patchedBaseDto, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, ControllerHelper.getValidationErrorMessage(bindingResult));
        } else {
            baseService.updateBase(
                    baseId,
                    patchedBaseDto);
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(value = "/{baseId}")
    public ResponseEntity<Void> deleteBaseV1(
            @PathVariable("baseId") Long baseId) {

        baseService.delete(baseId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }

    @GetMapping(value = "/{baseId}")
    public ResponseEntity<BaseDto> getBaseV1(
            @PathVariable("baseId") Long baseId) {

        return new ResponseEntity<>(baseService.getById(baseId), HttpStatus.OK);

    }

}
