package es.caib.helium.camunda.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.ProcessEngineService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping(IndexController.API_PATH)
public class IndexController {

    private final ProcessEngineService processEngineService;

    public static final String API_PATH = "/api/v1";

    @GetMapping(value = "/test")
    public ResponseEntity<String> test() {
        return new ResponseEntity<>(
                "Helium Camunda started.",
                HttpStatus.OK);
    }

    @GetMapping(value = "/engines")
    public ResponseEntity<List<String>> engines() {
        List<String> motorNames = new ArrayList<>();
        var motors = processEngineService.getProcessEngines();
        if (motors == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(
                motors.stream().map(m -> m.getName()).collect(Collectors.toList()),
                HttpStatus.OK);
    }

}
