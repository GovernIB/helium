package es.caib.helium.camunda.controller;

import es.caib.helium.camunda.service.TimerService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping(TimerController.API_PATH)
public class TimerController {

    public static final String API_PATH = "/api/v1/timers";

    private final TimerService timerService;

    @PostMapping(value="/{timerId}/suspend")
    public ResponseEntity<Void> suspendTimer(
            @PathVariable("timerId") String timerId,
            @RequestBody Date dueDate) {
        timerService.suspendTimer(timerId, dueDate);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value="/{timerId}/resume")
    public ResponseEntity<Void> resumeTimer(
            @PathVariable("timerId") String timerId,
            @RequestBody Date dueDate) {
        timerService.resumeTimer(timerId, dueDate);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}