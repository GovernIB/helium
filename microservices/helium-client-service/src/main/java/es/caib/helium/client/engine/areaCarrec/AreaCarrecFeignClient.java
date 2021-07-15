package es.caib.helium.client.engine.areaCarrec;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

public interface AreaCarrecFeignClient {

    @RequestMapping(method = RequestMethod.GET, value = AreaCarrecApiPath.FIND_AREES_BY_FILTRE)
    ResponseEntity<List<String>> findAreesByFiltre(@PathVariable("filtre") String filtre);

    @RequestMapping(method = RequestMethod.GET, value = AreaCarrecApiPath.FIND_AREES_BY_PERSONA)
    ResponseEntity<List<String>> findAreesByPersona(@PathVariable("personaCodi") String personaCodi);

    @RequestMapping(method = RequestMethod.GET, value = AreaCarrecApiPath.FIND_ROLS_BY_PERSONA)
    ResponseEntity<List<String>> findRolsByPersona(@PathVariable("personaCodi") String personaCodi);

    @RequestMapping(method = RequestMethod.GET, value = AreaCarrecApiPath.FIND_CARRECS_BY_FILTRE)
    ResponseEntity<List<String[]>> findCarrecsByFiltre(@PathVariable("filtre") String filtre);

    @RequestMapping(method = RequestMethod.GET, value = AreaCarrecApiPath.FIND_PERSONES_BY_GRUP_AND_CARREC)
    ResponseEntity<List<String>> findPersonesByGrupAndCarrec(
            @PathVariable("grupCodi") String grupCodi,
            @PathVariable("carrecCodi") String carrecCodi);

    @RequestMapping(method = RequestMethod.GET, value = AreaCarrecApiPath.FIND_CARRECS_BY_PERSONA_AND_GRUP)
    ResponseEntity<List<String>> findCarrecsByPersonaAndGrup(
            @PathVariable("personaCodi") String personaCodi,
            @PathVariable("grupCodi") String grupCodi);

    @RequestMapping(method = RequestMethod.GET, value = AreaCarrecApiPath.FIND_PERSONES_BY_CARREC)
    ResponseEntity<List<String>> findPersonesByCarrec(@PathVariable("carrecCodi") String carrecCodi);

    @RequestMapping(method = RequestMethod.GET, value = AreaCarrecApiPath.FIND_PERSONES_BY_GRUP)
    ResponseEntity<List<String>> findPersonesByGrup(@PathVariable("grupCodi") String grupCodi);
}
