package es.caib.helium.camunda.controller;

import es.caib.helium.camunda.service.AreaCarrecService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping(ActionController.API_PATH)
public class AreaCarrecController {

    public static final String API_PATH = "/api/v1";

    private final AreaCarrecService areaCarrecService;

    // TODO: Implementar àrees i càrrecs

    // AREES I CARRECS
    ////////////////////////////////////////////////////////////////////////////////
    @GetMapping(value = "/arees/byFiltre/{filtre}")
    public ResponseEntity<List<String>> findAreesByFiltre(
            @PathVariable("filtre") String filtre) {
        return new ResponseEntity<>(
                areaCarrecService.findAreesByFiltre(filtre),
                HttpStatus.OK);
    }

    @GetMapping(value = "/arees/{personaCodi}")
    public ResponseEntity<List<String>> findAreesByPersona(
            @PathVariable("personaCodi") String personaCodi) {
        return new ResponseEntity<>(
                areaCarrecService.findAreesByPersona(personaCodi),
                HttpStatus.OK);
    }

    @GetMapping(value = "/rols/{personaCodi}")
    public ResponseEntity<List<String>> findRolsByPersona(
            @PathVariable("personaCodi") String personaCodi) {
        return new ResponseEntity<>(
                areaCarrecService.findRolsByPersona(personaCodi),
                HttpStatus.OK);
    }

    @GetMapping(value = "/carrecs/byFiltre/{filtre}")
    public ResponseEntity<List<String[]>> findCarrecsByFiltre(
            @PathVariable("filtre") String filtre) {
        return new ResponseEntity<>(
                areaCarrecService.findCarrecsByFiltre(filtre),
                HttpStatus.OK);
    }

    @GetMapping(value = "/persones/{grupCodi}/{carrecCodi)")
    public ResponseEntity<List<String>> findPersonesByGrupAndCarrec(
            @PathVariable("grupCodi") String grupCodi,
            @PathVariable("carrecCodi") String carrecCodi) {
        return new ResponseEntity<>(
                areaCarrecService.findPersonesByGrupAndCarrec(grupCodi, carrecCodi),
                HttpStatus.OK);
    }

    @GetMapping(value = "/carrecs/{grupCodi}/{personaCodi}")
    public ResponseEntity<List<String>> findCarrecsByPersonaAndGrup(
            @PathVariable("personaCodi") String personaCodi,
            @PathVariable("grupCodi") String grupCodi) {
        return new ResponseEntity<>(
                areaCarrecService.findCarrecsByPersonaAndGrup(personaCodi, grupCodi),
                HttpStatus.OK);
    }

    @GetMapping(value = "/persones/byCarrec/{carrecCodi}")
    public ResponseEntity<List<String>> findPersonesByCarrec(
            @PathVariable("carrecCodi") String carrecCodi) {
        return new ResponseEntity<>(
                areaCarrecService.findPersonesByCarrec(carrecCodi),
                HttpStatus.OK);
    }

    @GetMapping(value = "/persones/byGrup/{grupCodi}")
    public ResponseEntity<List<String>> findPersonesByGrup(
            @PathVariable("grupCodi") String grupCodi) {
        return new ResponseEntity<>(
                areaCarrecService.findPersonesByGrup(grupCodi),
                HttpStatus.OK);
    }

}